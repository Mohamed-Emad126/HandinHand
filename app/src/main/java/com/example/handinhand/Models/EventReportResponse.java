package com.example.handinhand.Models;

public class EventReportResponse {

    private String error;
    private boolean status;
    private String event_report;

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

    public String getEvent_report() {
        return event_report;
    }

    public void setEvent_report(String event_report) {
        this.event_report = event_report;
    }
}
