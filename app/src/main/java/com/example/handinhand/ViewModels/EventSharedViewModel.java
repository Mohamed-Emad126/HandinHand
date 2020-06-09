package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.Models.EventPaginationObject;

public class EventSharedViewModel extends ViewModel {
    private final MutableLiveData<EventPaginationObject.Data> selected =
            new MutableLiveData<>();

    public void select(EventPaginationObject.Data item) {
        selected.setValue(item);
    }

    public LiveData<EventPaginationObject.Data> getSelected() {
        return selected;
    }

}
