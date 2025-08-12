package com.example.fw.common.digitalsignature;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.reports.DefaultReport;
import com.example.fw.common.reports.Report;
import com.example.fw.common.reports.ReportsConstants;
import com.example.fw.common.reports.config.ReportsConfigurationProperties;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PKCS#12形式のキーストアを使用してPDFに基本的な電子署名を付与するクラス
 * 
 * このクラスは、PKCS#12形式のキーストアから秘密鍵と証明書を読み込み、PDFに電子署名を付与します。
 * 
 * @see ReportSigner
 */
@Slf4j
@RequiredArgsConstructor
public class PKCS12BasicReportSigner implements ReportSigner {
    private static final String PKCS12 = "pkcs12";
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final ReportsConfigurationProperties config;
    private final SignatureOptions options;
    // PDFの一時保存ファイルのディレクトリ（パスを初期化設定後、定期削除のための別スレッドで参照されるためAtomicReferenceにしておく）
    private final AtomicReference<Path> pdfTempPath = new AtomicReference<>();

    /**
     * 初期化処理
     * 
     */
    @PostConstruct
    public void init() {
        // 帳票を一時保存する一時ディレクトリを作成する
        pdfTempPath.set(Path.of(ReportsConstants.TMP_DIR, config.getReportTmpdir()));
        appLogger.debug("pdfTempPath: {}", pdfTempPath);
        // 一時ディレクトリが存在しない場合は作成する
        pdfTempPath.get().toFile().mkdirs();
    }

    @Override
    public Report sign(Report originalReport) {
        // デフォルトの署名処理としてOpenPDFを使用して、PDFに電子署名を付与する実装例
        // https://javadoc.io/doc/com.github.librepdf/openpdf/1.3.43/com/lowagie/text/pdf/PdfStamper.html#createSignature-com.lowagie.text.pdf.PdfReader-java.io.OutputStream-char-
        PdfReader originalPdfReader = null;
        try {
            originalPdfReader = new PdfReader(originalReport.getInputStream());
        } catch (IOException e) {
            // TODO: 適切な例外、メッセージをスローする
            throw new RuntimeException("Failed Read PDF", e);
        }
        // メモリを極力使わないよう、PDFのファイルサイズが大きい場合も考慮し一時ファイルに出力してInputStreamで返却するようにする
        Path signedPdfTempFilePath = null;
        try {
            signedPdfTempFilePath = Files.createTempFile(pdfTempPath.get(), ReportsConstants.PDF_TEMP_FILE_PREFIX,
                    ReportsConstants.PDF_FILE_EXTENSION);
        } catch (IOException e) {
            // TODO: 適切な例外、メッセージをスローする
            throw new RuntimeException("Failed Create File", e);
        } finally {
            originalPdfReader.close();
        }
        try (FileOutputStream fos = new FileOutputStream(signedPdfTempFilePath.toFile())) {
            KeyStore ks = KeyStore.getInstance(PKCS12);
            ks.load(new FileInputStream(options.getKeyStoreFile()), options.getPassword().toCharArray());
            String alias = ks.aliases().nextElement();
            PrivateKey key = (PrivateKey) ks.getKey(alias, options.getPassword().toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);
            PdfStamper pdfStamper = PdfStamper.createSignature(originalPdfReader, fos, '\0');
            PdfSignatureAppearance sap = pdfStamper.getSignatureAppearance();
            // TODO: Optionsでの設定切り出し
            sap.setReason("署名理由");
            sap.setLocation("署名場所");
            if (options.isVisible()) {
                sap.setVisibleSignature(new Rectangle(100, 100, 200, 200), 1);
                // 可視署名の設定切り出し
                sap.setLayer2Text("署名者");
                String imagePath = options.getStampImagePath();
                sap.setImage(Image.getInstance(imagePath));
            }
            pdfStamper.setEnforcedModificationDate(Calendar.getInstance());

            // デフォルト実装はハッシュアルゴリズムがSHA-1と表示されてしまうため
            // ハッシュアルゴリズムをSHA-256の明示的な設定の上書きのため拡張をする
            DefaultPdfSignature sig = new DefaultPdfSignature();
            sig.setSignInfo(key, chain, null);
            sig.put(PdfName.M, new PdfDate(new GregorianCalendar()));
            sap.setCryptoDictionary(sig);
            
            // 通常なら、PdfStamperをclose()は呼び出すが、
            // close()メソッド内で、sap.preClose()で、NullPointerExceptionが発生するため置き換え

            // TODO: リファクタリング（メソッド切り出しまたはPdfStamperの拡張クラスへの変更）
            PdfString contents = (PdfString) sig.get(PdfName.CONTENTS);
            PdfLiteral lit = new PdfLiteral(
                    (contents.toString().length() + (PdfName.ADOBE_PPKLITE.equals(sap.getFilter()) ? 0 : 64)) * 2 + 2);
            Map<PdfName, Integer> exclusionSize = Map.of(PdfName.CONTENTS, lit.getPosLength());
            sap.preClose(exclusionSize);
            int totalBuf = (lit.getPosLength() - 2) / 2;
            byte[] buf = new byte[8192];
            int n;
            InputStream inp = sap.getRangeStream();
            try {
                while ((n = inp.read(buf)) > 0) {
                    sig.getSigner().update(buf, 0, n);
                }
            } catch (SignatureException se) {
                throw new ExceptionConverter(se);
            }
            buf = new byte[totalBuf];
            byte[] bsig = sig.getSignerContents();
            System.arraycopy(bsig, 0, buf, 0, bsig.length);
            PdfString str = new PdfString(buf);
            str.setHexWriting(true);
            PdfDictionary dic = new PdfDictionary();
            dic.put(PdfName.CONTENTS, str);
            sap.close(dic);
            pdfStamper.getReader().close();

            return DefaultReport.builder()//
                    .file(signedPdfTempFilePath.toFile()).build();
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException
                | DocumentException | IOException e) {
            // TODO: 適切な例外、メッセージをスローする
            throw new RuntimeException("Failed to sign the report", e);
        } finally {
            originalPdfReader.close();
        }

    }
}
