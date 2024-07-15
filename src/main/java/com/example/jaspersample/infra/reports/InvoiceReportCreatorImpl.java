package com.example.jaspersample.infra.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ResourceUtils;

import com.example.fw.common.reports.AbstractJasperReportCreator;
import com.example.fw.common.reports.ReportCreator;
import com.example.jaspersample.domain.model.Order;
import com.example.jaspersample.domain.reports.BillingReportCreator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * InvoiceReportCreatorの実装クラス<br>
 * 
 * 帳票出力のフレームワーク機能を利用して実装している
 */
@ReportCreator
public class InvoiceReportCreatorImpl extends AbstractJasperReportCreator<Order> implements BillingReportCreator {
	private static final String JRXML_FILE_PATH = "classpath:reports/invoice-report.jrxml";	

	@Override
	public InputStream createInvoice(Order order) {
		return createPDFReport(order);
	}

	@Override
	protected File getJRXMLFile() throws FileNotFoundException {
		return ResourceUtils.getFile(JRXML_FILE_PATH);
	}

	@Override
	protected Map<String, Object> getParameters(Order data) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("orderId", data.getId());		
		parameters.put("customerZip", data.getCustomer().getZip());
		parameters.put("customerAddress", data.getCustomer().getAddress());
		parameters.put("customerName", data.getCustomer().getName());
		//TODO: 合計金額を計算
		parameters.put("totalAmount", 27510000);
		parameters.put("billingSourceName", data.getBillingSource().getName());
		parameters.put("billingSourceZip", data.getBillingSource().getZip());
		parameters.put("billingSourceAddress", data.getBillingSource().getAddress());
		parameters.put("billingSourceTel", data.getBillingSource().getTel());
		parameters.put("billingSourceManager", data.getBillingSource().getManager());
		return parameters;
	}

	@Override
	protected JRDataSource getDataSource(Order data) {
		// 注文明細を設定
		return new JRBeanCollectionDataSource(data.getOrderItems());
	}

}
