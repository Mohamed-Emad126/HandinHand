package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.Models.Deal;

public class SharedDealViewModel extends ViewModel {
    private final MutableLiveData<Deal> selected = new MutableLiveData<>();


    public void select(Deal item) {
        selected.setValue(item);
    }

    public LiveData<Deal> getSelected() {
        return selected;
    }
}
