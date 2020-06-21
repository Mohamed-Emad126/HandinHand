package com.example.handinhand.Models;

public class AskForVerificationResponse {

    private String error;
    private boolean status;
    private String create_validation;

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

    public String getCreate_validation() {
        return create_validation;
    }

    public void setCreate_validation(String create_validation) {
        this.create_validation = create_validation;
    }
}
