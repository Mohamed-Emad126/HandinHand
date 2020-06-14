package com.example.handinhand.Models;

public class ProductReportResponse {

    private String error;
    private boolean status;
    private String product_report;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getProduct_report() {
        return product_report;
    }

    public void setProduct_report(String product_report) {
        this.product_report = product_report;
    }
}
