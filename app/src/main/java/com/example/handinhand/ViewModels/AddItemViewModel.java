package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.ItemsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.AddItemResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<AddItemResponse> mResponse = null;

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<AddItemResponse> getmResponse(String token, String id,
                                                  HashMap<String, RequestBody> itemInfo,
                                                  MultipartBody.Part image) {
        if(mResponse == null){
            isLoading.postValue(true);
            mResponse = new MutableLiveData<>();
            addItem(token, id, itemInfo, image);
            return mResponse;
        }
        else{
            return mResponse;
        }
    }

    private void addItem(String token, String id, HashMap<String, RequestBody> itemInfo,
                         MultipartBody.Part image) {
        ItemsClient itemsClient = RetrofitApi.getInstance().getItemsClient();
        Call<AddItemResponse> addItemResponseCall = itemsClient.addItem(token, itemInfo, image);

        addItemResponseCall.enqueue(new Callback<AddItemResponse>() {
            @Override
            public void onResponse(Call<AddItemResponse> call, Response<AddItemResponse> response) {
                isLoading.postValue(false);
                if(response.isSuccessful()){
                    mResponse.postValue(response.body());
                    isError.postValue(false);
                }
                else{
                    isError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<AddItemResponse> call, Throwable t) {
                isLoading.postValue(false);
                isError.postValue(true);
            }
        });
    }

    public void leave(){
        isLoading = new MutableLiveData<>();
        isError = new MutableLiveData<>();
        mResponse =null;

    }
}
