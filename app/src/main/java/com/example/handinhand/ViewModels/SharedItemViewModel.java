package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.Models.ItemsPaginationObject;

public class SharedItemViewModel extends ViewModel {
    private final MutableLiveData<ItemsPaginationObject.Data> selected = new MutableLiveData<>();
    private final MutableLiveData<Integer> deleteAt = new MutableLiveData<>();
    private final MutableLiveData<Integer> requestAt = new MutableLiveData<>();


    public void select(ItemsPaginationObject.Data item) {
        selected.setValue(item);
    }

    public LiveData<ItemsPaginationObject.Data> getSelected() {
        return selected;
    }

    public void deleteAt(int i){
        deleteAt.postValue(i);
    }

    public LiveData<Integer> getDeleteAt() {
        return deleteAt;
    }

    public void setRequestAt(int i){
        deleteAt.postValue(i);
    }

    public LiveData<Integer> setRequestAt() {
        return deleteAt;
    }
}
