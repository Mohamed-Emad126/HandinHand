package com.example.handinhand.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.handinhand.API.DealClient;
import com.example.handinhand.API.NotificationClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.CancelItemResponse;
import com.example.handinhand.Models.DealResponse;
import com.example.handinhand.Models.SpecificNotification;
import com.example.handinhand.R;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DealWorker extends Worker {

    private Context context;
    public DealWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String type = getInputData().getString("TYPE");
        String id = getInputData().getString("ELEMENT_ID");
        if(type.equals("ACCEPT")){
            String details = getInputData().getString("DETAILS");
            acceptDeal(id, details);
        }
        else if(type.equals("DECLINE")){
            declineDeal(id);
        }
        else if(type.equals("RESPONSE")){
            String details = getInputData().getString("DETAILS");
            responseDeal(id, details);
        }
        else if(type.equals("CANCEL")){
            cancelItem(id);
        }
        else{
            readNotification(id);
        }
        return Result.success();
    }

    private void cancelItem(String id) {
        DealClient cancelItem = RetrofitApi.getInstance().getDealClient();
        Map<String, String> mp = new HashMap<>();
        mp.put("_method", "PATCH");
        Call<CancelItemResponse> responseCall = cancelItem.cancelItem(SharedPreferenceHelper.getToken(context), id, mp);
        responseCall.enqueue(new Callback<CancelItemResponse>() {
            @Override
            public void onResponse(Call<CancelItemResponse> call, Response<CancelItemResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                        Toast.makeText(context, R.string.canceled, Toast.LENGTH_SHORT).show(),
                                0);
                    }
                }
                else{
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                            0);
                }
            }

            @Override
            public void onFailure(Call<CancelItemResponse> call, Throwable t) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                                Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                        0);
            }
        });
    }

    private void readNotification(String id) {
        NotificationClient notificationClient = RetrofitApi.getInstance().getNotificationClient();
        Call<SpecificNotification> call = notificationClient
                .specificNotification(SharedPreferenceHelper.getToken(context), id);

        call.enqueue(new Callback<SpecificNotification>() {
            @Override
            public void onResponse(Call<SpecificNotification> call, Response<SpecificNotification> response) {

            }

            @Override
            public void onFailure(Call<SpecificNotification> call, Throwable t) {

            }
        });

    }

    private void responseDeal(String id, String details) {
        DealClient dealClient = RetrofitApi.getInstance().getDealClient();
        Map<String, String> mp = new HashMap<>();
        mp.put("buyer_status", details);
        mp.put("_method", "PATCH");
        Call<DealResponse> dealResponseCall = dealClient.buyerResponse(SharedPreferenceHelper.getToken(context), id, mp);
        dealResponseCall.enqueue(new Callback<DealResponse>() {
            @Override
            public void onResponse(Call<DealResponse> call, Response<DealResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                        Toast.makeText(context, R.string.done, Toast.LENGTH_SHORT).show(),
                                0);
                    }
                }
                else{
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                            0);
                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                                Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                        0);
            }
        });
    }

    private void declineDeal(String id) {
        DealClient dealClient = RetrofitApi.getInstance().getDealClient();
        Map<String, String> mp = new HashMap<>();
        mp.put("_method", "PATCH");
        Call<DealResponse> dealResponseCall = dealClient.declineDeal(SharedPreferenceHelper.getToken(context), id, mp);
        dealResponseCall.enqueue(new Callback<DealResponse>() {
            @Override
            public void onResponse(Call<DealResponse> call, Response<DealResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                        Toast.makeText(context, R.string.declined, Toast.LENGTH_SHORT).show(),
                                0);
                    }
                }
                else{
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                            0);
                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                                Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                        0);
            }
        });
    }

    private void acceptDeal(String id, String details) {
        DealClient dealClient = RetrofitApi.getInstance().getDealClient();
        Map<String, String> mp = new HashMap<>();
        mp.put("details", details);
        mp.put("_method", "PATCH");
        Call<DealResponse> dealResponseCall = dealClient.acceptDeal(SharedPreferenceHelper.getToken(context), id, mp);
        dealResponseCall.enqueue(new Callback<DealResponse>() {
            @Override
            public void onResponse(Call<DealResponse> call, Response<DealResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().getStatus()){
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                Toast.makeText(context, R.string.Accepted, Toast.LENGTH_SHORT).show(),
                                0);
                    }
                }
                else{
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                                    Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                            0);
                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                                Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                        0);
            }
        });
    }
}
