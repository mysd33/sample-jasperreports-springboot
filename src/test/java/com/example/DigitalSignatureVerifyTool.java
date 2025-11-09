package com.example;

import java.io.StringWriter;

import eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.simplereport.jaxb.ObjectFactory;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * デジタル署名検証ツールのテストクラス
 */
class DigitalSignatureVerifyTool {

    public static void main(String[] args) throws JAXBException {
        String filePath = "pdf/請求書_PAdES_可視署名_ECDSA.pdf";
        String password = "1234";
        // 検証対象署名済 PDFファイル の読み込み
        DSSDocument signedDocument = new FileDocument(filePath);

        // PDF 用検証オブジェクトの生成
        PDFDocumentValidator documentValidator = new PDFDocumentValidator(signedDocument);
        documentValidator.setCertificateVerifier(new CommonCertificateVerifier());
        documentValidator.setPasswordProtection(password.toCharArray());

        // 検証を実行
        Reports reports = documentValidator.validateDocument();

        // https://github.com/esig/dss/blob/master/dss-cookbook/src/main/asciidoc/_chapters/signature-validation.adoc#13-signature-validation-and-reports
        // 結果を取得・表示
        // 出力：Simple Report
        XmlSimpleReport xmlSimple = reports.getSimpleReport().getJaxbModel();
        ObjectFactory factory = new ObjectFactory(); // パッケージ eu.europa.esig.dss.simplereport.jaxb
        JAXBElement<XmlSimpleReport> root = factory.createSimpleReport(xmlSimple);
        JAXBContext ctxSimple = JAXBContext.newInstance(XmlSimpleReport.class);
        Marshaller mSimple = ctxSimple.createMarshaller();
        mSimple.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        mSimple.marshal(root, sw);
        System.out.println("=== Simple Report ===\n" + sw.toString());

        // 出力：Detailed Report
        XmlDetailedReport xmlDetailed = reports.getDetailedReport().getJAXBModel();
        eu.europa.esig.dss.detailedreport.jaxb.ObjectFactory factoryDetailed = new eu.europa.esig.dss.detailedreport.jaxb.ObjectFactory();
        JAXBElement<XmlDetailedReport> rootDetailed = factoryDetailed.createDetailedReport(xmlDetailed);
        JAXBContext ctxDetailed = JAXBContext.newInstance(XmlDetailedReport.class);
        Marshaller mDetailed = ctxDetailed.createMarshaller();
        mDetailed.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter swDetailed = new StringWriter();
        mDetailed.marshal(rootDetailed, swDetailed);
        System.out.println("=== Detailed Report ===\n" + swDetailed.toString());

    }

}
