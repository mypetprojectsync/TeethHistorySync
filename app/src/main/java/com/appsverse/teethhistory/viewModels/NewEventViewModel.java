package com.appsverse.teethhistory.viewModels;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.Event;
import com.appsverse.teethhistory.fragments.TeethFormulaFragment;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import java.util.Date;

import io.realm.Realm;

public class NewEventViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private Date date = new Date();
    private String action = "Cleaning";
    private int guarantee = 12;
    private String notes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(int guarantee) {
        this.guarantee = guarantee;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void onClickCancelButton(Context context) {

        MainActivity mainActivity = (MainActivity) context;

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            mainActivity.binding.getViewData().setEventFragmentVisibilityData(View.GONE);
            mainActivity.binding.getViewData().setTeethFormulaFragmentVisibilityData(View.VISIBLE);
        } else {
            mainActivity.binding.getViewData().setEventFragmentVisibilityData(View.INVISIBLE);
        }

        mainActivity.binding.getViewData().setNewEventFragmentVisibilityData(View.GONE);
    }

    public void onClickSaveButton(Event event, Context context) {
        Number current_id = realm.where(EventModel.class).max("id");

        Log.d(TAG, "CreateNewUserViewModel max_user_id: " + current_id);

        int next_id;

        if (current_id == null) {
            next_id = 0;
        } else {
            next_id = current_id.intValue() + 1;
        }

        MainActivity mainActivity = (MainActivity) context;
        MainActivityViewModel mainActivityViewModel = mainActivity.binding.getModel();
        Log.d(TAG, "mainActivityViewModel.getChosenToothID(): " + mainActivityViewModel.getChosenToothID());
        ToothModel toothModel = realm.where(ToothModel.class).equalTo("id", mainActivityViewModel.getChosenToothID()).findFirst();

        realm.beginTransaction();
        EventModel eventModel = realm.createEmbeddedObject(EventModel.class, toothModel, "eventModels");
        eventModel.setId(next_id);
        eventModel.setDate(event.getDate());
        eventModel.setAction(event.getAction());
        eventModel.setGuarantee(event.getGuarantee());
        eventModel.setNotes(event.getNotes());

        switch (event.getAction()) {
            case "Extracting":
                toothModel.setExist(false);
                break;
            case "Filling":
                toothModel.setFilling(true);
                break;
            case "Implanting":
                toothModel.setImplant(true);
                break;
        }
        realm.commitTransaction();

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainActivity.binding.getViewData().setEventFragmentVisibilityData(View.GONE);
            mainActivity.binding.getViewData().setTeethFormulaFragmentVisibilityData(View.VISIBLE);
        } else {
            mainActivity.binding.getViewData().setEventFragmentVisibilityData(View.INVISIBLE);
        }
        mainActivity.binding.getViewData().setNewEventFragmentVisibilityData(View.GONE);

        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();

        TeethFormulaFragment fragment = (TeethFormulaFragment) fragmentManager.findFragmentById(R.id.teeth_formula_fragment);
        fragment.refillEventsList();
    }
}
