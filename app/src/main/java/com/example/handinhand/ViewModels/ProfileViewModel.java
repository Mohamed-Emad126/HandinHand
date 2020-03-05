package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.handinhand.API.ProfileClient;
import com.example.handinhand.API.RetrofitApi;
import com.example.handinhand.Models.Profile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private MutableLiveData<Profile> mProfile;

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError.postValue(isError);
    }

    public LiveData<Profile> getProfile(String token){
        if(mProfile == null){
            mProfile = new MutableLiveData<>();
            loadProfile(token);
            return mProfile;
        }
        isLoading.postValue(false);
        return mProfile;
    }

    private void loadProfile(String token) {
        ProfileClient client = RetrofitApi.getInstance().getProfileClient();
        Call<Profile> call = client.getProfile(token);
        isLoading.postValue(true);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    Profile profile = response.body();

                    if(profile.getDetails().getUser() != null){
                        mProfile.postValue(profile);
                        isError.postValue(false);
                    }
                }
                else{
                    isError.postValue(true);
                    isLoading.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                isError.postValue(true);
                isLoading.postValue(false);
            }
        });

    }

    public void refresh(String token){
        isLoading.postValue(true);
        isError.postValue(false);
        loadProfile(token);
    }

    public void leave(){
        isError.setValue(false);
        isLoading.setValue(false);
        mProfile = null;
    }


}
