package com.example.handinhand.Models;

public class ReportResponse {

    private String error;
    private boolean status;
    private String item_report;

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

    public String getItem_report() {
        return item_report;
    }

    public void setItem_report(String item_report) {
        this.item_report = item_report;
    }
}
