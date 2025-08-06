package com.example.fw.common.digitalsignature;

import java.util.List;

import com.example.fw.common.digitalsignature.config.DigitalSignatureConfigurationProperties;
import com.example.fw.common.keymanagement.KeyInfo;
import com.example.fw.common.keymanagement.KeyManager;
import com.example.fw.common.keymanagement.Signature;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AWSKmsSignatureToken implements SignatureTokenConnection {
    private final KeyManager keyManager;
    private final DigitalSignatureConfigurationProperties digitalSignatureConfigurationProperties;

    // TODO: 適当な実装。本来は、引数で渡されるDSSPrivateKeyEntryからkeyIdを取得する必要がある。
    private final String keyId;

    @Override
    public void close() {
        // 何もしない
    }

    @Override
    public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
        // TODO: 未実装
        throw new UnsupportedOperationException("getKeys() is not supported in AWSKmsSignatureTokenConnection");
    }

    @Override
    public SignatureValue sign(ToBeSigned toBeSigned, DigestAlgorithm digestAlgorithm, DSSPrivateKeyEntry keyEntry)
            throws DSSException {
        SignatureAlgorithm signatureAlgorithm = digitalSignatureConfigurationProperties.getSignatureAlgorithm();
        return doSign(toBeSigned.getBytes(), signatureAlgorithm);
    }

    @Override
    public SignatureValue sign(ToBeSigned toBeSigned, SignatureAlgorithm signatureAlgorithm,
            DSSPrivateKeyEntry keyEntry) throws DSSException {
        return doSign(toBeSigned.getBytes(), signatureAlgorithm);
    }

    @Override
    public SignatureValue signDigest(Digest digest, DSSPrivateKeyEntry keyEntry) throws DSSException {
        // SignatureAlgorithmを取得
        SignatureAlgorithm signatureAlgorithm = digitalSignatureConfigurationProperties.getSignatureAlgorithm();
        return doSign(digest.getValue(), signatureAlgorithm);
    }

    @Override
    public SignatureValue signDigest(Digest digest, SignatureAlgorithm signatureAlgorithm, DSSPrivateKeyEntry keyEntry)
            throws DSSException {
        return doSign(digest.getValue(), signatureAlgorithm);
    }

    private SignatureValue doSign(byte[] data, SignatureAlgorithm signatureAlgorithm) throws DSSException {
        // ダイジェストから署名を生成
        Signature signature = keyManager.createSignatureFromDigest(data, // ,
                KeyInfo.builder().keyId(keyId).build());
        return new SignatureValue(signatureAlgorithm, signature.getValue());
    }
}
