package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.handinhand.API.MainActivityClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.ApiErrors;
import com.example.handinhand.Models.LoginInfo;
import com.example.handinhand.Models.LoginResponse;
import com.example.handinhand.Models.RegisterResponse;
import com.example.handinhand.Models.ResetPasswordResponse;
import com.example.handinhand.Models.SendResetPasswordEmailResponse;
import com.example.handinhand.Utils.ErrorUtils;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<LoginResponse> mResponse;
    private MutableLiveData<RegisterResponse> registerResponse;
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isForgetError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRegisterError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isResetError = new MutableLiveData<>();
    private MutableLiveData<SendResetPasswordEmailResponse> resetPasswordEmailResponse;

    private MutableLiveData<ResetPasswordResponse> resetPasswordResponse;

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<Boolean> getIsResetError() {
        return isResetError;
    }

    public LiveData<Boolean> getIsRegisterError() {
        return isRegisterError;
    }

    public LiveData<Boolean> getIsForgetError() {
        return isForgetError;
    }

    public LiveData<LoginResponse> getUser(LoginInfo login) {
        if(mResponse == null){
            isError.setValue(false);
            mResponse = new MutableLiveData<>();
            error = new MutableLiveData<>();
            loadUser(login);
        }
        return mResponse;
    }

    private void loadUser(LoginInfo login) {

        MainActivityClient client = RetrofitApi.getInstance().getMainActivityClient();
        Call<LoginResponse> call = client.login(login);

        isLoading.setValue(true);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                isLoading.setValue(false);
                if (response.body() != null && response.isSuccessful()) {

                    if(response.body().getLogin()!=null){
                        mResponse.setValue(response.body());
                    }
                    else{
                        isError.setValue(response.body().getStatus());
                    }
                }
                else{
                    isError.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                isLoading.setValue(false);
                isError.setValue(true);
            }
        });
    }



    /**
     * in Register Fragment we Create New User
     * @param mUser the user information
     * @param image the image of the user
     * @return the user after completing the registration
     */
    public LiveData<RegisterResponse> createUser(HashMap<String, RequestBody> mUser, MultipartBody.Part image){

        if(registerResponse == null){
            registerResponse = new MutableLiveData<>();
            error = new MutableLiveData<>();
            isError.setValue(false);
            isRegisterError.setValue(false);
            sendInfoToServer(mUser, image);
        }

        return registerResponse;
    }

    private void sendInfoToServer(HashMap<String, RequestBody> mUser, MultipartBody.Part image) {
        MainActivityClient client = RetrofitApi.getInstance()
                .getMainActivityClient();
        Call<RegisterResponse> call = client.register(mUser, image);
        isLoading.setValue(true);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    registerResponse.setValue(response.body());

                    if(response.body() != null){
                        isRegisterError.setValue(response.body().getStatus());
                    }
                    else{
                        isRegisterError.setValue(true);
                    }

                }
                else{
                    ApiErrors errors = ErrorUtils.parseError(response);
                    error.setValue(errors.getError().getEmail().get(0));
                    isRegisterError.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                isLoading.setValue(false);
                isRegisterError.setValue(true);
            }
        });
    }

    public LiveData<String> getError(){
        return error;
    }

    public LiveData<RegisterResponse> getRegisterResponse(){
        return registerResponse;
    }



    public LiveData<SendResetPasswordEmailResponse> sendResetEmail(HashMap<String, String> email){
        if(resetPasswordEmailResponse == null){
            isForgetError.setValue(false);
            resetPasswordEmailResponse = new MutableLiveData<>();
            send(email);
        }
        return resetPasswordEmailResponse;
    }

    private void send(HashMap<String, String> email){
        MainActivityClient client = RetrofitApi.getInstance().getMainActivityClient();
        Call<SendResetPasswordEmailResponse> call = client.sendResetPasswordRequest(email);


        isLoading.setValue(true);

        call.enqueue(new Callback<SendResetPasswordEmailResponse>() {
            @Override
            public void onResponse(Call<SendResetPasswordEmailResponse> call,
                                   Response<SendResetPasswordEmailResponse> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    resetPasswordEmailResponse.setValue(response.body());
                }
                else{
                    isForgetError.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<SendResetPasswordEmailResponse> call, Throwable t) {
                isLoading.setValue(false);
                isForgetError.setValue(true);
            }
        });
    }





    public LiveData<ResetPasswordResponse> resetPassword(HashMap<String, String> info){
        if(resetPasswordResponse == null){
            isResetError.setValue(false);
            resetPasswordResponse = new MutableLiveData<>();
            reset(info);
        }
        return resetPasswordResponse;
    }

    private void reset(HashMap<String, String> info){
        MainActivityClient client = RetrofitApi.getInstance().getMainActivityClient();
        Call<ResetPasswordResponse> call = client.resetPassword(info);


        isLoading.setValue(true);

        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call,
                                   Response<ResetPasswordResponse> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    resetPasswordResponse.setValue(response.body());
                }
                else{
                    isResetError.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                isResetError.setValue(true);
                isLoading.setValue(false);
            }
        });
    }


    /**
     * restore the ViewModel after leaving any fragment that share Mutable live data
     * like isError or Error or user.
     */
    public void leave(){
        error = null;
        mResponse = null;
        registerResponse = null;
        resetPasswordEmailResponse = null;
        resetPasswordResponse = null;
        isResetError.setValue(false);
        isForgetError.setValue(false);
        isRegisterError.setValue(false);
        isLoading.setValue(false);
        isError.setValue(false);
    }
}
