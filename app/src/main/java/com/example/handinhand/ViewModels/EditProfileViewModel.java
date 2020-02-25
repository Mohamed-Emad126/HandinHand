package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDialogShowed = new MutableLiveData<>();


    public LiveData<Boolean> getIsDialogShowed() {
        return isDialogShowed;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
