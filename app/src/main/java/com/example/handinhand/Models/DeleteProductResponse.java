package com.example.handinhand.Models;

public class DeleteProductResponse {

    private String error;
    private boolean status;
    private String product_delete;

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

    public String getProduct_delete() {
        return product_delete;
    }

    public void setProduct_delete(String product_delete) {
        this.product_delete = product_delete;
    }
}
