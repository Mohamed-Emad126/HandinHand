package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.handinhand.API.ItemsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.ItemsPaginationObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsViewModel extends ViewModel {
    MutableLiveData<ItemsPaginationObject> mResponse;
    MutableLiveData<Integer> page = new MutableLiveData<>();
    MutableLiveData<Integer> lastPage = new MutableLiveData<>();
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isFirstError = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<List<ItemsPaginationObject.Data>> mList;

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

    public LiveData<List<ItemsPaginationObject.Data>> getmList() {
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

    public LiveData<ItemsPaginationObject> getmResponse(int page) {
        if(mResponse == null){
            isFirstLoading.postValue(true);
            isFirstError.postValue(false);
            mList = new MutableLiveData<>();
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
                if(response.isSuccessful()){
                    if(response.body() != null && response.body().getStatus()){
                        isFirstLoading.postValue(false);
                        isError.postValue(false);
                        mResponse.postValue(response.body());

                        List<ItemsPaginationObject.Data> list = mList.getValue();

                        setPage(response.body().getItems().getCurrent_page());
                        setLastPage(response.body().getItems().getLast_page());

                        if(page ==1){
                            list.clear();
                        }
                        list.addAll(response.body().getItems().getData());
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
            public void onFailure(Call<ItemsPaginationObject> call, Throwable t) {
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
        getListOfItems(1);
    }


    public void loadNextPage(int page){
        getListOfItems(page);
    }
}
