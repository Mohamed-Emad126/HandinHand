package com.example.handinhand.Models;

public class RegisterResponse {

    private String error;
    private boolean status;
    private Register Register;

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

    public Register getRegister() {
        return Register;
    }

    public void setRegister(Register Register) {
        this.Register = Register;
    }
}
