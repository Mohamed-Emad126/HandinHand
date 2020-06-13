package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.EventsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.EventPaginationObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsViewModel extends ViewModel {

    private MutableLiveData<EventPaginationObject> mResponse;
    private MutableLiveData<Integer> page = new MutableLiveData<>();
    private MutableLiveData<Integer> lastPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<List<EventPaginationObject.Data>> mList;
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

    public void setIsError(Boolean isError) {
        this.isError.postValue(isError);
    }

    public LiveData<Boolean> getIsFirstError() {
        return isFirstError;
    }

    public LiveData<EventPaginationObject> getmResponse(int page, String token) {
        if(mResponse == null){
            isFirstLoading.postValue(true);
            isFirstError.postValue(false);
            mResponse = new MutableLiveData<>();
            mList = new MutableLiveData<>();
            getListOfEvents(page, token);
        }
        return mResponse;
    }

    private void getListOfEvents(int page, String token) {
        EventsClient eventsClient = RetrofitApi.getInstance().getEventsClient();
        isLoading.postValue(true);
        Map<String, String> q = new HashMap<>();
        //f_params[orderBy][field]=price&f_params[orderBy][type]=ASC
        /*q.put("f_params[orderBy][field]", "price");
        q.put("f_params[orderBy][type]", "ASC");*/

        Call<EventPaginationObject> events = eventsClient.getEvents(token, page);
        events.enqueue(new Callback<EventPaginationObject>() {
            @Override
            public void onResponse(Call<EventPaginationObject> call, Response<EventPaginationObject> response) {
                isLoading.postValue(false);
                if(response.isSuccessful()){
                    if(response.body() != null && response.body().getStatus()){
                        isFirstLoading.postValue(false);
                        isError.postValue(false);
                        isFirstError.postValue(false);
                        mResponse.postValue(response.body());

                        List<EventPaginationObject.Data> list = mList.getValue();
                        if(list == null){
                            list = new ArrayList<>();
                        }

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
            public void onFailure(Call<EventPaginationObject> call, Throwable t) {
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
        getListOfEvents(1, token);
    }

    public void deleteEvent(int position){
        List<EventPaginationObject.Data> list = mList.getValue();
        if(list != null) {
            list.remove(position);
        }
        mList.postValue(list);
    }

    public void loadNextPage(int page, String token){
        getListOfEvents(page, token);
    }

    public void interestEvent(int position) {
        List<EventPaginationObject.Data> list = mList.getValue();
        if(list != null && list.size() >0) {
            EventPaginationObject.Data data = list.get(position);
            if(data.getIs_interested()){
                data.setIs_interested(false);
                data.setInterests(data.getInterests()-1);
            }
            else{
                data.setIs_interested(true);
                data.setInterests(data.getInterests()+1);
            }
            list.set(position, data);
        }
        mList.postValue(list);
    }
}
