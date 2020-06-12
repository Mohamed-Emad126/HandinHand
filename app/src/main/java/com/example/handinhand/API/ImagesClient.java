package com.example.handinhand.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ImagesClient {

    @GET
    Call<ResponseBody> downloadImage(@Url String imageUrl);
}
