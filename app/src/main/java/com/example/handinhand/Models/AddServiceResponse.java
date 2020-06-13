package com.example.handinhand.Models;

public class AddServiceResponse {

    private String error;
    private boolean status;
    private String create_service;

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

    public String getCreate_service() {
        return create_service;
    }

    public void setCreate_service(String create_service) {
        this.create_service = create_service;
    }
}
