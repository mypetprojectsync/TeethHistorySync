package com.appsverse.teethhistory.viewModels;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.data.Event;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class EditEventViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private int position;
    private Date date;
    private String action;
    private int guarantee;
    private String notes;
    private List<String> actions;
    private List<String> photosUri;
    private List<String> photosListForDeleting;

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

    public void setPhotosListForDeleting(List<String> photosListForDeleting) {
        this.photosListForDeleting = photosListForDeleting;
    }

    public void addListToPhotosListToDeleting(List<String> photosListForDeleting){
        this.photosListForDeleting.addAll(photosListForDeleting);
    }

    public void removeItemFromListToPhotosListToDeleting(String uri) {
        this.photosListForDeleting.remove(uri);
    }

    public List<String> getPhotosListForDeleting() {
        return photosListForDeleting;
    }

    public List<String> getPhotosUri() {
        return photosUri;
    }

    public void setPhotosUri(List<String> photosUri) {
        this.photosUri = photosUri;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void onClickCancelButton(Event event, Context context) {

        ToothModel toothModel = getToothModel((MainActivity) context);
        EventModel eventModel = toothModel.getEventModels().where().equalTo("id", event.getId()).findFirst();

        RealmList<String>  eventModelRealmList = eventModel.getPhotosUri();
        List<String> photosUri = event.getPhotosUri();

        int amountOfNewPhotos = photosUri.size() - eventModelRealmList.size();

        for (int i = 1; i <= amountOfNewPhotos; i++) {
            File file = new File(photosUri.get(photosUri.size()-i));
            boolean isDeleted = file.delete();
            if (isDeleted) {
                Log.d(TAG, "file " + photosUri.get(photosUri.size()-i) + " deleted");
            } else {
                Log.d(TAG, "file " + photosUri.get(photosUri.size()-i) + " doesn't deleted");
            }
        }

        if (photosListForDeleting != null) photosListForDeleting.clear();

        setVisibilities(context);
    }

    public void onClickSaveButton(Event event, Context context) {
        MainActivity mainActivity = (MainActivity) context;

        ToothModel toothModel = getToothModel(mainActivity);

        EventModel eventModel = toothModel.getEventModels().where().equalTo("id", event.getId()).findFirst();

        String oldEventModelAction = eventModel.getAction();
        String newEventModelAction = event.getAction();

        List<String> clearList = new ArrayList<>();
        clearList.addAll(event.getPhotosUri());
        if (photosListForDeleting != null) clearList.removeAll(photosListForDeleting);
        event.setPhotosUri(clearList);

        realm.beginTransaction();

        eventModel.setDate(event.getDate());
        eventModel.setAction(event.getAction());
        eventModel.setGuarantee(event.getGuarantee());
        eventModel.setNotes(event.getNotes());

        RealmList<String>  eventModelRealmList = eventModel.getPhotosUri();

        eventModelRealmList.deleteAllFromRealm();
        eventModelRealmList.addAll(event.getPhotosUri());

        if (newEventModelAction.equals("Filled") || oldEventModelAction.equals("Filled")) {
            returnToothModelStateIfLastActionFilled(toothModel);
        }

        RealmResults<EventModel> eventModelsResults = toothModel.getEventModels().sort("date", Sort.DESCENDING,"id", Sort.DESCENDING);

        if (eventModelsResults.get(0).getId() == eventModel.getId()
                && !oldEventModelAction.equals(newEventModelAction)) {

            Log.d(TAG, "last event action: " + oldEventModelAction);
            switch (newEventModelAction) {
                case "Extracted":
                    toothModel.setExist(false);
                    toothModel.setFilling(false);

                    if (toothModel.isBabyTooth()) {
                        toothModel.setBabyTooth(false);
                        toothModel.setPermanentTooth(true);
                        toothModel.setPosition(toothModel.getPosition() - 40);
                        ((TextView) mainActivity.binding.getTeethFormulaFragment().binding.getRoot().findViewById(toothModel.getId())).setText(String.valueOf(toothModel.getPosition()));

                    } else if (toothModel.isPermanentTooth()) {
                        toothModel.setPermanentTooth(false);
                    } else if (toothModel.isImplant()) {
                        toothModel.setImplant(false);
                    }
                    break;
                case "Implanted":
                    toothModel.setExist(true);
                    toothModel.setImplant(true);
                    break;
                case "Grown":
                    toothModel.setExist(true);
                    break;
            }

            switch (oldEventModelAction) {
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

        realm.commitTransaction();

        setVisibilities(context);

        mainActivity.binding.getNewEventFragment().setTextActionACTV();
        mainActivity.binding.getTeethFormulaFragment().setTooth();



        if (photosListForDeleting != null) {
            deleteSelectedPhotos(context, event);
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

    private void setVisibilities(Context context) {
        MainActivity mainActivity = (MainActivity) context;

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mainActivity.binding.getViewData().setEventFragmentVisibilityData(View.GONE);
            mainActivity.binding.getViewData().setTeethFormulaFragmentVisibilityData(View.VISIBLE);

        } else {
            mainActivity.binding.getViewData().setEventsListFragmentVisibilityData(View.VISIBLE);
        }

        mainActivity.binding.getViewData().setEditEventFragmentVisibilityData(View.GONE);

        mainActivity.binding.getTeethFormulaFragment().refillEventsList();
        mainActivity.binding.getEventsListFragment().refillEventsList();
    }

    public ToothModel getToothModel(MainActivity mainActivity) {
        UserModel userModel = realm.where(UserModel.class).equalTo("id", mainActivity.user_id).findFirst();
        MainActivityViewModel mainActivityViewModel = mainActivity.binding.getModel();
        return userModel.getToothModels().where().equalTo("id", mainActivityViewModel.getChosenToothID()).findFirst();
    }

    public void deleteSelectedPhotos(Context context, Event event) {

        //todo check and delete in not main thread?

        UserModel userModel = realm.where(UserModel.class).equalTo("id", ((MainActivity) context).user_id).findFirst();
        RealmList<ToothModel> toothModels = userModel.getToothModels();

        int coincidenceCounter = 0;
        for (String uri : photosListForDeleting) {
        for (ToothModel toothModel : toothModels) {

            RealmList<EventModel> eventModels = toothModel.getEventModels();

            for (EventModel eventModel : eventModels) {

                    if (eventModel.getPhotosUri().contains(uri)) {
                        coincidenceCounter++;
                        break;
                    }
                }
            if (coincidenceCounter>1) break;
        }
            if (coincidenceCounter<2) {
                File file = new File(uri);
                boolean isDeleted = file.delete();
                Log.d(TAG, "file deleted: " + isDeleted + " uri: " + uri);
            }
        }
        photosListForDeleting.clear();
    }

}
