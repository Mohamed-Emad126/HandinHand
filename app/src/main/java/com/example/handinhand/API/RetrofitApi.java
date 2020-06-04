package com.example.handinhand.API;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {
    //TODO: set of the Api
    private static final String BASE_URL = "http://59cbcc73.ngrok.io";


    private static RetrofitApi ourInstance = new RetrofitApi();
    private static Retrofit retrofit = null;
    private static MainActivityClient client = null;
    private static ProfileClient profileClient = null;
    private static ItemsClient itemsClient = null;
    private static EventsClient eventsClient = null;
    private static ImagesClient imagesClient = null;


    public static RetrofitApi getInstance() {
        if(ourInstance == null){
            ourInstance = new RetrofitApi();
        }
        return ourInstance;
    }

    private RetrofitApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient clientLog = new OkHttpClient.Builder()
          .addInterceptor(logging)
          .build();

        retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(clientLog)
                            .build();
    }

    public ImagesClient getImagesClient() {
        if(imagesClient == null){
            imagesClient = retrofit.create(ImagesClient.class);
        }
        return imagesClient;
    }

    public EventsClient getEventsClient() {
        if(eventsClient == null){
            eventsClient = retrofit.create(EventsClient.class);
        }
        return eventsClient;
    }

    public ItemsClient getItemsClient() {
        if(itemsClient == null){
            itemsClient = retrofit.create(ItemsClient.class);
        }
        return itemsClient;
    }

    public MainActivityClient getMainActivityClient() {
        if(client == null){
            client = retrofit.create(MainActivityClient.class);
        }
        return client;
    }

    public ProfileClient getProfileClient() {
        if(profileClient == null){
            profileClient = retrofit.create(ProfileClient.class);
        }
        return profileClient;
    }

    public Retrofit getRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
