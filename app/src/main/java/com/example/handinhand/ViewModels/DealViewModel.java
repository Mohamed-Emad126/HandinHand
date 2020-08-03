package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.DealClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.Deal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DealViewModel extends ViewModel {
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<Boolean> isDone = new MutableLiveData<>();
    MutableLiveData<Deal> deal = new MutableLiveData<>();;


    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<Boolean> getIsDone() {
        return isDone;
    }

    public void setIsDone( boolean done) {
        isDone.postValue(done);
    }

    public LiveData<Deal> getDeal() {
        return deal;
    }

    public void getDeal(String token, int id) {
        loadDeal(token, id);
    }

    private void loadDeal(String token, int id) {
        isError.postValue(false);
        isLoading.postValue(true);
        DealClient dealClient = RetrofitApi.getInstance().getDealClient();
        Call<Deal> dealCall = dealClient.getDeal(token, id);
        dealCall.enqueue(new Callback<Deal>() {
           @Override
           public void onResponse(Call<Deal> call, Response<Deal> response) {
               isLoading.postValue(false);
               if(response.isSuccessful() && response.body() != null){
                   if(response.body().getStatus()){
                       deal.postValue(response.body());
                       isDone.postValue(false);
                   }
                   else{
                       isError.postValue(true);
                   }
               }
           }

           @Override
           public void onFailure(Call<Deal> call, Throwable t) {
               isLoading.postValue(false);
               isError.postValue(true);
           }
       });
    }
}