package com.appsverse.teethhistory.viewModels;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.repository.UserModel;

import io.realm.Realm;

import static androidx.databinding.library.baseAdapters.BR.babyTeeth;

public class CreateNewUserViewModel extends ViewModel {

    final String TAG = "myLogs";
    Realm realm = Realm.getDefaultInstance();

    private String name = "";
    private boolean isBabyTeeth = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBabyTeeth() {
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        this.isBabyTeeth = babyTeeth;
    }

    public void onClickSaveButton(User user, Context context) {

        Number current_id = realm.where(UserModel.class).max("id");
        int next_id;

        if (current_id == null) {
            next_id = 0;
        } else {
            next_id = current_id.intValue() + 1;
        }

        if (realm.where(UserModel.class).contains("name", user.getName()).findFirst() != null) {
            Toast.makeText(context, R.string.input_unique_name, Toast.LENGTH_SHORT).show();
        } else {
            realm.beginTransaction();
            UserModel userModel = realm.createObject(UserModel.class, next_id);
            userModel.setName(user.getName());
            userModel.setBabyTeeth(user.isBabyTeeth());
            realm.commitTransaction();
        }
    }

    private void isUniqueName(User user) {

    }
}
