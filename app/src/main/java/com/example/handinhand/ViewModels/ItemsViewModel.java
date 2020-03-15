package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.ItemsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.ItemsPaginationObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsViewModel extends ViewModel {
    MutableLiveData<ItemsPaginationObject> mResponse;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<ItemsPaginationObject> getmResponse(int page) {
        if(mResponse == null){
            mResponse = new MutableLiveData<>();
            getListOfItems(page);
        }
        return mResponse;
    }

    private void getListOfItems(int page) {
        ItemsClient client = RetrofitApi.getInstance().getItemsClient();
        isLoading.postValue(true);
        Call<ItemsPaginationObject> call = client.getItems(page);
        call.enqueue(new Callback<ItemsPaginationObject>() {
            @Override
            public void onResponse(Call<ItemsPaginationObject> call,
                                   Response<ItemsPaginationObject> response) {
                isLoading.postValue(false);
                if(response.isSuccessful()){
                    if(response.body() != null && response.body().getStatus()){
                        isError.postValue(false);
                        mResponse.postValue(response.body());
                    }
                    else{
                        isError.postValue(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<ItemsPaginationObject> call, Throwable t) {
                isLoading.postValue(false);
                isError.postValue(true);
            }

        });

    }

    public void refresh(){
        isLoading.postValue(true);
        isError.postValue(false);
        getListOfItems(0);
    }
}
