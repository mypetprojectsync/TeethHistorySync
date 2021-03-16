package com.appsverse.teethhistory.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

public class Tooth extends BaseObservable {

    final String TAG = "myLogs";

    private int id;

    public Tooth(int id) {
        setId(id);
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }
}
