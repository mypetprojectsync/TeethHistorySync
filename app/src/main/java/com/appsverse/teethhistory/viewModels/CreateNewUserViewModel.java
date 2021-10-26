package com.appsverse.teethhistory.viewModels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import io.realm.Realm;

public class CreateNewUserViewModel extends ViewModel {

    final Realm realm = Realm.getDefaultInstance();

    public static final int NO_TEETH = 0;
    public static final int BABY_TEETH = 1;
    public static final int PERMANENT_TEETH = 2;

    private int id;
    private String name = "";

    private int userState = PERMANENT_TEETH;

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

    public int getUserState() {
        return userState;
    }

    public void setUserState(int userState) {
        this.userState = userState;
    }

    public void onClickSaveButton(Context context) {
        if (getName().trim().length() > 0) {
            Number current_id = realm.where(UserModel.class).max("id");

            int next_id;

            if (current_id == null) {
                next_id = 0;
            } else {
                next_id = current_id.intValue() + 1;
            }

            realm.beginTransaction();
            UserModel userModel = realm.createObject(UserModel.class, next_id);
            userModel.setName(getName().trim());
            realm.commitTransaction();

            switch (userState) {

                case NO_TEETH:
                setNoTeethToothModels(userModel);
                break;

                case BABY_TEETH:
                setBabyTeethToothModels(userModel);
                break;

                case PERMANENT_TEETH:
                setPermanentTeethToothModels(userModel);
                break;
            }

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("chosen_user_id", next_id);
            editor.apply();

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            realm.close();
        }

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
        toothModel.setDefaultState(ToothModel.PERMANENT_TOOTH);
        toothModel.setState(ToothModel.NORMAL);
    }

    private void setNoPermanentToothModel(ToothModel toothModel, int i) {
        toothModel.setId(i);
        toothModel.setPosition(i);
        toothModel.setDefaultState(ToothModel.NO_PERMANENT_TOOTH);
        toothModel.setState(ToothModel.NO_TOOTH);
    }


    private void setBabyToothModel(ToothModel toothModel, int i) {
        toothModel.setId(i);

        //add 40 to get baby tooth index from permanent tooth index
        toothModel.setPosition(i+40);

        toothModel.setDefaultState(ToothModel.BABY_TOOTH);
        toothModel.setState(ToothModel.NORMAL);
    }

    private void setNoBabyToothModel(ToothModel toothModel, int i) {
        toothModel.setId(i);

        //add 40 to get baby tooth index from permanent tooth index
        toothModel.setPosition(i+40);

        toothModel.setDefaultState(ToothModel.NO_BABY_TOOTH);
        toothModel.setState(ToothModel.NO_TOOTH);
    }

    public void onClickCancelButton(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int user_id = sharedPreferences.getInt("chosen_user_id", -1);

        if (user_id == -1) {
            ((Activity) context).finishAffinity();
        } else {
            ((Activity) context).finish();
        }
    }
}
