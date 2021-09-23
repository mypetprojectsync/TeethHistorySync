package com.appsverse.teethhistory.viewModels;

import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.data.Tooth;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class TeethFormulaFragmentViewModel extends ViewModel {
    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int chosenToothID;
    private int chosenToothPosition;
    private int layoutVisibility = -1;


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

    public int getLayoutVisibility() {
        return layoutVisibility;
    }

    public void setLayoutVisibility(int layoutVisibility) {
        this.layoutVisibility = layoutVisibility;
    }

    public List<ToothModel> getAllToothModelsForUser(int user_id){
        UserModel userModel = realm.where(UserModel.class).equalTo("id",user_id).findFirst();
        return userModel.getToothModels();
    }

    public  List<EventModel> getEventModelsList(int user_id, Tooth tooth){

            UserModel userModel = realm.where(UserModel.class).equalTo("id",user_id).findFirst();
            ToothModel toothModel = userModel.getToothModels().where().equalTo("id", tooth.getId()).findFirst();
            return toothModel.getEventModels().sort("date", Sort.DESCENDING, "id", Sort.DESCENDING);
    }

    public void deleteEvent(EventModel eventModel, MainActivity mainActivity){
        realm.beginTransaction();

        UserModel userModel = realm.where(UserModel.class).equalTo("id",mainActivity.user_id).findFirst();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", mainActivity.binding.getModel().getChosenToothID()).findFirst();
        int maxEventId = 0;
        RealmResults<EventModel> eventModelsResults = toothModel.getEventModels().sort("date", Sort.DESCENDING,"id", Sort.DESCENDING);

        //todo use last date and last position

        if (eventModel.getAction().equals("Filled")) {
            returnToothModelStateIfLastActionFilled(toothModel);
        }

        if (eventModelsResults.get(0).getId() == eventModel.getId()) removeToothState(eventModel, toothModel,mainActivity);

        if (toothModel.getEventModels().size() == 1) {
            resetToothState(userModel, toothModel);
            ((TextView) mainActivity.binding.getTeethFormulaFragment().binding.getRoot().findViewById(toothModel.getId())).setText(String.valueOf(toothModel.getPosition()));
        }

        eventModel.deleteFromRealm();
        realm.commitTransaction();

        mainActivity.binding.getNewEventFragment().setTextActionACTV();
    }

    private void resetToothState(UserModel userModel, ToothModel toothModel) {

        if (userModel.isNoTeeth()) {
            toothModel.setExist(false);
            toothModel.setPermanentTooth(false);
            toothModel.setBabyTooth(true);

            if ((toothModel.getPosition() > 10 && toothModel.getPosition() < 16)
            || (toothModel.getPosition() > 20 && toothModel.getPosition() < 26)
            || (toothModel.getPosition() > 30 && toothModel.getPosition() < 36)
            || (toothModel.getPosition() > 40 && toothModel.getPosition() < 46)){
                toothModel.setPosition(toothModel.getPosition()+40);
            }
        } else if (userModel.isBabyTeeth()) {
            toothModel.setExist(true);
            toothModel.setPermanentTooth(false);
            toothModel.setBabyTooth(true);

            if ((toothModel.getPosition() > 10 && toothModel.getPosition() < 16)
                    || (toothModel.getPosition() > 20 && toothModel.getPosition() < 26)
                    || (toothModel.getPosition() > 30 && toothModel.getPosition() < 36)
                    || (toothModel.getPosition() > 40 && toothModel.getPosition() < 46)){
                toothModel.setPosition(toothModel.getPosition()+40);

            }

        } else {
            toothModel.setExist(true);
            toothModel.setPermanentTooth(true);
            toothModel.setBabyTooth(false);
        }

        toothModel.setFilling(false);
        toothModel.setImplant(false);
    }

    private void removeToothState(EventModel eventModel, ToothModel toothModel, MainActivity mainActivity) {

        switch (eventModel.getAction()) {
            case "Extracted":
                returnToothModelStateIfLastActionExtracted(toothModel, mainActivity);
                break;
            case "Implanted":
                returnToothModelStateIfLastActionImplanted(toothModel);
                break;
            case "Grown":
                returnToothModelStateIfLastActionGrown(toothModel);
        }
    }

    private void returnToothModelStateIfLastActionGrown(ToothModel toothModel) {
        toothModel.setExist(false);
    }

    private void returnToothModelStateIfLastActionImplanted(ToothModel toothModel) {
        toothModel.setExist(false);
        toothModel.setImplant(false);
    }

    private void returnToothModelStateIfLastActionFilled(ToothModel toothModel) {
        //todo!! check all lists when could been have babytooth or permanenttooth filling and true if find one (or two?)

        RealmList<EventModel> eventsList = toothModel.getEventModels();

        if (toothModel.isBabyTooth()) {
            if (eventsList.where().equalTo("action", "Filled").findAll().size() > 0) {
                toothModel.setFilling(true);
            } else {
                toothModel.setFilling(false);
            }
        } else if (toothModel.isPermanentTooth()) {
            int amountOfPermanentToothFillingEvents = 0;
            for (EventModel eventModel : eventsList.where().equalTo("action", "Filled").findAll()) {
                if (eventModel.getId() / 1000 < 50) amountOfPermanentToothFillingEvents++;
            }
            if (amountOfPermanentToothFillingEvents > 0) {
                toothModel.setFilling(true);
            } else {
                toothModel.setFilling(false);
            }
        }
    }

    private void returnToothModelStateIfLastActionExtracted(ToothModel toothModel, MainActivity mainActivity) {

        toothModel.setExist(true);

        if (!toothModel.isBabyTooth() && !toothModel.isPermanentTooth()) {
            toothModel.setPermanentTooth(true);
        } else if (!toothModel.isBabyTooth() && toothModel.isPermanentTooth()) {
            toothModel.setBabyTooth(true);
            toothModel.setPermanentTooth(false);
            toothModel.setPosition(toothModel.getPosition() + 40);

            ((TextView) mainActivity.binding.getTeethFormulaFragment().binding.getRoot().findViewById(toothModel.getId())).setText(String.valueOf(toothModel.getPosition()));

        } else if (!toothModel.isBabyTooth()) {
            toothModel.setBabyTooth(false);
        }
    }

    public ToothModel getToothModel(MainActivity mainActivity){
        UserModel userModel = realm.where(UserModel.class).equalTo("id", mainActivity.user_id).findFirst();
        MainActivityViewModel mainActivityViewModel = mainActivity.binding.getModel();
        return userModel.getToothModels().where().equalTo("id", mainActivityViewModel.getChosenToothID()).findFirst();
    }



    public Tooth setTooth(Tooth tooth, MainActivity activity) {
        ToothModel toothModel = getToothModel(activity);

        tooth.setId(toothModel.getId());
        tooth.setPosition(toothModel.getPosition());
        tooth.setExist(toothModel.isExist());
        tooth.setBabyTooth(toothModel.isBabyTooth());
        tooth.setPermanentTooth(toothModel.isPermanentTooth());
        tooth.setFilling(toothModel.isFilling());
        tooth.setImplant(toothModel.isImplant());

        return tooth;
    }

    public void saveTooth(Tooth tooth, MainActivity activity) {
        ToothModel toothModel = getToothModel(activity);

        realm.beginTransaction();

        toothModel.setPosition(tooth.getPosition());
        toothModel.setExist(tooth.isExist());
        toothModel.setBabyTooth(tooth.isBabyTooth());
        toothModel.setPermanentTooth(tooth.isPermanentTooth());
        toothModel.setFilling(tooth.isFilling());
        toothModel.setImplant(tooth.isImplant());

        realm.commitTransaction();
    }

}