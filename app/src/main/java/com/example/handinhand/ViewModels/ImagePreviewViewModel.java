package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImagePreviewViewModel extends ViewModel {

    private MutableLiveData<String> url = new MutableLiveData<>();

    public LiveData<String> getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url.setValue(url);
    }
}
