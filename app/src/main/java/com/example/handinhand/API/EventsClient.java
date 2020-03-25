package com.example.handinhand.API;

import com.example.handinhand.Models.AddEventResponse;
import com.example.handinhand.Models.EventPaginationObject;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface EventsClient {

    @Multipart
    @POST("api/event")
    Call<AddEventResponse> addEvent(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> item,
            @Part MultipartBody.Part image
    );


    @GET("api/events")
    Call<EventPaginationObject> getEvents(
            @Query("page") int page,
            @QueryMap Map<String, String> Queries
    );
}
