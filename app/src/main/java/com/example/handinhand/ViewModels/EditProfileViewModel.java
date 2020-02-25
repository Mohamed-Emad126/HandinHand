package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.Models.Profile;

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<Profile.User> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDialogShowed = new MutableLiveData<>();
    private MutableLiveData<Boolean> isImageRemoved = new MutableLiveData<>();

    public LiveData<Boolean> getIsImageRemoved() {
        return isImageRemoved;
    }
    public LiveData<Boolean> getIsDialogShowed() {
        return isDialogShowed;
    }
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
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

    public void leave() {
        isDialogShowed.postValue(false);
        isLoading.postValue(false);
        isImageRemoved.postValue(false);
    }
}
