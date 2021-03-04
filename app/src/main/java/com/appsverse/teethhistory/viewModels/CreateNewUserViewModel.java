package com.appsverse.teethhistory.viewModels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.repository.UserModel;

import java.util.List;

import io.realm.Realm;

public class CreateNewUserViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private String name = "";
    private boolean isBabyTeeth = false;

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

    public boolean isBabyTeeth() {
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        this.isBabyTeeth = babyTeeth;
    }

    public void onClickSaveButton(User user, Context context) {

        Number current_id = realm.where(UserModel.class).max("id");

        Log.d(TAG, "CreateNewUserViewModel max_user_id: " + current_id);

        int next_id;

        if (current_id == null) {
            next_id = 0;
        } else {
            next_id = current_id.intValue() + 1;
        }

            Log.d(TAG, "start writing new user to database");
            realm.beginTransaction();
            UserModel userModel = realm.createObject(UserModel.class, next_id);
            userModel.setName(user.getName());
            userModel.setBabyTeeth(user.isBabyTeeth());
            realm.commitTransaction();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("chosen_user_id", next_id);
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        ((Activity) context).finish();
    }

    public void onClickCancelButton(Context context) {
        ((Activity) context).finish();
    }

    //todo нужна ли проверка на уникальность?
    private void isUniqueName(User user) {

    }
}
