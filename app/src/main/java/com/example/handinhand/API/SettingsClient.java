package com.example.handinhand.API;

import com.example.handinhand.Models.AskForVerificationResponse;
import com.example.handinhand.Models.EmailVerificationResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface SettingsClient {

    @GET("api/email/resend")
    Call<EmailVerificationResponse> verifyEmail(
            @Header("Authorization") String token
    );

    @Multipart
    @POST("api/items")
    Call<AskForVerificationResponse> askForTrustedAccount(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> item,
            @Part MultipartBody.Part image
    );
}
