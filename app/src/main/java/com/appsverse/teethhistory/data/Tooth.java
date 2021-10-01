package com.appsverse.teethhistory.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

public class Tooth extends BaseObservable {

    public static final int IMPLANTED = 30;
    public static final int FILLED = 20;
    public static final int NORMAL = 10;
    public static final int NO_TOOTH = 0;

    private int id;
    private int position;

    private int state;

    public Tooth(int id, int position, int state) {
        setId(id);
        setPosition(position);
        setState(state);
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

    public void setPosition(boolean checked, int position) {
        if (checked) this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        notifyPropertyChanged(BR.state);
    }

    public void setState(boolean checked, int state) {

        if (checked) this.state = state;

        notifyPropertyChanged(BR.state);
    }
}
