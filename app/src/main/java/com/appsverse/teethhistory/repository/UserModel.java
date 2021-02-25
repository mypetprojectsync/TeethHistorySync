package com.appsverse.teethhistory.repository;

import android.util.Log;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserModel extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private boolean isBabyTeeth;

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

    public boolean isBabyTeeth() {
        Log.d("myLogs", "realm isBabyTeeth " + isBabyTeeth);
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        Log.d("myLogs", "realm setBabyTeeth " + babyTeeth);
        isBabyTeeth = babyTeeth;
    }

}
