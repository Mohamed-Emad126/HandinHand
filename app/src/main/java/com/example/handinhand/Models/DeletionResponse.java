package com.example.handinhand.Models;

public class DeletionResponse {

    private String error;
    private boolean status;
    private String item_delete;

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

    public String getItem_delete() {
        return item_delete;
    }

    public void setItem_delete(String item_delete) {
        this.item_delete = item_delete;
    }
}
