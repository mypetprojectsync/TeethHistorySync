package com.appsverse.teethhistory.repository;

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

    public boolean isNoTeeth() {
        return isNoTeeth;
    }

    public void setNoTeeth(boolean noTeeth) {
        isNoTeeth = noTeeth;
    }

    public boolean isBabyTeeth() {
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        isBabyTeeth = babyTeeth;
    }

    public RealmList<ToothModel> getToothModels() {
        return toothModels;
    }

    public void setToothModels(RealmList<ToothModel> toothModels) {
        this.toothModels = toothModels;
    }


}
