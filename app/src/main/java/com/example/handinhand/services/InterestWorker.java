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
import com.example.handinhand.Models.ItemRequestResponse;
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
        boolean isDone = false;
        if(type.equals("item")){
            isDone = requestItem(id);
        }
        else if(type.equals("event")){
            isDone = requestEvent(id);
        }
        else if(type.equals("product")){
            isDone = requestProduct(id);
        }
        else if(type.equals("service")){
            isDone = requestService(id);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        if(isDone){
            handler.postDelayed(() ->
                            Toast.makeText(context, R.string.done, Toast.LENGTH_SHORT).show(),
                    0 );
        }
        else{
            handler.postDelayed(() ->
                            Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                    0 );
        }
        return isDone?Result.success():Result.retry();
    }

    private boolean requestItem(String id){
        final boolean[] status = {false};
        ItemsClient client = RetrofitApi.getInstance().getItemsClient();
        client.itemRequest(SharedPreferenceHelper.getToken(context), id).enqueue(new Callback<ItemRequestResponse>() {
            @Override
            public void onResponse(Call<ItemRequestResponse> call, Response<ItemRequestResponse> response) {
                if(response.isSuccessful() && response.body()!= null){
                    status[0] = true;
                }
                else {
                    status[0] = false;
                }
            }

            @Override
            public void onFailure(Call<ItemRequestResponse> call, Throwable t) {
                status[0] = false;
            }
        });
        return status[0];
    }

    private boolean requestEvent(String id){
        final boolean[] status = {false};

        return status[0];
    }

    private boolean requestProduct(String id){
        final boolean[] status = {false};

        return status[0];
    }

    private boolean requestService(String id){
        final boolean[] status = {false};

        return status[0];
    }
}
