package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.ProductsClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.AddProductResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<AddProductResponse> mResponse = null;

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<AddProductResponse> getmResponse(String token,
                                                     HashMap<String, RequestBody> itemInfo,
                                                     MultipartBody.Part image) {
        if (mResponse == null) {
            isLoading.postValue(true);
            mResponse = new MutableLiveData<>();
            addProduct(token, itemInfo, image);
            return mResponse;
        } else {
            return mResponse;
        }
    }

    private void addProduct(String token, HashMap<String, RequestBody> itemInfo,
                         MultipartBody.Part image) {
        ProductsClient productsClient = RetrofitApi.getInstance().getProductsClient();
        Call<AddProductResponse> addItemResponseCall = productsClient.addProduct(token, itemInfo, image);

        addItemResponseCall.enqueue(new Callback<AddProductResponse>() {
            @Override
            public void onResponse(Call<AddProductResponse> call, Response<AddProductResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    mResponse.postValue(response.body());
                    isError.postValue(false);
                } else {
                    isError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<AddProductResponse> call, Throwable t) {
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
