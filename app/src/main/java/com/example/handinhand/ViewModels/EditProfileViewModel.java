package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.API.ProfileClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.Models.ProfileUpdateResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<Profile.User> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDialogShowed = new MutableLiveData<>();
    private MutableLiveData<Boolean> isImageRemoved = new MutableLiveData<>();
    private MutableLiveData<ProfileUpdateResponse> mResponse;

    public LiveData<Boolean> getIsImageRemoved() {
        return isImageRemoved;
    }
    public LiveData<Boolean> getIsDialogShowed() {
        return isDialogShowed;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<Boolean> getIsError() {
        return isError;
    }
    public LiveData<Profile.User> getUser() {
        return user;
    }

    public void setUser(Profile.User user) {
        this.user.postValue(user);
    }
    public void setIsDialogShowed(Boolean isDialogShowed) {
        this.isDialogShowed.postValue(isDialogShowed);
    }
    public void setIsImageRemoved(Boolean isImageRemoved) {
        this.isImageRemoved.postValue(isImageRemoved);
    }

    public LiveData<ProfileUpdateResponse> getResponse(String token,
                                                       HashMap<String, RequestBody> updates,
                                                       MultipartBody.Part image
    ) {
        if(mResponse == null){
            mResponse = new MutableLiveData<>();
            isLoading.postValue(true);
            updateUser(token, updates, image);
            return mResponse;
        }
        //isLoading.postValue(false);
        return mResponse;
    }

    private void updateUser(String token, HashMap<String, RequestBody> updates,
                            MultipartBody.Part image) {
        ProfileClient client = RetrofitApi.getInstance().getProfileClient();

        Call<ProfileUpdateResponse> call;

        if(image != null){
             call = client.updateProfileInfoWithImage(token, updates, image);
        }
        else{
            call = client.updateProfileInfo(token, updates);
        }

        call.enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                isLoading.postValue(false);
                if(response.isSuccessful()){
                    mResponse.postValue(response.body());
                }
                else{
                    isError.postValue(true);
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                isLoading.postValue(false);
                isError.postValue(true);

            }
        });

    }

    public void leave() {
        isDialogShowed.postValue(false);
        isError.postValue(false);
        isLoading.postValue(false);
        isImageRemoved.postValue(false);
        user = new MutableLiveData<>();
        mResponse = null;
    }
}
