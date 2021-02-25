package com.appsverse.teethhistory.data;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

public class User extends BaseObservable {

    final String TAG = "myLogs";

    private String name;
    private boolean isBabyTeeth = false;

    public User(String name, boolean isBabyTeeth) {
        setName(name);
        setBabyTeeth(isBabyTeeth);
    }

    @Bindable
    public String getName() {

    Log.d(TAG,"getName user: " + name);
    return name;
    }

    public void setName(String name) {
        Log.d(TAG,"setName user: " + name);
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isBabyTeeth() {
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        this.isBabyTeeth = babyTeeth;
        notifyPropertyChanged(BR.babyTeeth);
    }

}
