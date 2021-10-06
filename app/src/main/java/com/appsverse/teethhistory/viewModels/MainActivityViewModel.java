package com.appsverse.teethhistory.viewModels;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivityViewModel extends ViewModel implements Observable {

    final Realm realm = Realm.getDefaultInstance();

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private int user_id;
    private String username;

    private boolean isEditUsernameDialogActive;
    private boolean isDeleteUserDialogActive;

    private AlertDialog editUserDialog;
    private AlertDialog deleteUserDialog;

    private int chosenToothID = -1;

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

    public void setMainActivityViewModelData(int user_id) {

        UserModel userModel = realm.where(UserModel.class).equalTo("id", user_id).findFirst();

        if (userModel == null) userModel = realm.where(UserModel.class).findFirst();

        this.setUser_id(userModel.getId());
        this.setUsername(userModel.getName());
        this.setEditUsernameDialogActive(false);
        this.setDeleteUserDialogActive(false);
        this.setEditUserDialog(null);
        this.setDeleteUserDialog(null);
    }

    public void updateUsername(String name) {
        setUsername(name);

        realm.beginTransaction();
        realm.where(UserModel.class).equalTo("id", user_id).findFirst().setName(name);
        realm.commitTransaction();
    }

    public void deleteUser() {
        realm.beginTransaction();
        realm.where(UserModel.class).equalTo("id", user_id).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }

    public int getFirstUserID() {
        return realm.where(UserModel.class).findFirst().getId();
    }

    public String getUsernameFromRealm() {
        return realm.where(UserModel.class).equalTo("id", user_id).findFirst().getName();
    }

    @Bindable
    public int getTeethFormulaFragmentVisibility() {
        return teethFormulaFragmentVisibility;
    }

    public void setTeethFormulaFragmentVisibility(int teethFormulaFragmentVisibility) {
        this.teethFormulaFragmentVisibility = teethFormulaFragmentVisibility;
        notifyPropertyChanged(BR.teethFormulaFragmentVisibility);
    }

    @Bindable
    public int getNewEventFragmentVisibility() {
        return newEventFragmentVisibility;
    }

    public void setNewEventFragmentVisibility(int newEventFragmentVisibility) {
        this.newEventFragmentVisibility = newEventFragmentVisibility;
        notifyPropertyChanged(BR.newEventFragmentVisibility);
    }

    @Bindable
    public int getEditEventFragmentVisibilityData() {
        return editEventFragmentVisibilityData;
    }

    public void setEditEventFragmentVisibilityData(int editEventFragmentVisibilityData) {
        this.editEventFragmentVisibilityData = editEventFragmentVisibilityData;
        notifyPropertyChanged(BR.editEventFragmentVisibilityData);
    }

    @Bindable
    public int getEventFragmentVisibilityData() {
        return eventFragmentVisibilityData;
    }

    public void setEventFragmentVisibilityData(int eventFragmentVisibilityData) {
        this.eventFragmentVisibilityData = eventFragmentVisibilityData;
        notifyPropertyChanged(BR.eventFragmentVisibilityData);
    }

    @Bindable
    public int getEventsListFragmentVisibilityData() {
        return eventsListFragmentVisibilityData;
    }

    public void setEventsListFragmentVisibilityData(int eventsListFragmentVisibilityData) {
        this.eventsListFragmentVisibilityData = eventsListFragmentVisibilityData;
        notifyPropertyChanged(BR.eventsListFragmentVisibilityData);
    }

    public RealmResults<EventModel> getSortedEventsList() {
        UserModel userModel = realm.where(UserModel.class).equalTo("id", user_id).findFirst();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", chosenToothID).findFirst();
        return toothModel.getEventModels().sort("date", Sort.DESCENDING, "id", Sort.DESCENDING);
    }

    public void deleteEvent(EventModel eventModel, MainActivity mainActivity) {
        realm.beginTransaction();

        UserModel userModel = realm.where(UserModel.class).equalTo("id", user_id).findFirst();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", chosenToothID).findFirst();
        RealmResults<EventModel> eventModelsResults = toothModel.getEventModels().sort("date", Sort.DESCENDING, "id", Sort.DESCENDING);

        if (eventModelsResults.get(0).getId() == eventModel.getId()) {
            String lastAction = eventModelsResults.get(0).getAction();
            if (lastAction.equals(mainActivity.getResources().getString(R.string.extracted))) {

                toothModel.setState(ToothModel.NO_TOOTH);

                if (toothModel.getPosition() < 50)
                    toothModel.setPosition(toothModel.getPosition() + 40);

            } else if (lastAction.equals(mainActivity.getResources().getString(R.string.cleaned))
                    || lastAction.equals(mainActivity.getResources().getString(R.string.grown))
                    || lastAction.equals(mainActivity.getResources().getString(R.string.other))) {

                toothModel.setState(ToothModel.NORMAL);

            } else if (lastAction.equals(mainActivity.getResources().getString(R.string.filled))) {

                toothModel.setState(ToothModel.FILLED);

            } else if (lastAction.equals(mainActivity.getResources().getString(R.string.implanted))) {

                toothModel.setState(ToothModel.IMPLANTED);
            }
        }

        eventModel.deleteFromRealm();


        if (toothModel.getEventModels().size() == 0) {
            resetToothState(toothModel);

            mainActivity.binding.getTeethFormulaFragment().setPositionIVById(toothModel.getId(), toothModel.getPosition());

            mainActivity.binding.getTeethFormulaFragment().binding.getTooth().setState(toothModel.getState());
        }

        realm.commitTransaction();

        mainActivity.binding.getNewEventFragment().setTextActionACTV();
    }

    private void resetToothState(ToothModel toothModel) {

        switch (toothModel.getDefaultState()) {
            case ToothModel.NO_BABY_TOOTH:

                toothModel.setState(ToothModel.NO_TOOTH);

                if (toothModel.getPosition() < 50)
                    toothModel.setPosition(toothModel.getPosition() + 40);
                break;

            case ToothModel.NO_PERMANENT_TOOTH:

                toothModel.setState(ToothModel.NO_TOOTH);

                if (toothModel.getPosition() > 50)
                    toothModel.setPosition(toothModel.getPosition() - 40);
                break;

            case ToothModel.BABY_TOOTH:

                toothModel.setState(ToothModel.NORMAL);

                if (toothModel.getPosition() < 50)
                    toothModel.setPosition(toothModel.getPosition() + 40);
                break;

            case ToothModel.PERMANENT_TOOTH:

                toothModel.setState(ToothModel.NORMAL);

                if (toothModel.getPosition() > 50)
                    toothModel.setPosition(toothModel.getPosition() - 40);
                break;
        }
    }


    public String getDatabaseInJson() {
        List<UserModel> userModels = realm.where(UserModel.class).findAll();
        String json = new Gson().toJson(realm.copyFromRealm(userModels));
        return "{\"userModels\":" + json + "}";
    }


    public void copyJsonToRealm(String databaseInJson) {
        if (databaseInJson.length() > 0) {
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

            int next_id;

            next_id = current_id + 1;

            arrayItem.put("id", String.valueOf(next_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteUserPhotos() {

        UserModel userModel = realm.where(UserModel.class).equalTo("id", user_id).findFirst();
        RealmList<ToothModel> toothModels = userModel.getToothModels();

        for (ToothModel toothModel : toothModels) {
            RealmList<EventModel> eventModels = toothModel.getEventModels();

            for (EventModel eventModel : eventModels) {
                RealmList<String> photosUri = eventModel.getPhotosUri();

                for (String uri : photosUri) {
                    if (!checkUriInOtherEvents(uri)) {
                        File file = new File(uri);
                        file.delete();
                    }
                }

            }
        }
    }

    private boolean checkUriInOtherEvents(String uri) {

        int coincidenceCounter = 0;

        List<UserModel> userModels = realm.where(UserModel.class).findAll();

        for (UserModel userModel : userModels) {
            RealmList<ToothModel> toothModels = userModel.getToothModels();

            for (ToothModel toothModel : toothModels) {

                RealmList<EventModel> eventModels = toothModel.getEventModels();

                for (EventModel eventModel : eventModels) {

                    if (eventModel.getPhotosUri().contains(uri)) {
                        coincidenceCounter++;
                        if (coincidenceCounter > 1) return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}
