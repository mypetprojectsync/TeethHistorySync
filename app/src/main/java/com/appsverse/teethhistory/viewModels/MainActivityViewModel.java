package com.appsverse.teethhistory.viewModels;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.repository.UserModel;

import java.util.List;

import io.realm.Realm;

public class MainActivityViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();


    private int user_id;
    private String username;
    private boolean isBabyTeeth;

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
}
