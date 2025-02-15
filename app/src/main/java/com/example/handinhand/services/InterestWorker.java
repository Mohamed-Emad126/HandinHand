package com.example.handinhand.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.handinhand.API.EventsClient;
import com.example.handinhand.API.ItemsClient;
import com.example.handinhand.API.ProductsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.API.ServicesClient;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.EventInterestResponse;
import com.example.handinhand.Models.ItemRequestResponse;
import com.example.handinhand.Models.ProductRequestResponse;
import com.example.handinhand.Models.ServiceInterestResponse;
import com.example.handinhand.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterestWorker extends Worker {

    private Context context;

    public InterestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String type = inputData.getString("TYPE");
        String id = inputData.getString("ELEMENT_ID");
        if (type.equals("item")) {
            requestItem(id);
        } else if (type.equals("event")) {
            requestEvent(id);
        } else if (type.equals("product")) {
            requestProduct(id);
        } else if (type.equals("service")) {
            requestService(id);
        }
        return Result.success();
    }

    private void requestItem(String id) {
        ItemsClient client = RetrofitApi.getInstance().getItemsClient();
        client.itemRequest(SharedPreferenceHelper.getToken(context), id).enqueue(new Callback<ItemRequestResponse>() {
            @Override
            public void onResponse(Call<ItemRequestResponse> call, Response<ItemRequestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.done, Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }
            }

            @Override
            public void onFailure(Call<ItemRequestResponse> call, Throwable t) {
            }
        });
    }

    private void requestEvent(String id) {
        EventsClient eventsClient = RetrofitApi.getInstance().getEventsClient();
        eventsClient.interestEvent(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<EventInterestResponse>() {
                    @Override
                    public void onResponse(Call<EventInterestResponse> call, Response<EventInterestResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.done, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<EventInterestResponse> call, Throwable t) {
                    }
                });
    }

    private void requestProduct(String id) {
        ProductsClient productsClient = RetrofitApi.getInstance().getProductsClient();
        productsClient.productRequest(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<ProductRequestResponse>() {
                    @Override
                    public void onResponse(Call<ProductRequestResponse> call, Response<ProductRequestResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new Handler(Looper.getMainLooper()).postDelayed(() -> Toast.makeText(context, R.string.done, Toast.LENGTH_SHORT).show(), 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductRequestResponse> call, Throwable t) {
                    }
                });
    }

    private void requestService(String id) {
        ServicesClient servicesClient = RetrofitApi.getInstance().getServicesClient();
        servicesClient.interestService(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<ServiceInterestResponse>() {
                    @Override
                    public void onResponse(Call<ServiceInterestResponse> call, Response<ServiceInterestResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.done, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<ServiceInterestResponse> call, Throwable t) {
                    }
                });
    }
}
