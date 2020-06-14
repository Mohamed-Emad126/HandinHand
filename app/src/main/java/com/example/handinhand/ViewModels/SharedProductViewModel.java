package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.Models.ProductPaginationObject;

public class SharedProductViewModel extends ViewModel {
    private final MutableLiveData<ProductPaginationObject.Data> selected = new MutableLiveData<>();
    private final MutableLiveData<Integer> deleteAt = new MutableLiveData<>();
    private final MutableLiveData<Integer> requestAt = new MutableLiveData<>();


    public void select(ProductPaginationObject.Data item) {
        selected.setValue(item);
    }

    public LiveData<ProductPaginationObject.Data> getSelected() {
        return selected;
    }

    public void deleteAt(int i) {
        deleteAt.postValue(i);
    }

    public LiveData<Integer> getDeleteAt() {
        return deleteAt;
    }

    public void setRequestAt(int i) {
        requestAt.postValue(i);
    }

    public LiveData<Integer> getRequestAt() {
        return requestAt;
    }
}
