package com.example.fw.common.digitalsignature;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfSigGenericPKCS;

public class DefaultPdfSignature extends PdfSigGenericPKCS {

    public DefaultPdfSignature() {
        // https://www.antenna.co.jp/pdf/reference/PDFSingature.html
        // ISO 32000-1では、SubFilterとしてadbe.pkcs7.detachedを設定することが推奨されている。
        super(PdfName.ADOBE_PPKMS, PdfName.ADBE_PKCS7_DETACHED);
        hashAlgorithm = "SHA256";
    }

}
