package com.example.handinhand.API;

import com.example.handinhand.Models.NotificationResponse;
import com.example.handinhand.Models.SpecificNotification;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationClient {
    @GET("api/notifications")
    Call<NotificationResponse> getNotifications(
            @Header("Authorization") String token,
            @Query("page") int page
    );

    @GET("api/notifications/{notification_id}")
    Call<SpecificNotification> specificNotification(
            @Header("Authorization") String token,
            @Path("notification_id") String id
    );


}
