package com.example.handinhand.Models;

public class ApiErrors {
    private Error error;
    private String register;
    private boolean status;

    public String getRegister() {
        return register;
    }

    public boolean isStatus() {
        return status;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
    //TODO: create the model from json


}
