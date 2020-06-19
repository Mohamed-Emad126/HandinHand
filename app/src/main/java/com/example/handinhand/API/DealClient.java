package com.example.handinhand.API;

import com.example.handinhand.Models.Deal;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DealClient {
    @GET("api/deal/{deal_id}")
    Call<Deal> dealDescription(
            @Path("deal_id") String dealId
    );
}
