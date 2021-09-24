package com.appsverse.teethhistory.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

public class Tooth extends BaseObservable {

    private int id;
    private int position;
    private boolean isExist = true;
    private boolean isBabyTooth = false;
    private boolean isPermanentTooth = true;
    private boolean isFilling = false;
    private boolean isImplant = false;

    public Tooth(int id, int position) {
        setId(id);
        setPosition(position);
        setExist(true);
        setBabyTooth(false);
        setPermanentTooth(true);
        setFilling(false);
        setImplant(false);
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
}
