package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.API.ServicesClient;
import com.example.handinhand.Models.ServicePaginationObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesViewModel extends ViewModel {
    private MutableLiveData<ServicePaginationObject> mResponse;
    private MutableLiveData<Integer> page = new MutableLiveData<>();
    private MutableLiveData<Integer> lastPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<List<ServicePaginationObject.Data>> mList;
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

    public LiveData<List<ServicePaginationObject.Data>> getmList() {
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

    public LiveData<ServicePaginationObject> getmResponse(int page, String token) {
        if (mResponse == null) {
            isFirstLoading.postValue(true);
            isFirstError.postValue(false);
            mResponse = new MutableLiveData<>();
            mList = new MutableLiveData<>();
            getListOfServices(page, token);
        }
        return mResponse;
    }

    private void getListOfServices(int page, String token) {
        ServicesClient servicesClient = RetrofitApi.getInstance().getServicesClient();
        isLoading.postValue(true);
        Map<String, String> q = new HashMap<>();
        //f_params[orderBy][field]=price&f_params[orderBy][type]=ASC
        /*q.put("f_params[orderBy][field]", "price");
        q.put("f_params[orderBy][type]", "ASC");*/

        Call<ServicePaginationObject> services = servicesClient.getService(token, page);
        services.enqueue(new Callback<ServicePaginationObject>() {
            @Override
            public void onResponse(Call<ServicePaginationObject> call, Response<ServicePaginationObject> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        isFirstLoading.postValue(false);
                        isError.postValue(false);
                        isFirstError.postValue(false);
                        mResponse.postValue(response.body());

                        List<ServicePaginationObject.Data> list = mList.getValue();
                        if (list == null) {
                            list = new ArrayList<>();
                        }

                        setPage(response.body().getServices().getCurrent_page());
                        setLastPage(response.body().getServices().getLast_page());

                        if (page == 1) {
                            list.clear();
                        }
                        list.addAll(response.body().getServices().getData());
                        mList.postValue(list);
                    } else {
                        if (isFirstLoading.getValue() != null && isFirstLoading.getValue()) {
                            isFirstError.postValue(true);
                            isError.postValue(false);
                            isFirstLoading.postValue(false);
                            isLoading.postValue(false);
                        } else {
                            isFirstLoading.postValue(false);
                            isFirstError.postValue(false);
                            isError.postValue(true);
                            isLoading.postValue(false);
                        }
                    }
                } else {
                    if (isFirstLoading.getValue() != null && isFirstLoading.getValue()) {
                        isFirstError.postValue(true);
                        isError.postValue(false);
                        isFirstLoading.postValue(false);
                        isLoading.postValue(false);
                    } else {
                        isFirstLoading.postValue(false);
                        isFirstError.postValue(false);
                        isError.postValue(true);
                        isLoading.postValue(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServicePaginationObject> call, Throwable t) {
                if (isFirstLoading.getValue() != null && isFirstLoading.getValue()) {
                    isFirstError.postValue(true);
                    isError.postValue(false);
                    isFirstLoading.postValue(false);
                    isLoading.postValue(false);
                } else {
                    isFirstLoading.postValue(false);
                    isFirstError.postValue(false);
                    isError.postValue(true);
                    isLoading.postValue(false);
                }
            }
        });

    }

    public void refresh(String token) {
        isError.postValue(false);
        isFirstLoading.postValue(true);
        isFirstError.postValue(false);
        getListOfServices(1, token);
    }

    public void deleteService(int position) {
        List<ServicePaginationObject.Data> list = mList.getValue();
        if (list != null) {
            list.remove(position);
        }
        mList.postValue(list);
    }

    public void loadNextPage(int page, String token) {
        getListOfServices(page, token);
    }

    public void interestService(int position) {
        List<ServicePaginationObject.Data> list = mList.getValue();
        if (list != null && list.size() > 0) {
            ServicePaginationObject.Data data = list.get(position);
            if (data.getIs_interested()) {
                data.setIs_interested(false);
                data.setInterests(data.getInterests() - 1);
            } else {
                data.setIs_interested(true);
                data.setInterests(data.getInterests() + 1);
            }
            list.set(position, data);
        }
        mList.postValue(list);
    }
}
