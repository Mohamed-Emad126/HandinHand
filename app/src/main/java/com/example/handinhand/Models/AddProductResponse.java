package com.example.handinhand.Models;

public class AddProductResponse {

    private String error;
    private boolean status;
    private String create_product;

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

    public String getCreate_product() {
        return create_product;
    }

    public void setCreate_product(String create_product) {
        this.create_product = create_product;
    }
}
