package com.example.handinhand.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handinhand.Models.EventPaginationObject;

public class EventSharedViewModel extends ViewModel {
    private final MutableLiveData<EventPaginationObject.Data> selected = new MutableLiveData<>();
    private final MutableLiveData<Integer> deleteAt = new MutableLiveData<>();
    private final MutableLiveData<Integer> interestAt = new MutableLiveData<>();


    public void select(EventPaginationObject.Data item) {
        selected.setValue(item);
    }

    public void interestSelected(){
        EventPaginationObject.Data value = selected.getValue();
        if(value != null && value.getIs_interested()){
            value.setInterests(value.getInterests() -1);
        }
        else if(value != null){
            value.setInterests(value.getInterests() +1);
        }
        value.setIs_interested(!value.getIs_interested());
        selected.postValue(value);
    }

    public LiveData<EventPaginationObject.Data> getSelected() {
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
