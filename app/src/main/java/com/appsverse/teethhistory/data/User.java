package com.appsverse.teethhistory.data;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;


public class User extends BaseObservable {

    final String TAG = "myLogs";

    private int id;
    private String name;
    private boolean isNoTeeth = false;
    private boolean isBabyTeeth = false;

    public User(String name, boolean isNoTeeth, boolean isBabyTeeth) {
        setName(name);
        setNoTeeth(isNoTeeth);
        setBabyTeeth(isBabyTeeth);
    }


    @Bindable
    public String getName() {

        Log.d(TAG, "getName user: " + name);
        return name;
    }

    public void setName(String name) {
        Log.d(TAG, "setName user: " + name);
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isNoTeeth() {
        return isNoTeeth;
    }

    public void setNoTeeth(boolean noTeeth) {
        isNoTeeth = noTeeth;
        notifyPropertyChanged(BR.noTeeth);
    }

    @Bindable
    public boolean isBabyTeeth() {
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        this.isBabyTeeth = babyTeeth;
        notifyPropertyChanged(BR.babyTeeth);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
