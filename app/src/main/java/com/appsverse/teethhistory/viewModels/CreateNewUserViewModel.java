package com.appsverse.teethhistory.viewModels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import io.realm.Realm;

public class CreateNewUserViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private String name = "";
    private boolean isNoTeeth = false;
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
        userModel.setNoTeeth(user.isNoTeeth());
        userModel.setBabyTeeth(user.isBabyTeeth());
        realm.commitTransaction();

        if (user.isNoTeeth()) {
            setNoTeethToothModels(userModel);
        } else if (user.isBabyTeeth()) {
            setBabyTeethToothModels(userModel);
        } else {
            setPermanentTeethToothModels(userModel);
        }

           Log.d(TAG, ""+ realm.where(ToothModel.class).findAll());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("chosen_user_id", next_id);
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        realm.close();
        ((Activity) context).finish();
    }

    private void setBabyTeethToothModels(UserModel userModel) {
        for (int i = 18; i > 15; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 15; i > 10; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 21; i < 26; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 26; i < 29; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }

        for (int i = 48; i > 45; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 45; i > 40; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 31; i < 36; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 36; i < 39; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
    }

    private void setNoTeethToothModels(UserModel userModel) {
        for (int i = 18; i > 15; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 15; i > 10; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 21; i < 26; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 26; i < 29; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }

        for (int i = 48; i > 45; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 45; i > 40; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 31; i < 36; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoBabyToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 36; i < 39; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setNoPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
    }

    private void setPermanentTeethToothModels(UserModel userModel) {

        for (int i = 18; i > 10; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 21; i < 29; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 48; i > 40; i--) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }
        for (int i = 31; i < 39; i++) {
            realm.beginTransaction();
            ToothModel toothModel = realm.createEmbeddedObject(ToothModel.class, userModel, "toothModels");
            setPermanentToothModel(toothModel, i);
            realm.commitTransaction();
        }

    }

    private void setPermanentToothModel(ToothModel toothModel, int i) {
        toothModel.setId(i);
        toothModel.setPosition(i);
        toothModel.setExist(true);
        toothModel.setBabyTooth(false);
        toothModel.setPermanentTooth(true);
        toothModel.setFilling(false);
        toothModel.setImplant(false);
    }

    private void setNoPermanentToothModel(ToothModel toothModel, int i) {
        toothModel.setId(i);
        toothModel.setPosition(i);
        toothModel.setExist(false);
        toothModel.setBabyTooth(false);
        toothModel.setPermanentTooth(true);
        toothModel.setFilling(false);
        toothModel.setImplant(false);
    }


    private void setBabyToothModel(ToothModel toothModel, int i) {
        toothModel.setId(i);

        //add 40 to get baby tooth index from permanent tooth index
        toothModel.setPosition(i+40);

        toothModel.setExist(true);
        toothModel.setBabyTooth(true);
        toothModel.setPermanentTooth(false);
        toothModel.setFilling(false);
        toothModel.setImplant(false);
    }

    private void setNoBabyToothModel(ToothModel toothModel, int i) {
        toothModel.setId(i);

        //add 40 to get baby tooth index from permanent tooth index
        toothModel.setPosition(i+40);

        toothModel.setExist(false);
        toothModel.setBabyTooth(true);
        toothModel.setPermanentTooth(false);
        toothModel.setFilling(false);
        toothModel.setImplant(false);
    }


    public void onClickCancelButton(Context context) {
        ((Activity) context).finish();
    }

    public boolean isNoTeeth() {
        return isNoTeeth;
    }

    public void setNoTeeth(boolean noTeeth) {
        isNoTeeth = noTeeth;
    }
}
