package com.appsverse.teethhistory.viewModels;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.data.Event;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import java.util.Date;

import io.realm.Realm;

public class EditEventViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private Date date;
    private String action;
    private int guarantee;
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

        mainActivity.binding.getViewData().setEditEventFragmentVisibilityData(View.GONE);
    }

    public void onClickSaveButton(Event event, Context context) {

        MainActivity mainActivity = (MainActivity) context;
        UserModel userModel = realm.where(UserModel.class).equalTo("id", mainActivity.user_id).findFirst();

        MainActivityViewModel mainActivityViewModel = mainActivity.binding.getModel();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", mainActivityViewModel.getChosenToothID()).findFirst();

        EventModel eventModel = toothModel.getEventModels().where().equalTo("id", event.getId()).findFirst();

        realm.beginTransaction();

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

        mainActivity.binding.getViewData().setEditEventFragmentVisibilityData(View.GONE);

        mainActivity.binding.getTeethFormulaFragment().refillEventsList();
    }
}
