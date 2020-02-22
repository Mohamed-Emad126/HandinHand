package com.example.handinhand.Models;

public class ResetPasswordResponse {

    private String error;
    private boolean status;
    private Reset_password reset_password;

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

    public Reset_password getReset_password() {
        return reset_password;
    }

    public void setReset_password(Reset_password reset_password) {
        this.reset_password = reset_password;
    }
}
