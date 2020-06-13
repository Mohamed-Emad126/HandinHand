package com.example.handinhand.Models;

public class ServiceInterestResponse {

    private String error;
    private boolean status;
    private String service_interest;

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

    public String getService_interest() {
        return service_interest;
    }

    public void setService_interest(String service_interest) {
        this.service_interest = service_interest;
    }
}
