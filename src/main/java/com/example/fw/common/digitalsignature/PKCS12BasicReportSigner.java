package com.example.fw.common.digitalsignature;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.reports.DefaultReport;
import com.example.fw.common.reports.Report;
import com.example.fw.common.reports.ReportsConstants;
import com.example.fw.common.reports.config.ReportsConfigurationProperties;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

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
            //TODO: 適切な例外、メッセージをスローする
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
        //TODO: ハッシュアルゴリズムがSHA-1と表示されてしまうが、SHA-256に変更する方法が不明
        try (FileOutputStream fos = new FileOutputStream(signedPdfTempFilePath.toFile())) {
            KeyStore ks = KeyStore.getInstance(PKCS12);
            ks.load(new FileInputStream(options.getKeyStoreFile()), options.getPassword().toCharArray());
            String alias = ks.aliases().nextElement();
            PrivateKey key = (PrivateKey) ks.getKey(alias, options.getPassword().toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);
            PdfStamper pdfStamper = PdfStamper.createSignature(originalPdfReader, fos, '\0'); 
            PdfSignatureAppearance sap = pdfStamper.getSignatureAppearance();
            // TODO: 証明書チェーンの設定切り替え
            //sap.setCrypto(key, chain, null, PdfSignatureAppearance.SELF_SIGNED);
            sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
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
            pdfStamper.close();
            
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
