package com.example.handinhand.Models;

public class CancelItemResponse {

    private String error;
    private boolean status;
    private String item_cancel;

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

    public String getItem_cancel() {
        return item_cancel;
    }

    public void setItem_cancel(String item_cancel) {
        this.item_cancel = item_cancel;
    }
}
