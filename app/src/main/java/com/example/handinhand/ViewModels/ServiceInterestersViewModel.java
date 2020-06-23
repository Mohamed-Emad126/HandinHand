package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.API.ServicesClient;
import com.example.handinhand.Models.ServiceDescription;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceInterestersViewModel extends ViewModel {
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    MutableLiveData<ServiceDescription> event = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsFirstLoading() {
        return isFirstLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<ServiceDescription> getEvent(String token, int id) {
            isFirstLoading.postValue(true);
            loadEvent(token, id);
        return event;
    }

    private void loadEvent(String token, int id) {
        isError.postValue(false);
        isLoading.postValue(true);
        ServicesClient eventsClient = RetrofitApi.getInstance().getServicesClient();
        Call<ServiceDescription> serviceWithId = eventsClient.getServiceWithId(token, id);
        serviceWithId.enqueue(new Callback<ServiceDescription>() {
            @Override
            public void onResponse(Call<ServiceDescription> call, Response<ServiceDescription> response) {
                isLoading.postValue(false);
                isFirstLoading.postValue(false);
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        event.postValue(response.body());
                        isError.postValue(false);
                    } else {
                        isError.postValue(true);
                    }
                } else {
                    isError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<ServiceDescription> call, Throwable t) {
                isError.postValue(true);
                isFirstLoading.postValue(false);
                isLoading.postValue(false);
            }
        });
    }

    public void refresh(String token, int id) {
        isFirstLoading.postValue(false);
        loadEvent(token, id);
    }
}