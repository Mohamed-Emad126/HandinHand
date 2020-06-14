package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.ProductsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.ProductPaginationObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsViewModel extends ViewModel {
    private MutableLiveData<ProductPaginationObject> mResponse;
    private MutableLiveData<Integer> page = new MutableLiveData<>();
    private MutableLiveData<Integer> lastPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFirstError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<List<ProductPaginationObject.Data>> mList;
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

    public LiveData<List<ProductPaginationObject.Data>> getmList() {
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

    public LiveData<ProductPaginationObject> getmResponse(int page, String token) {
        if (mResponse == null) {
            isFirstLoading.postValue(true);
            isFirstError.postValue(false);
            mResponse = new MutableLiveData<>();
            mList = new MutableLiveData<>();
            getListOfItems(page, token);
        }
        return mResponse;
    }

    private void getListOfItems(int page, String token) {
        ProductsClient client = RetrofitApi.getInstance().getProductsClient();
        isLoading.postValue(true);

        Call<ProductPaginationObject> call = client.getProducts(token, page);
        call.enqueue(new Callback<ProductPaginationObject>() {
            @Override
            public void onResponse(Call<ProductPaginationObject> call,
                                   Response<ProductPaginationObject> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        isFirstLoading.postValue(false);
                        isError.postValue(false);
                        isFirstError.postValue(false);
                        mResponse.postValue(response.body());

                        List<ProductPaginationObject.Data> list = mList.getValue();
                        if (list == null) {
                            list = new ArrayList<>();
                        }

                        setPage(response.body().getProducts().getCurrent_page());
                        setLastPage(response.body().getProducts().getLast_page());

                        if (page == 1) {
                            list.clear();
                        }
                        list.addAll(response.body().getProducts().getData());
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
            public void onFailure(Call<ProductPaginationObject> call, Throwable t) {
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
        getListOfItems(1, token);
    }

    public void deleteItem(int position) {
        List<ProductPaginationObject.Data> list = mList.getValue();
        if (list != null) {
            list.remove(position);
        }
        mList.postValue(list);
    }

    public void loadNextPage(int page, String token) {
        getListOfItems(page, token);
    }
}
