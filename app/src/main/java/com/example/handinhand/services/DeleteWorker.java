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
        boolean isDone = false;
        if(type.equals("item")){
            isDone = deleteItem(id);
        }
        else if(type.equals("event")){
            isDone = deleteEvent(id);
        }
        else if(type.equals("product")){
            isDone = deleteProduct(id);
        }
        else if(type.equals("service")){
            isDone = deleteProduct(id);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        if(isDone){
            handler.postDelayed(() ->
                            Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show(),
                    0 );
        }
        else{
            handler.postDelayed(() ->
                            Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show(),
                    0 );
        }
        return isDone?Result.success():Result.retry();
    }

    private boolean deleteItem(String id){
        final boolean[] status = {false};

        ItemsClient itemsClient = RetrofitApi.getInstance().getItemsClient();
        itemsClient.deleteItem(SharedPreferenceHelper.getToken(context), id)
                .enqueue(new Callback<DeletionResponse>() {
            @Override
            public void onResponse(Call<DeletionResponse> call, Response<DeletionResponse> response) {
                if(response.isSuccessful() && response.body()!= null && response.body().getStatus()){
                    status[0] = true;
                }
                else{
                    status[0] = false;
                }
            }
            @Override
            public void onFailure(Call<DeletionResponse> call, Throwable t) {
                status[0] = false;
            }
        });
        return status[0];
    }

    private boolean deleteEvent(String id){
        final boolean[] status = {false};
        //TODO: Delete event
        return status[0];
    }

    private boolean deleteProduct(String id){
        final boolean[] status = {false};
        //TODO: Delete product
        return status[0];
    }

    private boolean deleteService(String id){
        final boolean[] status = {false};
        //TODO: Delete service
        return status[0];
    }
}
