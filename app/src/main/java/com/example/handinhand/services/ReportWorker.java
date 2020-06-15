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
import com.example.handinhand.Models.EventReportResponse;
import com.example.handinhand.Models.ProductReportResponse;
import com.example.handinhand.Models.ReportResponse;
import com.example.handinhand.Models.ServiceReportResponse;
import com.example.handinhand.R;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportWorker extends Worker {
    private Context context;
    public ReportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String type = inputData.getString("TYPE");
        String id = inputData.getString("ELEMENT_ID");
        String reason = inputData.getString("REASON");
        Map<String, String> mp = new HashMap<>();
        mp.put("reason", reason);
        if(type.equals("item")){
            reportItem(id, mp);
        }
        else if(type.equals("event")){
            reportEvent(id, mp);
        }
        else if(type.equals("product")){
            reportProduct(id, mp);
        }
        else if(type.equals("service")){
            reportService(id, mp);
        }

        return Result.success();
    }

    private void reportItem(String id, Map<String, String> reason){
        ItemsClient client = RetrofitApi.getInstance().getItemsClient();
        client.reportItem(SharedPreferenceHelper.getToken(context), id, reason).enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if(response.isSuccessful() && response.body()!= null){
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.Reported, Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }
                else {
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
            }
        });
    }

    private void reportEvent(String id, Map<String, String> reason){
        EventsClient eventsClient = RetrofitApi.getInstance().getEventsClient();
        eventsClient.reportEvent(SharedPreferenceHelper.getToken(context), id, reason)
                .enqueue(new Callback<EventReportResponse>() {
                    @Override
                    public void onResponse(Call<EventReportResponse> call, Response<EventReportResponse> response) {
                        if(response.isSuccessful() && response.body()!= null){
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.Reported, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<EventReportResponse> call, Throwable t) {
                    }
                });
    }

    private void reportProduct(String id, Map<String, String> reason){
        ProductsClient productsClient = RetrofitApi.getInstance().getProductsClient();
        productsClient.reportProduct(SharedPreferenceHelper.getToken(context), id, reason)
                .enqueue(new Callback<ProductReportResponse>() {
                    @Override
                    public void onResponse(Call<ProductReportResponse> call, Response<ProductReportResponse> response) {
                        if(response.isSuccessful() && response.body()!= null){
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.Reported, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductReportResponse> call, Throwable t) {
                    }
                });
    }

    private void reportService(String id, Map<String, String> reason){
        ServicesClient servicesClient = RetrofitApi.getInstance().getServicesClient();
        servicesClient.reportService(SharedPreferenceHelper.getToken(context), id, reason)
                .enqueue(new Callback<ServiceReportResponse>() {
                    @Override
                    public void onResponse(Call<ServiceReportResponse> call, Response<ServiceReportResponse> response) {
                        if(response.isSuccessful() && response.body()!= null){
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, R.string.Reported, Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<ServiceReportResponse> call, Throwable t) {
                    }
                });
    }
}
