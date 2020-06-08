package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.EventsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.EventPaginationObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsViewModel extends ViewModel {

    MutableLiveData<EventPaginationObject> mResponse;
    MutableLiveData<Integer> page = new MutableLiveData<>();
    MutableLiveData<Integer> lastPage = new MutableLiveData<>();
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstError = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<List<EventPaginationObject.Data>> mList;

    public LiveData<Integer> getPage() {
        return page;
    }

    public void setPage(Integer pge) {
        page.postValue(pge);
    }

    public void setIsError(Boolean isError) {
        this.isError.postValue(isError);
    }

    public LiveData<Integer> getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lstPge) {
        lastPage.postValue(lstPge);
    }

    public LiveData<List<EventPaginationObject.Data>> getmList() {
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

    public LiveData<Boolean> getIsFirstError() {
        return isFirstError;
    }

    public LiveData<EventPaginationObject> getmResponse(int page) {
        if(mResponse == null){
            isFirstLoading.postValue(true);
            isFirstError.postValue(false);
            mList = new MutableLiveData<>();
            mResponse = new MutableLiveData<>();
            getListOfEvents(page);
        }
        return mResponse;
    }

    private void getListOfEvents(int page) {
        EventsClient client = RetrofitApi.getInstance().getEventsClient();
        isLoading.postValue(true);
        /*Map<String, String> q = new HashMap<>();
        //f_params[orderBy][field]=price&f_params[orderBy][type]=ASC
        q.put("f_params[orderBy][field]", "price");
        q.put("f_params[orderBy][type]", "ASC");*/

        Call<EventPaginationObject> call = client.getEvents(page, null);
        call.enqueue(new Callback<EventPaginationObject>() {
            @Override
            public void onResponse(Call<EventPaginationObject> call,
                                   Response<EventPaginationObject> response) {
                isLoading.postValue(false);
                if(response.isSuccessful()){
                    if(response.body() != null && response.body().getStatus()){
                        isFirstLoading.postValue(false);
                        isError.postValue(false);
                        mResponse.postValue(response.body());

                        List<EventPaginationObject.Data> list = mList.getValue();

                        setPage(response.body().getEvents().getCurrent_page());
                        setLastPage(response.body().getEvents().getLast_page());

                        if(page ==1){
                            list.clear();
                        }
                        list.addAll(response.body().getEvents().getData());
                        mList.postValue(list);
                    }
                    else{
                        if(isFirstLoading.getValue() != null && isFirstLoading.getValue()){
                            isFirstError.postValue(true);
                            isFirstLoading.postValue(false);
                        }
                        else {
                            isError.postValue(true);
                            isLoading.postValue(false);
                        }
                    }
                }
                else{
                    if(isFirstLoading.getValue() != null && isFirstLoading.getValue()){
                        isFirstError.postValue(true);
                        isError.postValue(true);
                        isFirstLoading.postValue(false);
                        isLoading.postValue(false);
                    }
                    else {
                        isError.postValue(true);
                        isLoading.postValue(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<EventPaginationObject> call, Throwable t) {
                if(isFirstLoading.getValue() != null && isFirstLoading.getValue()){
                    isFirstError.postValue(true);
                    isError.postValue(true);
                    isFirstLoading.postValue(false);
                    isLoading.postValue(false);
                }
                else{
                    isLoading.postValue(false);
                    isError.postValue(true);
                }
            }

        });

    }

    public void refresh(){
        isError.postValue(false);
        isFirstLoading.postValue(true);
        isFirstError.postValue(false);
        getListOfEvents(1);
    }


    public void loadNextPage(int page){
        getListOfEvents(page);
    }

    public void interestEvent(List<EventPaginationObject.Data> eventsList) {
        mList.postValue(eventsList);
    }
}
