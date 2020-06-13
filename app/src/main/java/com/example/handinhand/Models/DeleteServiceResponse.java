package com.example.handinhand.Models;

public class DeleteServiceResponse {

    private String error;
    private boolean status;
    private String service_delete;

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

    public String getService_delete() {
        return service_delete;
    }

    public void setService_delete(String service_delete) {
        this.service_delete = service_delete;
    }
}
