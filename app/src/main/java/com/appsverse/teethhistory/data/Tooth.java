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
    private boolean isExist = true;
    private boolean isBabyTooth = false;
    private boolean isPermanentTooth = true;
    private boolean isFilling = false;
    private boolean isImplant = false;

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
        if (isBabyTooth && position>10 && position<50) setPosition(position+40);
        this.isBabyTooth = isBabyTooth;
        notifyPropertyChanged(BR.babyTooth);
    }


    @Bindable
    public boolean isPermanentTooth() {
        return isPermanentTooth;
    }

    public void setPermanentTooth(boolean isPermanentTooth) {
        if (isPermanentTooth && position>50 && position<90) setPosition(position-40);
        this.isPermanentTooth = isPermanentTooth;
        notifyPropertyChanged(BR.permanentTooth);
    }

    @Bindable
    public boolean isFilling() {
        return isFilling;
    }

    public void setFilling(boolean filling) {
        if (filling) setImplant(false);
        this.isFilling = filling;
        notifyPropertyChanged(BR.filling);
    }

    @Bindable
    public boolean isImplant() {
        return isImplant;
    }

    public void setImplant(boolean implant) {
        if (implant) setFilling(false);
        this.isImplant = implant;
        notifyPropertyChanged(BR.implant);
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
