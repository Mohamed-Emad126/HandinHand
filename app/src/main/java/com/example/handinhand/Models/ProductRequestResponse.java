package com.example.handinhand.Models;

public class ProductRequestResponse {

    private String error;
    private boolean status;
    private String product_deal;

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

    public String getProduct_deal() {
        return product_deal;
    }

    public void setProduct_deal(String product_deal) {
        this.product_deal = product_deal;
    }
}
