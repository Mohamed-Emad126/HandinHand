package com.example.handinhand.API;

import com.example.handinhand.Models.LoginInfo;
import com.example.handinhand.Models.LoginResponse;
import com.example.handinhand.Models.RegisterResponse;
import com.example.handinhand.Models.ResetPasswordResponse;
import com.example.handinhand.Models.SendResetPasswordEmailResponse;

import java.util.HashMap;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface MainActivityClient {


    @POST("api/login")
    Call<LoginResponse> login(
            @Body LoginInfo emailAndPassword
    );


    @Multipart
    @POST("api/register")
    Call<RegisterResponse> register(
            @PartMap Map<String, RequestBody> user,
            @Part MultipartBody.Part image
            );


    @FormUrlEncoded
    @POST("api/password/email")
    Call<SendResetPasswordEmailResponse> sendResetPasswordRequest(
            @FieldMap HashMap<String, String> email
    );


    @FormUrlEncoded
    @POST("api/password/reset")
    Call<ResetPasswordResponse> resetPassword(
            @FieldMap HashMap<String, String> info
    );



}
