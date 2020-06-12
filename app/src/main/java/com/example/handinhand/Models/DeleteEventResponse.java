package com.example.handinhand.Models;

public class DeleteEventResponse {

    private String error;
    private boolean status;
    private String event_delete;

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

    public String getEvent_delete() {
        return event_delete;
    }

    public void setEvent_delete(String event_delete) {
        this.event_delete = event_delete;
    }
}
