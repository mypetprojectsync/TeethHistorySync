package com.appsverse.teethhistory.repository;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class ToothModel extends RealmObject {

    private int id;
    private int position;
    private boolean isExist;
    private boolean isBabyTooth;
    private boolean isPermanentTooth;
    private boolean isFilling;
    private boolean isImplant;
    private RealmList<EventModel> eventModels;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public boolean isBabyTooth() {
        return isBabyTooth;
    }

    public void setBabyTooth(boolean babyTooth) {
        isBabyTooth = babyTooth;
    }

    public boolean isFilling() {
        return isFilling;
    }

    public void setFilling(boolean filling) {
        isFilling = filling;
    }

    public boolean isImplant() {
        return isImplant;
    }

    public void setImplant(boolean implant) {
        isImplant = implant;
    }

    public RealmList<EventModel> getEventModels() {
        return eventModels;
    }

    public void setEventModels(RealmList<EventModel> eventModels) {
        this.eventModels = eventModels;
    }

    public boolean isPermanentTooth() {
        return isPermanentTooth;
    }

    public void setPermanentTooth(boolean permanentTooth) {
        isPermanentTooth = permanentTooth;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
