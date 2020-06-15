package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.Models.ServicePaginationObject;

public class ServiceSharedViewModel extends ViewModel {
    private final MutableLiveData<ServicePaginationObject.Data> selected = new MutableLiveData<>();
    private final MutableLiveData<Integer> deleteAt = new MutableLiveData<>();
    private final MutableLiveData<Integer> interestAt = new MutableLiveData<>();


    public void select(ServicePaginationObject.Data item) {
        selected.setValue(item);
    }

    public void interestSelected(){
        ServicePaginationObject.Data value = selected.getValue();
        if(value != null && value.getIs_interested()){
            value.setInterests(value.getInterests() -1);
            value.setIs_interested(false);
        }
        else if(value != null){
            value.setInterests(value.getInterests() +1);
            value.setIs_interested(true);
        }
        selected.postValue(value);
    }

    public LiveData<ServicePaginationObject.Data> getSelected() {
        return selected;
    }

    public void deleteAt(int i){
        deleteAt.postValue(i);
    }

    public LiveData<Integer> getDeleteAt() {
        return deleteAt;
    }

    public void setInterestAt(int i){
        interestAt.postValue(i);
    }

    public LiveData<Integer> getInterestAt() {
        return interestAt;
    }
}
