package com.example.jaspersample.domain.reports;

import java.io.InputStream;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceReportCSVData implements AutoCloseable {
    private InputStream inputStream;
    private String pdfPassword;

    @Override
    public void close() throws Exception {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
