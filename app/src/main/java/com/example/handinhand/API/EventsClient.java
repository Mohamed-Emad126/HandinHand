package com.example.handinhand.API;

import com.example.handinhand.Models.AddEventResponse;
import com.example.handinhand.Models.DeleteEventResponse;
import com.example.handinhand.Models.EventInterestResponse;
import com.example.handinhand.Models.EventPaginationObject;
import com.example.handinhand.Models.EventReportResponse;

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

public interface EventsClient {

    @Multipart
    @POST("api/events")
    Call<AddEventResponse> addEvent(
            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> event,
            @Part MultipartBody.Part image
    );


    @GET("api/events")
    Call<EventPaginationObject> getEvents(
            @Header("Authorization") String token,
            @Query("page") int page
    );

    @FormUrlEncoded
    @POST("api/events/{event_id}/report")
    Call<EventReportResponse> reportEvent(
            @Header("Authorization") String token,
            @Path("event_id") String eventId,
            @FieldMap Map<String, String> reason
    );



    @DELETE("api/events/{event_id}")
    Call<DeleteEventResponse> deleteEvent(
            @Header("Authorization") String token,
            @Path("event_id") String eventId
    );


    @POST("api/events/{event_id}/interest")
    Call<EventInterestResponse> interestEvent(
            @Header("Authorization") String token,
            @Path("event_id") String eventId
    );
}
