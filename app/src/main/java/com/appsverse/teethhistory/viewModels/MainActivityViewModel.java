package com.appsverse.teethhistory.viewModels;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.repository.UserModel;

import io.realm.Realm;

public class MainActivityViewModel extends ViewModel {

    final String TAG = "myLogs";
    Realm realm = Realm.getDefaultInstance();

    private UserModel user;

    public UserModel getUser() {
        return realm.where(UserModel.class).findFirst();
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public boolean isUserExist() {
        return (realm.where(UserModel.class).findFirst() != null);
    }

}
