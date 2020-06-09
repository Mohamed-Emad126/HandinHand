package com.example.handinhand.Models;

public class ItemRequestResponse {

    private String error;
    private boolean status;
    private String item_deal;

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

    public String getItem_deal() {
        return item_deal;
    }

    public void setItem_deal(String item_deal) {
        this.item_deal = item_deal;
    }
}
