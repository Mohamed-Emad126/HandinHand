package com.example.handinhand.Models;

public class AddItemResponse {

    private String error;
    private boolean status;
    private String create_item;

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

    public String getCreate_item() {
        return create_item;
    }

    public void setCreate_item(String create_item) {
        this.create_item = create_item;
    }
}
