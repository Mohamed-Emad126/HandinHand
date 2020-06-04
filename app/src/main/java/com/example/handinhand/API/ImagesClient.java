package com.example.handinhand.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImagesClient {

    @GET("/resource/{image_url}")
    Call<ResponseBody> downloadImage(@Path("Image_url") String imageUrl);
}
