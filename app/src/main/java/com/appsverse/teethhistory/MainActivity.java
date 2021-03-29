package com.appsverse.teethhistory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.appsverse.teethhistory.data.MainActivityViewData;
import com.appsverse.teethhistory.data.Tooth;
import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.fragments.EditEventFragment;
import com.appsverse.teethhistory.fragments.EventsListFragment;
import com.appsverse.teethhistory.fragments.NewEventFragment;
import com.appsverse.teethhistory.fragments.TeethFormulaFragment;
import com.appsverse.teethhistory.handlers.OnClickHandler;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.MainActivityViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    final String TAG = "myLogs";

    MainActivityViewModel model;
    public ActivityMainBinding binding;
    MainActivityViewData mainActivityViewData;

    public int user_id;

    //todo добавить проверку на существование user_id в базе, если нет, то загружать первого юзера
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = sharedPreferences.getInt("chosen_user_id", -1);

        model = new ViewModelProvider(this).get(MainActivityViewModel.class);
        if (user_id >= 0 && model.getUsername() == null) {
            Log.d(TAG, "onCreate if (user_id >= 0 && model.getUsername() == null) user_id: " + user_id);
            model.setMainActivityViewModelData(user_id);
        }

        if (model.isUserExist()) {
            Log.d(TAG, "model.isUserExist()");

            binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            binding.setModel(model);

            User user = new User(model.getUsername(), model.isNoTeeth(), model.isBabyTeeth());
            binding.setUser(user);

            TeethFormulaFragment fragment = (TeethFormulaFragment) getSupportFragmentManager().findFragmentById(R.id.teeth_formula_fragment);
            binding.setTeethFormulaFragment(fragment);

            EditEventFragment editEventFragment = (EditEventFragment) getSupportFragmentManager().findFragmentById(R.id.edit_event_fragment);
            binding.setEditEventFragment(editEventFragment);

            EventsListFragment eventsListFragment = (EventsListFragment) getSupportFragmentManager().findFragmentById(R.id.events_list_fragment);
            binding.setEventsListFragment(eventsListFragment);

            NewEventFragment newEventFragment = (NewEventFragment) getSupportFragmentManager().findFragmentById(R.id.new_event_fragment);
            binding.setNewEventFragment(newEventFragment);

            mainActivityViewData = new MainActivityViewData(
                    model.getTeethFormulaFragmentVisibility(),
                    model.getNewEventFragmentVisibility(),
                    model.getEditEventFragmentVisibilityData(),
                    model.getEventFragmentVisibilityData(),
                    model.getEventsListFragmentVisibilityData());
            binding.setViewData(mainActivityViewData);

            OnClickHandler handler = new OnClickHandler();
            handler.onMainActivityClick(binding, model);


            if (model.isEditUsernameDialogActive()) {
                OnClickHandler onClickHandler = new OnClickHandler();
                onClickHandler.createEditUserNameDialog(binding);
            }

            if (model.isDeleteUserDialogActive()) {
                OnClickHandler onClickHandler = new OnClickHandler();
                onClickHandler.deleteUser(binding, model);
            }

            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                mainActivityViewData.setTeethFormulaFragmentVisibilityData(View.VISIBLE);
                mainActivityViewData.setEventFragmentVisibilityData(View.VISIBLE);

                if (model.getNewEventFragmentVisibility() == View.GONE
                        && model.getEditEventFragmentVisibilityData() == View.GONE) {
                    mainActivityViewData.setEventsListFragmentVisibilityData(View.VISIBLE);
                }

            } else {

                if (model.getNewEventFragmentVisibility() == View.VISIBLE || model.getEditEventFragmentVisibilityData() == View.VISIBLE) {
                    mainActivityViewData.setTeethFormulaFragmentVisibilityData(View.GONE);
                    mainActivityViewData.setEventFragmentVisibilityData(View.VISIBLE);
                } else {
                    binding.getViewData().setTeethFormulaFragmentVisibilityData(View.VISIBLE);
                    mainActivityViewData.setEventFragmentVisibilityData(View.GONE);
                }
                mainActivityViewData.setEventsListFragmentVisibilityData(View.GONE);
            }

        } else {
            Log.d(TAG, "model.isUserExist() == false");
            Intent intent = new Intent(this, CreateNewUserActivity.class);
            this.startActivity(intent);
        }


    }

    public ActivityMainBinding getBinding() {
        return this.binding;
    }

    /*public void resetTooth() {

        DialogResetToothBinding dialogResetToothBinding = DataBindingUtil.setContentView(this, R.layout.dialog_reset_tooth);

        Tooth tooth = new Tooth(model.getChosenToothID(), 0);
        dialogResetToothBinding.setTooth(tooth);

        model.resetTooth(this, tooth);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setView(dialogResetToothBinding.getRoot());
        dialogBuilder.setTitle("Reset tooth #"+ model.getChosenToothID()+" state");
        dialogBuilder.setPositiveButton("ok", (dialog, which) -> {

            model.resetTooth(this, tooth);

        });
        dialogBuilder.setNegativeButton("cancel", (dialog, which) -> {
        });
        dialogBuilder.show();
    }*/

    public RealmResults<EventModel> getSortedEventsList(){
        return model.getSortedEventsList(this);
    }

    public void deleteEvent(EventModel eventModel) {
        model.deleteEvent(eventModel, this);
    }

    public void setToothState(){
       // model.setToothState();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (model.getEditUserDialog() != null) model.getEditUserDialog().dismiss();
        if (model.getDeleteUserDialog() != null) model.getDeleteUserDialog().dismiss();

        if (mainActivityViewData != null) {
            model.setTeethFormulaFragmentVisibility(mainActivityViewData.getTeethFormulaFragmentVisibilityData());
            model.setNewEventFragmentVisibility(mainActivityViewData.getNewEventFragmentVisibilityData());
            model.setEditEventFragmentVisibilityData(mainActivityViewData.getEditEventFragmentVisibilityData());
            model.setEventFragmentVisibilityData(mainActivityViewData.getEventFragmentVisibilityData());
            model.setEventsListFragmentVisibilityData(mainActivityViewData.getEventsListFragmentVisibilityData());
        }
    }
}