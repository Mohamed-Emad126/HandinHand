package com.example.handinhand.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.handinhand.API.ItemsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.ReportResponse;
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
        boolean isDone = false;
        if(type.equals("item")){
            isDone = reportItem(id, mp);
        }
        else if(type.equals("event")){
            isDone = reportEvent(id, mp);
        }
        else if(type.equals("product")){
            isDone = reportProduct(id, mp);
        }
        else if(type.equals("service")){
            isDone = reportService(id, mp);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        if(isDone){
            handler.postDelayed(() ->
                            Toast.makeText(context, R.string.Reported, Toast.LENGTH_SHORT).show(),
                    0 );
        }
        else{
            handler.postDelayed(() ->
                            Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                    0 );
        }
        return Result.success();
    }

    private boolean reportItem(String id, Map<String, String> reason){
        final boolean[] status = {false};
        ItemsClient client = RetrofitApi.getInstance().getItemsClient();
        client.reportItem(SharedPreferenceHelper.getToken(context), id, reason).enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                if(response.isSuccessful() && response.body()!= null){
                    status[0] = true;
                }
                else {
                    status[0] = false;
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                status[0] = false;
            }
        });
        return status[0];
    }

    private boolean reportEvent(String id, Map<String, String> reason){
        final boolean[] status = {false};

        return status[0];
    }

    private boolean reportProduct(String id, Map<String, String> reason){
        final boolean[] status = {false};

        return status[0];
    }

    private boolean reportService(String id, Map<String, String> reason){
        final boolean[] status = {false};

        return status[0];
    }
}
