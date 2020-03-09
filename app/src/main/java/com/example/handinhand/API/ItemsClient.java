package com.example.handinhand.API;


import com.example.handinhand.Models.AddItemResponse;

import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface ItemsClient {

    @Multipart
    @POST("api/user/{user_id}/item")
    Call<AddItemResponse> addItem(
            @Header("Authorization") String token,
            @Path("user_id") String user_id,
            @PartMap Map<String, RequestBody> item,
            @Part MultipartBody.Part image
    );

}
