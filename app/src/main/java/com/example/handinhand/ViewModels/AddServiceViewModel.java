package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.API.ServicesClient;
import com.example.handinhand.Models.AddServiceResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddServiceViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<AddServiceResponse> mResponse = null;

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<AddServiceResponse> getmResponse(String token, Map<String, String> serviceInfo) {
        if (mResponse == null) {
            isLoading.postValue(true);
            mResponse = new MutableLiveData<>();
            addItem(token, serviceInfo);
            return mResponse;
        } else {
            return mResponse;
        }
    }

    private void addItem(String token, Map<String, String> serviceInfo) {
        ServicesClient servicesClient = RetrofitApi.getInstance().getServicesClient();
        Call<AddServiceResponse> addServiceResponseCall = servicesClient.addService(token, serviceInfo);

        addServiceResponseCall.enqueue(new Callback<AddServiceResponse>() {
            @Override
            public void onResponse(Call<AddServiceResponse> call, Response<AddServiceResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    mResponse.postValue(response.body());
                    isError.postValue(false);
                } else {
                    isError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<AddServiceResponse> call, Throwable t) {
                isLoading.postValue(false);
                isError.postValue(true);
            }
        });
    }

    public void leave() {
        isLoading = new MutableLiveData<>();
        isError = new MutableLiveData<>();
        mResponse = null;

    }
}
