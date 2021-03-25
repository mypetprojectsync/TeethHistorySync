package com.appsverse.teethhistory.repository;

import android.util.Log;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserModel extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private boolean isNoTeeth;
    private boolean isBabyTeeth;
    private RealmList<ToothModel> toothModels;

    public int getId() {
        Log.d("myLogs", "realm getID " + id);
        return id;
    }

    public void setId(int id) {
        Log.d("myLogs", "realm setID " + id);
        this.id = id;
    }

    public String getName() {
        Log.d("myLogs", "realm getName " + name);
        return name;
    }

    public void setName(String name) {
        Log.d("myLogs", "realm setName " + name);
        this.name = name;
    }

    public boolean isNoTeeth() {
        return isNoTeeth;
    }

    public void setNoTeeth(boolean noTeeth) {
        isNoTeeth = noTeeth;
    }

    public boolean isBabyTeeth() {
        Log.d("myLogs", "realm isBabyTeeth " + isBabyTeeth);
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        Log.d("myLogs", "realm setBabyTeeth " + babyTeeth);
        isBabyTeeth = babyTeeth;
    }

    public RealmList<ToothModel> getToothModels() {
        return toothModels;
    }

    public void setToothModels(RealmList<ToothModel> toothModels) {
        this.toothModels = toothModels;
    }


}
