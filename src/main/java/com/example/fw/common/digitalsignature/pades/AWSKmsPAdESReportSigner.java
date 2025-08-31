package com.example.fw.common.digitalsignature.pades;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.util.Assert;

import com.example.fw.common.digitalsignature.ReportSigner;
import com.example.fw.common.digitalsignature.config.DigitalSignatureConfigurationProperties;
import com.example.fw.common.keymanagement.Certificate;
import com.example.fw.common.keymanagement.KeyInfo;
import com.example.fw.common.keymanagement.KeyManager;
import com.example.fw.common.logging.ApplicationLogger;
import com.example.fw.common.logging.LoggerFactory;
import com.example.fw.common.reports.DefaultReport;
import com.example.fw.common.reports.Report;
import com.example.fw.common.reports.ReportsConstants;
import com.example.fw.common.reports.config.ReportsConfigurationProperties;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AWS KMSを使用してPDFにPAdES形式の電子署名を付与するクラス
 * 
 * このクラスは、AWS KMSを使用してPDFにPAdES形式の電子署名を付与します。
 * 
 * @see ReportSigner
 */
@Slf4j
@RequiredArgsConstructor
public class AWSKmsPAdESReportSigner implements ReportSigner {
    private static final ApplicationLogger appLogger = LoggerFactory.getApplicationLogger(log);
    private final KeyManager keyManager;
    private final DigitalSignatureConfigurationProperties digitalSignatureConfigurationProperties;
    private final ReportsConfigurationProperties reportsConfigurationProperties;
    // PDFの一時保存ファイルのディレクトリ（パスを初期化設定後、定期削除のための別スレッドで参照されるためAtomicReferenceにしておく）
    private final AtomicReference<Path> pdfTempPath = new AtomicReference<>();
    // 電子署名に使用するキーID
    private String keyId;
    // 電子署名に使用する証明書
    private Certificate certificate;

    @PostConstruct
    public void init() {
        // 帳票を一時保存する一時ディレクトリを作成する
        pdfTempPath.set(Path.of(ReportsConstants.TMP_DIR, reportsConfigurationProperties.getReportTmpdir()));
        appLogger.debug("pdfTempPath: {}", pdfTempPath);
        // 一時ディレクトリが存在しない場合は作成する
        pdfTempPath.get().toFile().mkdirs();
        keyId = digitalSignatureConfigurationProperties.getAwsKms().getKeyId();
        // 証明書の取得
        certificate = keyManager.getCertificateFromObjectStorage(KeyInfo.builder().keyId(keyId).build());
    }

    @Override
    public Report sign(Report report) {
        try (AWSKmsSignatureToken token = new AWSKmsSignatureToken(keyManager, digitalSignatureConfigurationProperties,
                keyId)) {
            DSSDocument toSignDocument = null;
            if (report instanceof DefaultReport defaultReport) {
                // Fileに対して電子署名付与を実装
                toSignDocument = new FileDocument(defaultReport.getFile());
            } else {
                // InMemoryDocumentに対して電子署名付与を実装
                toSignDocument = new InMemoryDocument(report.getInputStream());
            }

            // 証明書検証機能を初期化
            CertificateVerifier certificateVerifier = new CommonCertificateVerifier();

            // PAdES署名サービスを作成
            PAdESService padesService = new PAdESService(certificateVerifier);

            // der形式でエンコードされた証明書データを設定
            X509Certificate x509Certificate;
            try {
                x509Certificate = certificate.getX509Certificate();
            } catch (CertificateException | IOException e) {
                // TODO: 適切な例外処理を実装する
                throw new RuntimeException("Failed to get X509Certificate from Certificate", e);
            }

            // 証明書の有効性を確認
            validateCertificate(x509Certificate);

            // PAdESSignatureの署名パラメータを作成
            PAdESSignatureParameters signatureParameters = createSignatureParameters(x509Certificate);

            // 署名対象のハッシュ値を計算
            ToBeSigned dataToSign = padesService.getDataToSign(toSignDocument, signatureParameters);

            // TODO: 引数がダミーのため、ちゃんとした実装にする
            SignatureValue signatureValue = token.sign(dataToSign,
                    digitalSignatureConfigurationProperties.getSignatureAlgorithm(), // TODO:現状ダミー
                    null); // TODO: 現状ダミー
            // 署名をPDFに適用
            DSSDocument signedDocument = padesService.signDocument(toSignDocument, //
                    signatureParameters, signatureValue);
            Path tempFielPath;
            try {
                tempFielPath = Files.createTempFile(pdfTempPath.get(), ReportsConstants.PDF_TEMP_FILE_PREFIX,
                        ReportsConstants.PDF_FILE_EXTENSION);

                try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(tempFielPath))) {
                    signedDocument.writeTo(bos);
                }
                File file = tempFielPath.toFile();
                return DefaultReport.builder().file(file).build();
            } catch (IOException e) {
                // TODO：適切な例外処理を実装する
                throw new RuntimeException("Failed to sign the report", e);
            }
        }
    }

    /**
     * 証明書の有効性を検証する
     * 
     * @param certificate 検証対象の証明書
     */
    private void validateCertificate(X509Certificate certificate) {
        // TODO: 適切な例外、メッセージをスローする
        Assert.notNull(certificate, "Certificate must not be null");
        try {
            // 証明書の有効期限を確認
            certificate.checkValidity();
        } catch (CertificateExpiredException e) {
            // TODO: 適切な例外、メッセージをスローする
            throw new RuntimeException("Certificate has expired", e);
        } catch (CertificateNotYetValidException e) {
            // TODO: 適切な例外、メッセージをスローする
            throw new RuntimeException("Certificate is not yet valid", e);
        }

    }

    /**
     * PAdESSignatureParametersを作成する
     * 
     * @param privateKey 署名に使用する秘密鍵
     * @return PAdESSignatureParameters
     */
    private PAdESSignatureParameters createSignatureParameters(X509Certificate x509Certificate) {
        PAdESSignatureParameters pAdESSignatureParameters = new PAdESSignatureParameters();
        pAdESSignatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
        pAdESSignatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);
        pAdESSignatureParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
        CertificateToken certificateToken = new CertificateToken(x509Certificate);
        pAdESSignatureParameters.setSigningCertificate(certificateToken);
        // TODO
        // pAdESSignatureParameters.setCertificateChain(certificateToken);
        return pAdESSignatureParameters;
    }
}
