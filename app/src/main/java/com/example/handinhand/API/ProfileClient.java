package com.example.handinhand.API;

import com.example.handinhand.Models.Profile;
import com.example.handinhand.Models.ProfileUpdateResponse;

import java.util.HashMap;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ProfileClient {

    @GET("api/profile")
    Call<Profile> getProfile(@Header("Authorization") String token);


    @Multipart
    @POST("api/profile/update")
    Call<ProfileUpdateResponse> updateProfileInfo(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> user
    );


    @Multipart
    @POST("api/profile/update")
    Call<ProfileUpdateResponse> updateProfileInfoWithImage(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> user,
            @Part MultipartBody.Part image
    );

}
