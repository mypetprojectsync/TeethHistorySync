package com.appsverse.teethhistory.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

public class Tooth extends BaseObservable {

    final String TAG = "myLogs";

    private int id;
    private int position;
    private boolean isExist = true;
    private boolean isBabyTooth = false;
    private boolean isPermanentTooth = true;

   // public Tooth(int id) {
    public Tooth(int id, int position) {
        setId(id);
        setPosition(position);
        setExist(true);
        setBabyTooth(false);
        setPermanentTooth(true);
    }

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean isExist) {
        this.isExist = isExist;
        notifyPropertyChanged(BR.exist);
    }

    @Bindable
    public boolean isBabyTooth() {
        return isBabyTooth;
    }

    public void setBabyTooth(boolean isBabyTooth) {
        this.isBabyTooth = isBabyTooth;
        notifyPropertyChanged(BR.babyTooth);
    }


    @Bindable
    public boolean isPermanentTooth() {
        return isPermanentTooth;
    }

    public void setPermanentTooth(boolean isPermanentTooth) {
        this.isPermanentTooth = isPermanentTooth;
        notifyPropertyChanged(BR.permanentTooth);
    }
}
