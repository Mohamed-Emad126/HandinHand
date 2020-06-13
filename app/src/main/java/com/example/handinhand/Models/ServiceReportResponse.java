package com.example.handinhand.Models;

public class ServiceReportResponse {

    private String error;
    private boolean status;
    private String service_report;

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

    public String getService_report() {
        return service_report;
    }

    public void setService_report(String service_report) {
        this.service_report = service_report;
    }
}
