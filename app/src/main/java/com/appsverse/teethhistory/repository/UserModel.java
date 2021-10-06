package com.appsverse.teethhistory.repository;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserModel extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;

    private RealmList<ToothModel> toothModels;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<ToothModel> getToothModels() {
        return toothModels;
    }

    public void setToothModels(RealmList<ToothModel> toothModels) {
        this.toothModels = toothModels;
    }
}
