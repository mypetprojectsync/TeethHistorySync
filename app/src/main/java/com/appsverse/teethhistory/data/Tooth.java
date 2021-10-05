package com.appsverse.teethhistory.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

public class Tooth extends BaseObservable {

    public static final String IMPLANTED = "i";
    public static final String FILLED = "f";
    public static final String NORMAL = "g";
    public static final String NO_TOOTH = "";

    private int id;
    private int position;
    private String state;

    public Tooth(int id, int position, String state) {
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
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        notifyPropertyChanged(BR.state);
    }

    public void setState(boolean checked, String state) {

        if (checked) this.state = state;

        notifyPropertyChanged(BR.state);
    }
}
