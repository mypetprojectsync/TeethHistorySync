package com.appsverse.teethhistory.viewModels;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
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
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class NewEventViewModel extends ViewModel {

    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private int position;
    private Date date = new Date();
    private String action;
    private int guarantee = 12;
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

    public void onClickCancelButton(Event event, Context context) {

        List<String> photosUri = event.getPhotosUri();


        //todo check uri in other events. Delete only if it not use in other events
        if (photosUri != null) {
            for (int i = 1; i <= photosUri.size(); i++) {
                File file = new File(photosUri.get(photosUri.size() - i));
                boolean isDeleted = file.delete();
                if (isDeleted) {
                    Log.d(TAG, "file " + photosUri.get(photosUri.size() - i) + " deleted");
                    MediaScannerConnection.scanFile(context, new String[]{photosUri.get(photosUri.size() - i)}, null, null);
                } else {
                    Log.d(TAG, "file " + photosUri.get(photosUri.size() - i) + " doesn't deleted");
                }
            }
        }
        if (photosUri != null) {
            photosUri.clear();
        }

        setVisibilities(context);

        MainActivity mainActivity = (MainActivity) context;

        setDefaultValues(event, photosUri, mainActivity);

        if (photosListForDeleting != null) photosListForDeleting.clear();
    }

    private void setDefaultValues(Event event, List<String> photosUri, MainActivity mainActivity) {
        setDate(new Date());
        setGuarantee(12);
        setNotes("");
        setPhotosUri(photosUri);

        event.setDate(getDate());
        event.setGuarantee(getGuarantee());
        event.setNotes(getNotes());

        mainActivity.binding.getNewEventFragment().setTextActionACTV();
        mainActivity.binding.getNewEventFragment().binding.guaranteeSlider.setValue(event.getGuarantee());
        mainActivity.binding.getNewEventFragment().eventPhotosListAdapter.notifyDataSetChanged();

        if (this.photosUri != null) this.photosUri.clear();
    }

    public void onClickSaveButton(Event event, Context context) {

        int next_id = 0;
        int current_id = 0;

        //todo как работает для разных пользователей? ВРОДЕ НЕ ДОЛЖНО
        MainActivity mainActivity = (MainActivity) context;

        ToothModel toothModel = getToothModel(mainActivity);

        if (toothModel.getEventModels().size() > 0)
            current_id = toothModel.getEventModels().where().max("id").intValue();

        if (current_id == 0) {
            next_id = 1;
        } else {
            next_id = current_id + 1;
        }

        realm.beginTransaction();
        EventModel eventModel = realm.createEmbeddedObject(EventModel.class, toothModel, "eventModels");
        eventModel.setId(next_id);
        eventModel.setPosition(toothModel.getPosition());
        eventModel.setDate(event.getDate());
        eventModel.setAction(event.getAction());
        eventModel.setGuarantee(event.getGuarantee());
        eventModel.setNotes(event.getNotes());

        RealmList<String> realmList = new RealmList<>();
        realmList.addAll(event.getActions());
        eventModel.setActions(realmList);

        RealmList<String> eventModelRealmList = eventModel.getPhotosUri();

        int amountOfNewPhotos = 0;
        if (event.getPhotosUri() != null) amountOfNewPhotos = event.getPhotosUri().size() - eventModelRealmList.size();

        for (int i = 0; i < amountOfNewPhotos; i++) {
            Log.d(TAG, "eventModelRealmList.add");
            eventModelRealmList.add(event.getPhotosUri().get(eventModelRealmList.size()));
        }

        RealmResults<EventModel> eventModelsResults = toothModel.getEventModels().sort("date", Sort.DESCENDING, "id", Sort.DESCENDING);

        if (eventModelsResults.get(0).getId() == eventModel.getId()) {
            switch (event.getAction()) {
                case "Extracted":
                    toothModel.setExist(false);
                    toothModel.setFilling(false);

                    if (toothModel.isBabyTooth()) {
                        toothModel.setBabyTooth(false);
                        toothModel.setPermanentTooth(true);

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

        realm.commitTransaction();

        Log.d(TAG, "*******************************************************");
        for (EventModel i : eventModelsResults) {
            Log.d(TAG, "date in long: " + i.getDate().getTime());
            Log.d(TAG, i.toString());
        }
        Log.d(TAG, "*******************************************************");

        setVisibilities(context);

        mainActivity.binding.getNewEventFragment().setTextActionACTV();
        mainActivity.binding.getTeethFormulaFragment().setTooth();

        Log.d(TAG, "Tooth condition: " + toothModel.toString());
        for (EventModel i : toothModel.getEventModels()) {
            Log.d(TAG, i.toString());
        }

        if (photosListForDeleting != null) {
            deleteSelectedPhotos(context);
        }

        List<String> photosUri = event.getPhotosUri();
        setDefaultValues(event, photosUri, mainActivity);

    }

    private void setVisibilities(Context context) {
        MainActivity mainActivity = (MainActivity) context;

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            mainActivity.binding.getViewData().setEventFragmentVisibilityData(View.GONE);
            mainActivity.binding.getViewData().setTeethFormulaFragmentVisibilityData(View.VISIBLE);
        } else {
            mainActivity.binding.getViewData().setEventsListFragmentVisibilityData(View.VISIBLE);
        }

        mainActivity.binding.getViewData().setNewEventFragmentVisibilityData(View.GONE);

        mainActivity.binding.getTeethFormulaFragment().refillEventsList();
        mainActivity.binding.getEventsListFragment().refillEventsList();
    }

    public ToothModel getToothModel(MainActivity mainActivity) {
        UserModel userModel = realm.where(UserModel.class).equalTo("id", mainActivity.user_id).findFirst();
        MainActivityViewModel mainActivityViewModel = mainActivity.binding.getModel();
        Log.d(TAG, "getToothModel() user_id: " + mainActivity.user_id + " chosen_tooth: " + mainActivityViewModel.getChosenToothID());
        return userModel.getToothModels().where().equalTo("id", mainActivityViewModel.getChosenToothID()).findFirst();
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

    public List<String> getPhotosUri() {
        return photosUri;
    }

    public void setPhotosUri(List<String> photosUri) {
        this.photosUri = photosUri;
    }

    public String newEventViewModelState(){
        return " id: " + getId()
                + ", position: " + getPosition()
                + ", date: " + getDate()
                + ", action: " + getAction()
                + ", guarantee: " + getGuarantee()
                + ", notes: " + getNotes()
                + ", actions: " + getActions()
                + ", photosUri: " + getPhotosUri();
    }

    public void deleteSelectedPhotos(Context context) {

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
