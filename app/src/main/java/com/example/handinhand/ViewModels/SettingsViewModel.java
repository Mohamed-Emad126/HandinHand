package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.API.SettingsClient;
import com.example.handinhand.Models.AskForVerificationResponse;
import com.example.handinhand.Models.EmailVerificationResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsViewModel extends ViewModel {
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isLoadingAsk = new MutableLiveData<>();
    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<Boolean> isErrorAsk = new MutableLiveData<>();
    MutableLiveData<EmailVerificationResponse> verify = new MutableLiveData<>();
    MutableLiveData<AskForVerificationResponse> ask = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoadingAsk() {
        return isLoadingAsk;
    }

    public LiveData<Boolean> getIsErrorAsk() {
        return isErrorAsk;
    }

    public LiveData<AskForVerificationResponse> getAsk() {
        return ask;
    }

    public void getAsk(String token,
                       HashMap<String, RequestBody> details,
                       MultipartBody.Part image){
        isLoadingAsk.postValue(true);
        SettingsClient settingsClient = RetrofitApi.getInstance().getSettingsClient();
        settingsClient.askForTrustedAccount(token, details, image).enqueue(
                new Callback<AskForVerificationResponse>() {
                    @Override
                    public void onResponse(Call<AskForVerificationResponse> call, Response<AskForVerificationResponse> response) {
                        isLoadingAsk.postValue(false);
                        if(response.isSuccessful() && response.body() != null && response.body().getStatus()){
                            ask.postValue(response.body());
                            isErrorAsk.postValue(false);
                        }
                        else{
                            isErrorAsk.postValue(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<AskForVerificationResponse> call, Throwable t) {
                        isErrorAsk.postValue(true);
                    }
                }
        );
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<EmailVerificationResponse> getVerify() {
        return verify;
    }

    public void getVerify(String token) {
        isLoading.postValue(true);
        SettingsClient settingsClient = RetrofitApi.getInstance().getSettingsClient();
        settingsClient.verifyEmail(token).enqueue(new Callback<EmailVerificationResponse>() {
            @Override
            public void onResponse(Call<EmailVerificationResponse> call, Response<EmailVerificationResponse> response) {
                isLoading.postValue(false);
                if(response.isSuccessful() &&
                        response.body() != null &&
                        response.body().getStatus()){
                    isError.postValue(false);
                    verify.postValue(response.body());
                }
                else{
                    isError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<EmailVerificationResponse> call, Throwable t) {
                isLoading.postValue(false);
                isError.postValue(true);
            }
        });
    }
}
