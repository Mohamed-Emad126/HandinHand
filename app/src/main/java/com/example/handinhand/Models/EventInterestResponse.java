package com.example.handinhand.Models;

public class EventInterestResponse {

    private String error;
    private boolean status;
    private String event_interest;

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

    public String getEvent_interest() {
        return event_interest;
    }

    public void setEvent_interest(String event_interest) {
        this.event_interest = event_interest;
    }
}
