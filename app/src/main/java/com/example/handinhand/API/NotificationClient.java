package com.example.handinhand.API;

import com.example.handinhand.Models.NotificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface NotificationClient {
    @GET("api/notifications")
    Call<NotificationResponse> getNotifications(
            @Header("Authorization") String token,
            @Query("page") int page
    );
}
