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
import com.example.handinhand.Models.DeleteEventResponse;
import com.example.handinhand.Models.DeleteProductResponse;
import com.example.handinhand.Models.DeleteServiceResponse;
import com.example.handinhand.Models.DeletionResponse;
import com.example.handinhand.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteWorker extends Worker {
    private Context context;

    public DeleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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
            deleteItem(id);
        } else if (type.equals("event")) {
            deleteEvent(id);
        } else if (type.equals("product")) {
            deleteProduct(id);
        } else if (type.equals("service")) {
            deleteService(id);
        }
        return Result.success();
    }

    private void deleteItem(String id) {
        ItemsClient itemsClient = RetrofitApi.getInstance().getItemsClient();
        itemsClient.deleteItem(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<DeletionResponse>() {
                    @Override
                    public void onResponse(Call<DeletionResponse> call, Response<DeletionResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getStatus()) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<DeletionResponse> call, Throwable t) {
                    }
                });
    }

    private void deleteEvent(String id) {
        EventsClient eventsClient = RetrofitApi.getInstance().getEventsClient();
        eventsClient.deleteEvent(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<DeleteEventResponse>() {
                    @Override
                    public void onResponse(Call<DeleteEventResponse> call, Response<DeleteEventResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteEventResponse> call, Throwable t) {
                    }
                });
    }

    private void deleteProduct(String id) {
        ProductsClient productsClient = RetrofitApi.getInstance().getProductsClient();
        productsClient.deleteProduct(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<DeleteProductResponse>() {
                    @Override
                    public void onResponse(Call<DeleteProductResponse> call, Response<DeleteProductResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteProductResponse> call, Throwable t) {
                    }
                });
    }

    private void deleteService(String id) {
        ServicesClient servicesClient = RetrofitApi.getInstance().getServicesClient();
        servicesClient.deleteService(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<DeleteServiceResponse>() {
                    @Override
                    public void onResponse(Call<DeleteServiceResponse> call, Response<DeleteServiceResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteServiceResponse> call, Throwable t) {
                    }
                });
    }
}
