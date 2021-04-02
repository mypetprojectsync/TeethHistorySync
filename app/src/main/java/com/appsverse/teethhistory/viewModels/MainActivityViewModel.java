package com.appsverse.teethhistory.viewModels;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.data.Tooth;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivityViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();


    private int user_id;
    private String username;
    private boolean isBabyTeeth;
    private boolean isNoTeeth;

    private boolean isEditUsernameDialogActive;
    private boolean isDeleteUserDialogActive;

    private AlertDialog editUserDialog;
    private AlertDialog deleteUserDialog;

    private int chosenToothID = -1;

    //private int teethFormulaFragmentVisibility = -1;
    private int teethFormulaFragmentVisibility = View.VISIBLE;
    private int newEventFragmentVisibility = View.GONE;
    private int editEventFragmentVisibilityData = View.GONE;
    private int eventFragmentVisibilityData = View.GONE;
    private int eventsListFragmentVisibilityData = View.GONE;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isBabyTeeth() {
        return isBabyTeeth;
    }

    public void setBabyTeeth(boolean babyTeeth) {
        isBabyTeeth = babyTeeth;
    }

    public boolean isEditUsernameDialogActive() {
        return isEditUsernameDialogActive;
    }

    public void setEditUsernameDialogActive(boolean editUsernameDialogActive) {
        isEditUsernameDialogActive = editUsernameDialogActive;
    }

    public boolean isDeleteUserDialogActive() {
        return isDeleteUserDialogActive;
    }

    public void setDeleteUserDialogActive(boolean deleteUserDialogActive) {
        isDeleteUserDialogActive = deleteUserDialogActive;
    }

    public AlertDialog getEditUserDialog() {
        return editUserDialog;
    }

    public void setEditUserDialog(AlertDialog editUserDialog) {
        this.editUserDialog = editUserDialog;
    }


    public AlertDialog getDeleteUserDialog() {
        return deleteUserDialog;
    }

    public void setDeleteUserDialog(AlertDialog deleteUserDialog) {
        this.deleteUserDialog = deleteUserDialog;
    }

    public boolean isUserExist() {
        return (realm.where(UserModel.class).findFirst() != null);
    }

    public List<UserModel> getAllUsers() {
        return realm.where(UserModel.class).findAll();
    }

    public int getChosenToothID() {
        return chosenToothID;
    }

    public void setChosenToothID(int chosenToothID) {
        this.chosenToothID = chosenToothID;
    }

    public void setMainActivityViewModelData(int user_id){

        Log.d(TAG, "setMainActivityViewModelData user_id: " + user_id);

        UserModel userModel = realm.where(UserModel.class).equalTo("id", user_id).findFirst();
        this.setUser_id(userModel.getId());
        this.setUsername(userModel.getName());
        this.setBabyTeeth(userModel.isBabyTeeth());
        this.setEditUsernameDialogActive(false);
        this.setDeleteUserDialogActive(false);
        this.setEditUserDialog(null);
        this.setDeleteUserDialog(null);
    }

    public void updateUsername(String name){
        setUsername(name);

        realm.beginTransaction();
        realm.where(UserModel.class).equalTo("id", user_id).findFirst().setName(name);
        realm.commitTransaction();
    }

    public void deleteUser(){
        realm.beginTransaction();
        realm.where(UserModel.class).equalTo("id", user_id).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }

    public int getFirstUserID(){
        return realm.where(UserModel.class).findFirst().getId();
    }

    public String getUsernameFromRealm(){
        return realm.where(UserModel.class).equalTo("id", user_id).findFirst().getName();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

    }

    public int getTeethFormulaFragmentVisibility() {
        return teethFormulaFragmentVisibility;
    }

    public void setTeethFormulaFragmentVisibility(int teethFormulaFragmentVisibility) {
        this.teethFormulaFragmentVisibility = teethFormulaFragmentVisibility;
    }

    public int getNewEventFragmentVisibility() {
        return newEventFragmentVisibility;
    }

    public void setNewEventFragmentVisibility(int newEventFragmentVisibility) {
        this.newEventFragmentVisibility = newEventFragmentVisibility;
    }

    public int getEditEventFragmentVisibilityData() {
        return editEventFragmentVisibilityData;
    }

    public void setEditEventFragmentVisibilityData(int editEventFragmentVisibilityData) {
        this.editEventFragmentVisibilityData = editEventFragmentVisibilityData;
    }

    public int getEventFragmentVisibilityData() {
        return eventFragmentVisibilityData;
    }

    public void setEventFragmentVisibilityData(int eventFragmentVisibilityData) {
        this.eventFragmentVisibilityData = eventFragmentVisibilityData;
    }

    public int getEventsListFragmentVisibilityData() {
        return eventsListFragmentVisibilityData;
    }

    public void setEventsListFragmentVisibilityData(int eventsListFragmentVisibilityData) {
        this.eventsListFragmentVisibilityData = eventsListFragmentVisibilityData;
    }

    public boolean isNoTeeth() {
        return isNoTeeth;
    }

    public void setNoTeeth(boolean noTeeth) {
        isNoTeeth = noTeeth;
    }

    public RealmResults<EventModel> getSortedEventsList(MainActivity mainActivity) {
        UserModel userModel = realm.where(UserModel.class).equalTo("id",user_id).findFirst();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", chosenToothID).findFirst();
        return toothModel.getEventModels().sort("date", Sort.DESCENDING, "id", Sort.DESCENDING);
    }

    public void deleteEvent(EventModel eventModel, MainActivity mainActivity) {
        realm.beginTransaction();

        UserModel userModel = realm.where(UserModel.class).equalTo("id", user_id).findFirst();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", chosenToothID).findFirst();
        int maxEventId = 0;
        RealmResults<EventModel> eventModelsResults = toothModel.getEventModels().sort("date", Sort.DESCENDING,"id", Sort.DESCENDING);

        //todo use last date and last position
        /*for (EventModel eventModel1 : toothModel.getEventModels()){
            if (eventModel1.getId()%1000 > maxEventId%1000) maxEventId = eventModel1.getId();
        }*/

        if (eventModel.getAction().equals("Filled")) {
            returnToothModelStateIfLastActionFilled(toothModel);
        }

        Log.d(TAG, "maxEventId: " + maxEventId%1000 + " event id: " + eventModel.getId()%1000);
        //if (maxEventId%1000 == eventModel.getId()%1000) removeToothState(eventModel, toothModel,mainActivity);
        if (eventModelsResults.get(0).getId() == eventModel.getId()) removeToothState(eventModel, toothModel, mainActivity);

        if (toothModel.getEventModels().size() == 1) {
            resetToothState(userModel, toothModel);
            ((TextView) mainActivity.binding.getTeethFormulaFragment().binding.getRoot().findViewById(toothModel.getId())).setText(String.valueOf(toothModel.getPosition()));
        }

        eventModel.deleteFromRealm();
        realm.commitTransaction();

        mainActivity.binding.getNewEventFragment().setTextActionACTV();

        Log.d(TAG, "tooth state after delete" + toothModel.toString());
        //todo hide editEventFragment if delete event which opened on new event fragment
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
        Log.d(TAG, "removeToothState");
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
        //todo optimize this method
        //toothModel.setFilling(false);
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

    public String getDatabaseInJson(ActivityMainBinding binding) {
        //UserModel userModel = realm.where(UserModel.class).equalTo("id", binding.getUser().getId()).findFirst();
        List<UserModel> userModels = realm.where(UserModel.class).findAll();
        String json = new Gson().toJson(realm.copyFromRealm(userModels));
        //Log.d(TAG, "Json:\n" + json);
        //Log.d(TAG, "Json size: " + json);
        return "{\"userModels\":"+json+"}";
    }



    public void copyJsonToRealm(String databaseInJson) {
        if (databaseInJson.length()>0) {
            try {
                JSONObject jsonObject = new JSONObject(databaseInJson);

                JSONArray jsonArray = jsonObject.getJSONArray("userModels");

                int current_id = realm.where(UserModel.class).max("id").intValue();

                for (int i = 0; i < jsonArray.length(); i++) {
                    setPrimaryKey(jsonArray, i, current_id);
                    current_id++;
                }
                realm.beginTransaction();

                realm.createAllFromJson(UserModel.class, jsonArray);
                realm.commitTransaction();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPrimaryKey(JSONArray jsonArray, int i, int current_id) {
        try {
            JSONObject arrayItem = jsonArray.getJSONObject(i);


            Log.d(TAG, "CreateNewUserViewModel max_user_id: " + current_id);

            int next_id;

           // if (current_id == 0) {
          //      next_id = 0;
           // } else {
                next_id = current_id + 1;
           // }

            arrayItem.put("id", String.valueOf(next_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /*public void setToothState() {
        UserModel userModel = realm.where(UserModel.class).equalTo("id",user_id).findFirst();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", chosenToothID).findFirst();

        if (eventModelsResults.get(0).getId() == eventModel.getId()) {
            switch (event.getAction()) {
                case "Extracted":
                    toothModel.setExist(false);
                    toothModel.setFilling(false);

                    if (toothModel.isBabyTooth()) {
                        toothModel.setBabyTooth(false);
                        toothModel.setPermanentTooth(true);
                        //todo!!! change chosenToothId and toothId

                        toothModel.setPosition(toothModel.getPosition() - 40);
                        TextView chosenTV = mainActivity.binding.getTeethFormulaFragment().binding.getRoot().findViewById(toothModel.getId());
                        chosenTV.setText(String.valueOf(toothModel.getPosition()));

                    } else if (toothModel.isPermanentTooth()) {
                        toothModel.setPermanentTooth(false);
                    } else if (toothModel.isImplant()) {
                        toothModel.setImplant(false);
                    }
                    break;
                case "Filled":
                    toothModel.setFilling(true);
                    break;
                case "Implanted":
                    toothModel.setExist(true);
                    toothModel.setImplant(true);
                    break;
                case "Grown":
                    toothModel.setExist(true);
                    break;
            }
        }
    }*/

   /* public ToothModel getChosenTooth(MainActivity mainActivity){
        UserModel userModel = realm.where(UserModel.class).equalTo("id",mainActivity.user_id).findFirst();
        return userModel.getToothModels().where().equalTo("id", mainActivity.binding.getModel().getChosenToothID()).findFirst();
    }
*/
    /*public void resetTooth(MainActivity mainActivity, Tooth tooth) {
        ToothModel toothModel = getChosenTooth(mainActivity);
        realm.beginTransaction();

        toothModel.setFilling(false);
        toothModel.setExist(tooth.isExist());
        toothModel.setBabyTooth(tooth.isBabyTooth());
        toothModel.setPermanentTooth(tooth.isPermanentTooth());
        toothModel.setImplant(false);
        toothModel.getEventModels().clear();
        realm.commitTransaction();
    }*/
}
