package com.example.handinhand.Models;

public class AddEventResponse {

    private String error;
    private boolean status;
    private String create_event;

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

    public String getCreate_event() {
        return create_event;
    }

    public void setCreate_event(String create_event) {
        this.create_event = create_event;
    }
}
