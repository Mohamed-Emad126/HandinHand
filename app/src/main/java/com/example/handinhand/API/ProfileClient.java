package com.example.handinhand.API;

import com.example.handinhand.Models.LoginInfo;
import com.example.handinhand.Models.LoginResponse;
import com.example.handinhand.Models.Profile;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ProfileClient {

    @GET("api/profile")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @PATCH("api/profile/update")
    Call<LoginResponse> updateProfileInfo(
            @Header("Authorization") String token,
            @Body LoginInfo updatedUser
    );


    @Multipart
    @PATCH("api/profile/update")
    Call<LoginResponse> updateProfileInfoWithImage(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> user,
            @Part MultipartBody.Part image
    );

}
