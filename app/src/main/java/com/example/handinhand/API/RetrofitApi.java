package com.example.handinhand.API;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {
    //TODO: set of the Api
    private static final String BASE_URL = "http://cc7637924d19.ngrok.io";


    private static RetrofitApi ourInstance = null;
    private static Retrofit retrofit = null;
    private static MainActivityClient client = null;
    private static ProfileClient profileClient = null;
    private static ItemsClient itemsClient = null;
    private static EventsClient eventsClient = null;
    private static ImagesClient imagesClient = null;
    private static ServicesClient servicesClient = null;
    private static ProductsClient productsClient = null;
    private static NotificationClient notificationClient = null;
    private static DealClient dealClient = null;
    private static SettingsClient settingsClient = null;


    public static RetrofitApi getInstance() {
        if(ourInstance == null){
            ourInstance = new RetrofitApi();
        }
        return ourInstance;
    }

    private RetrofitApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor accept = chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };
        OkHttpClient clientLog = new OkHttpClient.Builder()
          .addInterceptor(logging)
          .addInterceptor(accept)
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

    public SettingsClient getSettingsClient() {
        if(settingsClient == null){
            settingsClient = retrofit.create(SettingsClient.class);
        }
        return settingsClient;
    }

    public DealClient getDealClient() {
        if(dealClient == null){
            dealClient = retrofit.create(DealClient.class);
        }
        return dealClient;
    }

    public NotificationClient getNotificationClient() {
        if(notificationClient == null){
            notificationClient = retrofit.create(NotificationClient.class);
        }
        return notificationClient;
    }

    public ProductsClient getProductsClient() {
        if(productsClient == null){
            productsClient = retrofit.create(ProductsClient.class);
        }
        return productsClient;
    }

    public EventsClient getEventsClient() {
        if(eventsClient == null){
            eventsClient = retrofit.create(EventsClient.class);
        }
        return eventsClient;
    }

    public ServicesClient getServicesClient() {
        if(servicesClient == null){
            servicesClient = retrofit.create(ServicesClient.class);
        }
        return servicesClient;
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
