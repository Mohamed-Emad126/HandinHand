package com.example.handinhand.API;

import com.example.handinhand.Models.CancelItemResponse;
import com.example.handinhand.Models.Deal;
import com.example.handinhand.Models.DealResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DealClient {

    @GET("api/deals/{deal_id}")
    Call<Deal> getDeal(
            @Header("Authorization") String token,
            @Path("deal_id") int dealId
    );

    //updates.put("_method", RetrofitHelper.createPartFromString("PATCH"));
    @FormUrlEncoded
    @POST("api/deals/{deal_id}/accept")
    Call<DealResponse> acceptDeal(
            @Header("Authorization") String token,
            @Path("deal_id") String dealId,
            @FieldMap Map<String, String> details
    );

    @FormUrlEncoded
    @POST("api/deals/{deal_id}/decline")
    Call<DealResponse> declineDeal(
            @Header("Authorization") String token,
            @Path("deal_id") String dealId,
            @FieldMap Map<String, String> details
    );

    //authenticatedapi/deals/{deal id}/respond
    //updates.put("_method", RetrofitHelper.createPartFromString("PATCH"));
    @FormUrlEncoded
    @POST("api/deals/{deal_id}/respond")
    Call<DealResponse> buyerResponse(
            @Header("Authorization") String token,
            @Path("deal_id") String dealId,
            @FieldMap Map<String, String> response
    );

    //api/items/{item id}/cancel
    @POST("api/items/{item_id}/cancel")
    Call<CancelItemResponse> cancelItem(
            @Header("Authorization") String token,
            @Path("item_id") String dealId,
            @FieldMap Map<String, String> response
    );

}
