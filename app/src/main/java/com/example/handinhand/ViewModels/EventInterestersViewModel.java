package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.EventsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.EventDescription;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventInterestersViewModel extends ViewModel {
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    MutableLiveData<EventDescription> event;

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsFirstLoading() {
        return isFirstLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<EventDescription> getEvent(String token, int id) {
        if(event == null){
            isFirstLoading.postValue(true);
            event = new MutableLiveData<>();
            loadEvent(token, id);
        }
        return event;
    }

    private void loadEvent(String token, int id) {
        isError.postValue(false);
        isLoading.postValue(true);
        EventsClient eventsClient = RetrofitApi.getInstance().getEventsClient();
        Call<EventDescription> eventWithId = eventsClient.getEventWithId(token, id);
        eventWithId.enqueue(new Callback<EventDescription>() {
            @Override
            public void onResponse(Call<EventDescription> call, Response<EventDescription> response) {
                isLoading.postValue(false);
                isFirstLoading.postValue(false);
                if(response.isSuccessful()){
                    if(response.body().getStatus()){
                        event.postValue(response.body());
                        isError.postValue(false);
                    }
                    else{
                        isError.postValue(true);
                    }
                }
                else {
                    isError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<EventDescription> call, Throwable t) {
                isError.postValue(true);
                isLoading.postValue(false);
            }
        });
    }

    public void refresh(String token, int id){
        isFirstLoading.postValue(false);
        loadEvent(token, id);
    }
}
