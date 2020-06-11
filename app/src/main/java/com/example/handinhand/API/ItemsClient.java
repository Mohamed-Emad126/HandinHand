package com.example.handinhand.API;


import com.example.handinhand.Models.AddItemResponse;
import com.example.handinhand.Models.DeletionResponse;
import com.example.handinhand.Models.ItemRequestResponse;
import com.example.handinhand.Models.ItemsPaginationObject;
import com.example.handinhand.Models.ReportResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ItemsClient {

    @Multipart
    @POST("api/items")
    Call<AddItemResponse> addItem(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> item,
            @Part MultipartBody.Part image
    );


    @GET("api/items")
    Call<ItemsPaginationObject> getItems(
            @Header("Authorization") String token,
            @Query("page") int page,
            @QueryMap Map<String, String> Queries
    );

    @FormUrlEncoded
    @POST("api/items/{item_id}/report")
    Call<ReportResponse> reportItem(
            @Header("Authorization") String token,
            @Path("item_id") String item_id,
            @FieldMap Map<String, String> reason
    );



    @DELETE("api/items/{item_id}")
    Call<DeletionResponse> deleteItem(
            @Header("Authorization") String token,
            @Path("item_id") String item_id
    );


    @POST("api/items/{item_id}/request")
    Call<ItemRequestResponse> itemRequest(
            @Header("Authorization") String token,
            @Path("item_id") String item_id
    );

}
