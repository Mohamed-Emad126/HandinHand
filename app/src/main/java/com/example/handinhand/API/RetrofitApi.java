package com.example.handinhand.API;

import com.example.handinhand.Helpers.RetrofitHelper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {
    //TODO: set of the Api
    private static final String BASE_URL = "http://b410a4fe.ngrok.io";


    private static RetrofitApi ourInstance = new RetrofitApi();
    private static Retrofit retrofit = null;
    private static MainActivityClient client;
    private static ProfileClient profileClient;
    private static ItemsClient itemsClient;




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


        client = retrofit.create(MainActivityClient.class);
        profileClient = retrofit.create(ProfileClient.class);
        itemsClient = retrofit.create(ItemsClient.class);
    }

    public ItemsClient getItemsClient() {
        return itemsClient;
    }

    public MainActivityClient getMainActivityClient() {
        return client;
    }

    public ProfileClient getProfileClient() {
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
