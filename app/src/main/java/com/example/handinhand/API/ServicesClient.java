package com.example.handinhand.API;

import com.example.handinhand.Models.AddServiceResponse;
import com.example.handinhand.Models.DeleteServiceResponse;
import com.example.handinhand.Models.ServiceInterestResponse;
import com.example.handinhand.Models.ServicePaginationObject;
import com.example.handinhand.Models.ServiceReportResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicesClient {

    @FormUrlEncoded
    @POST("api/services")
    Call<AddServiceResponse> addService(
            @Header("Authorization") String token,
            @FieldMap Map<String, String> service
    );


    @GET("api/services")
    Call<ServicePaginationObject> getService(
            @Header("Authorization") String token,
            @Query("page") int page
    );

    @FormUrlEncoded
    @POST("api/services/{service_id}/report")
    Call<ServiceReportResponse> reportService(
            @Header("Authorization") String token,
            @Path("service_id") String serviceId,
            @FieldMap Map<String, String> reason
    );


    @DELETE("api/services/{service_id}")
    Call<DeleteServiceResponse> deleteService(
            @Header("Authorization") String token,
            @Path("service_id") String serviceId
    );


    @POST("api/services/{service_id}/interest")
    Call<ServiceInterestResponse> interestService(
            @Header("Authorization") String token,
            @Path("service_id") String serviceId
    );
}
