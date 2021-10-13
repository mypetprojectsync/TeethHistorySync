package com.appsverse.teethhistory.viewModels;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.data.Tooth;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import java.util.List;

import io.realm.Realm;

public class TeethFormulaFragmentViewModel extends ViewModel {

    final Realm realm = Realm.getDefaultInstance();

    private int chosenToothID;
    private int chosenToothPosition;
    private String chosenToothState;

    private int eventsListSelectedPosition;

    public int getChosenToothID() {
        return chosenToothID;
    }

    public void setChosenToothID(int chosenToothID) {
        this.chosenToothID = chosenToothID;
    }


    public int getChosenToothPosition() {
        return chosenToothPosition;
    }

    public void setChosenToothPosition(int chosenToothPosition) {
        this.chosenToothPosition = chosenToothPosition;
    }

    public List<ToothModel> getAllToothModelsForUser(int user_id) {
        UserModel userModel = realm.where(UserModel.class).equalTo("id", user_id).findFirst();
        return userModel.getToothModels();
    }

    public ToothModel getToothModel(MainActivity mainActivity) {
        UserModel userModel = realm.where(UserModel.class).equalTo("id", mainActivity.user_id).findFirst();
        MainActivityViewModel mainActivityViewModel = mainActivity.binding.getModel();
        return userModel.getToothModels().where().equalTo("id", mainActivityViewModel.getChosenToothID()).findFirst();
    }


    public Tooth setTooth(Tooth tooth, MainActivity activity) {
        ToothModel toothModel = getToothModel(activity);

        tooth.setId(toothModel.getId());
        tooth.setPosition(toothModel.getPosition());
        tooth.setState(toothModel.getState());

        return tooth;
    }

    public void saveTooth(Tooth tooth, MainActivity activity) {
        ToothModel toothModel = getToothModel(activity);

        realm.beginTransaction();

        toothModel.setPosition(tooth.getPosition());
        toothModel.setState(tooth.getState());

        realm.commitTransaction();
    }

    public String getChosenToothState() {
        return chosenToothState;
    }

    public void setChosenToothState(String chosenToothState) {
        this.chosenToothState = chosenToothState;
    }

    public int getEventsListSelectedPosition() {
        return eventsListSelectedPosition;
    }

    public void setEventsListSelectedPosition(int eventsListSelectedPosition) {
        this.eventsListSelectedPosition = eventsListSelectedPosition;
    }
}