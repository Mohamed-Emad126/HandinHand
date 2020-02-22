package com.example.handinhand.Models;

public class SendResetPasswordEmailResponse {

    private String error;
    private boolean status;
    private Send_reset_password_email send_reset_password_email;

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

    public Send_reset_password_email getSend_reset_password_email() {
        return send_reset_password_email;
    }

    public void setSend_reset_password_email(Send_reset_password_email send_reset_password_email) {
        this.send_reset_password_email = send_reset_password_email;
    }
}
