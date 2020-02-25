package com.example.handinhand.API;

import com.example.handinhand.Models.Profile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ProfileClient {

    @GET("api/profile")
    Call<Profile> getProfile(@Header("Authorization") String token);


}
