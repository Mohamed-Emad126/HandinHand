package com.example.handinhand.Models;

public class EmailVerificationResponse {

    private String error;
    private boolean status;
    private Email_verification email_verification;

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

    public Email_verification getEmail_verification() {
        return email_verification;
    }

    public void setEmail_verification(Email_verification email_verification) {
        this.email_verification = email_verification;
    }

    public static class Email_verification {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
