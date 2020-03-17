package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.handinhand.API.ItemsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.ItemsPaginationObject;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsViewModel extends ViewModel {
    MutableLiveData<ItemsPaginationObject> mResponse;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstError = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();

    public LiveData<Boolean> getIsFirstLoading() {
        return isFirstLoading;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<Boolean> getIsFirstError() {
        return isFirstError;
    }

    public LiveData<ItemsPaginationObject> getmResponse(int page) {
        if(mResponse == null){
            isFirstLoading.postValue(true);
            isFirstError.postValue(false);
            mResponse = new MutableLiveData<>();
            getListOfItems(page);
        }
        return mResponse;
    }

    private void getListOfItems(int page) {
        ItemsClient client = RetrofitApi.getInstance().getItemsClient();
        isLoading.postValue(true);
        Map<String, String> q = new HashMap<>();
        //f_params[orderBy][field]=price&f_params[orderBy][type]=ASC
        q.put("f_params[orderBy][field]", "price");
        q.put("f_params[orderBy][type]", "ASC");

        Call<ItemsPaginationObject> call = client.getItems(page, q);
        call.enqueue(new Callback<ItemsPaginationObject>() {
            @Override
            public void onResponse(Call<ItemsPaginationObject> call,
                                   Response<ItemsPaginationObject> response) {
                isLoading.postValue(false);
                isFirstLoading.postValue(false);
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
                isFirstLoading.postValue(false);
                isLoading.postValue(false);
                isError.postValue(true);
            }

        });

    }

    public void refresh(){
        isError.postValue(false);
        isFirstLoading.postValue(true);
        getListOfItems(1);
    }
    public void loadNextPage(int page){
        getListOfItems(page);
    }
}
