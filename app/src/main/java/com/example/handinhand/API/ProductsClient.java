package com.example.handinhand.API;

import com.example.handinhand.Models.AddProductResponse;
import com.example.handinhand.Models.DeleteProductResponse;
import com.example.handinhand.Models.ProductPaginationObject;
import com.example.handinhand.Models.ProductReportResponse;
import com.example.handinhand.Models.ProductRequestResponse;

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

public interface ProductsClient {
    @Multipart
    @POST("api/products")
    Call<AddProductResponse> addProduct(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> item,
            @Part MultipartBody.Part image
    );


    @GET("api/products")
    Call<ProductPaginationObject> getProducts(
            @Header("Authorization") String token,
            @Query("page") int page
    );

    @FormUrlEncoded
    @POST("api/products/{product_id}/report")
    Call<ProductReportResponse> reportProduct(
            @Header("Authorization") String token,
            @Path("product_id") String product_id,
            @FieldMap Map<String, String> reason
    );



    @DELETE("api/products/{product_id}")
    Call<DeleteProductResponse> deleteProduct(
            @Header("Authorization") String token,
            @Path("product_id") String product_id
    );


    @POST("api/products/{product_id}/request")
    Call<ProductRequestResponse> productRequest(
            @Header("Authorization") String token,
            @Path("product_id") String product_id
    );
}
