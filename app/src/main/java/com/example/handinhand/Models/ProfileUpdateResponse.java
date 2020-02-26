package com.example.handinhand.Models;

public class ProfileUpdateResponse {

    private String error;
    private boolean status;
    private String profile_update;

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

    public String getProfile_update() {
        return profile_update;
    }

    public void setProfile_update(String profile_update) {
        this.profile_update = profile_update;
    }
}
