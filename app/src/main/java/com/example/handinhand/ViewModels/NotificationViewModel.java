package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.NotificationClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.NotificationResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends ViewModel {
    private MutableLiveData<NotificationResponse> mResponse;
    private MutableLiveData<Integer> page = new MutableLiveData<>();
    private MutableLiveData<Integer> lastPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<List<NotificationResponse.Data>> mList;
    private MutableLiveData<Boolean> sharedError = new MutableLiveData<>();


    public LiveData<Boolean> getSharedError() {
        return sharedError;
    }

    public void setSharedError(Boolean sharedError) {
        this.sharedError.postValue(sharedError);
    }

    public LiveData<Integer> getPage() {
        return page;
    }

    public void setPage(Integer pge) {
        page.postValue(pge);
    }

    public LiveData<Integer> getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lstPge) {
        lastPage.postValue(lstPge);
    }

    public LiveData<List<NotificationResponse.Data>> getmList() {
        return mList;
    }

    public LiveData<Boolean> getIsFirstLoading() {
        return isFirstLoading;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError.postValue(isError);
    }

    public LiveData<Boolean> getIsFirstError() {
        return isFirstError;
    }

    public LiveData<NotificationResponse> getmResponse(int page, String token) {
        if(mResponse == null){
            isFirstLoading.postValue(true);
            isFirstError.postValue(false);
            mResponse = new MutableLiveData<>();
            mList = new MutableLiveData<>();
            getListOfNotifications(page, token);
        }
        return mResponse;
    }

    private void getListOfNotifications(int page, String token) {
        NotificationClient notificationClient = RetrofitApi.getInstance().getNotificationClient();
        isLoading.postValue(true);

        Call<NotificationResponse> notifications = notificationClient.getNotifications(token, page);
        notifications.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                isLoading.postValue(false);
                if(response.isSuccessful()){
                    if(response.body() != null && response.body().getStatus()){
                        isFirstLoading.postValue(false);
                        isError.postValue(false);
                        isFirstError.postValue(false);
                        mResponse.postValue(response.body());

                        List<NotificationResponse.Data> list = mList.getValue();
                        if(list == null){
                            list = new ArrayList<>();
                        }

                        setPage(response.body().getNotifications().getCurrent_page());
                        setLastPage(response.body().getNotifications().getLast_page());

                        if(page ==1){
                            list.clear();
                        }
                        list.addAll(response.body().getNotifications().getData());
                        mList.postValue(list);
                    }
                    else{
                        if(isFirstLoading.getValue() != null && isFirstLoading.getValue()){
                            isFirstError.postValue(true);
                            isError.postValue(false);
                            isFirstLoading.postValue(false);
                            isLoading.postValue(false);
                        }
                        else {
                            isFirstLoading.postValue(false);
                            isFirstError.postValue(false);
                            isError.postValue(true);
                            isLoading.postValue(false);
                        }
                    }
                }
                else{
                    if(isFirstLoading.getValue() != null && isFirstLoading.getValue()){
                        isFirstError.postValue(true);
                        isError.postValue(false);
                        isFirstLoading.postValue(false);
                        isLoading.postValue(false);
                    }
                    else {
                        isFirstLoading.postValue(false);
                        isFirstError.postValue(false);
                        isError.postValue(true);
                        isLoading.postValue(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                if(isFirstLoading.getValue() != null && isFirstLoading.getValue()){
                    isFirstError.postValue(true);
                    isError.postValue(false);
                    isFirstLoading.postValue(false);
                    isLoading.postValue(false);
                }
                else {
                    isFirstLoading.postValue(false);
                    isFirstError.postValue(false);
                    isError.postValue(true);
                    isLoading.postValue(false);
                }
            }
        });

    }

    public void refresh(String token){
        isError.postValue(false);
        isFirstLoading.postValue(true);
        isFirstError.postValue(false);
        getListOfNotifications(1, token);
    }

    public void loadNextPage(int page, String token){
        getListOfNotifications(page, token);
    }
}
