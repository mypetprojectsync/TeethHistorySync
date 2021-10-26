package com.appsverse.teethhistory.viewModels;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    final int MINIMAL_POSITION_IMAGE_ID = 1000;
    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private int position;
    private Date date = new Date();
    private int action;
    private int warranty = 12;
    private String notes;

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

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
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

    public void addListToPhotosListToDeleting(List<String> photosListForDeleting) {
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

        if (photosUri != null) {

            for (String uri : photosUri) {
                if (checkUriInOtherEvents(uri)) {
                    File file = new File(uri);
                    file.delete();
                }
            }

            photosUri.clear();
        }

        setVisibilities(context);

        setDefaultValues(event, (MainActivity) context);

        if (photosListForDeleting != null) photosListForDeleting.clear();
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
                        if (coincidenceCounter > 1) return false;
                    }
                }
            }
        }
        return true;
    }

    private void setDefaultValues(Event event, MainActivity mainActivity) {
        setDate(new Date());
        setWarranty(12);
        setNotes("");

        event.setDate(getDate());
        event.setWarranty(getWarranty());
        event.setNotes(getNotes());

        mainActivity.binding.getNewEventFragment().setDefaultTextActionACTV();

        mainActivity.binding.getNewEventFragment().binding.warrantySlider.setValue(event.getWarranty());

        if (event.getPhotosUri() != null) event.getPhotosUri().clear();

        mainActivity.binding.getNewEventFragment().eventPhotosListAdapter.notifyDataSetChanged();
    }

    public void onClickSaveButton(Event event, Context context) {

        int next_id;
        int current_id = 0;

        MainActivity mainActivity = (MainActivity) context;

        ToothModel toothModel = getToothModel(mainActivity);

        if (toothModel.getEventModels().size() > 0)
            current_id = toothModel.getEventModels().where().max("id").intValue();

        if (current_id == 0) {
            next_id = 100 + toothModel.getId();
        } else {
            next_id = current_id + 100;
        }

        realm.beginTransaction();
        EventModel eventModel = realm.createEmbeddedObject(EventModel.class, toothModel, "eventModels");
        eventModel.setId(next_id);
        eventModel.setPosition(toothModel.getPosition());
        eventModel.setDate(event.getDate());
        eventModel.setAction(event.getAction());
        eventModel.setWarranty(event.getWarranty());
        eventModel.setNotes(event.getNotes());

        RealmList<String> eventModelRealmList = eventModel.getPhotosUri();

        int amountOfNewPhotos = 0;
        if (event.getPhotosUri() != null)
            amountOfNewPhotos = event.getPhotosUri().size() - eventModelRealmList.size();

        for (int i = 0; i < amountOfNewPhotos; i++) {
            eventModelRealmList.add(event.getPhotosUri().get(eventModelRealmList.size()));
        }

        RealmResults<EventModel> eventModelsResults = toothModel.getEventModels().sort("date", Sort.DESCENDING, "id", Sort.DESCENDING);

        if (eventModelsResults.get(0).getId() == eventModel.getId())
            setToothModelState(event, context, toothModel);

        realm.commitTransaction();

        setVisibilities(context);

        mainActivity.binding.getTeethFormulaFragment().setTooth();

        if (photosListForDeleting != null) {
            deleteSelectedPhotos();
        }

        setDefaultValues(event, mainActivity);

        hideKeyboard(mainActivity);
    }

    private void hideKeyboard(MainActivity mainActivity) {
        InputMethodManager inputManager = (InputMethodManager)
                mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(mainActivity.binding.getRoot().getWindowToken(), 0);
        }
    }

    private void setToothModelState(Event event, Context context, ToothModel toothModel) {
        if (event.getAction() == EventModel.EXTRACTED) {

            if (toothModel.getPosition() > 50) {

                toothModel.setPosition(toothModel.getPosition() - 40);
                toothModel.setState(ToothModel.NO_TOOTH);

                ((MainActivity) context).binding.getTeethFormulaFragment().setPositionIVById(toothModel.getId(), toothModel.getPosition());

            } else {
                toothModel.setState(ToothModel.NO_TOOTH);
            }

        } else if (event.getAction() == EventModel.FILLED) {

            toothModel.setState(ToothModel.FILLED);

        } else if (event.getAction() == EventModel.IMPLANTED) {

            toothModel.setState(ToothModel.IMPLANTED);

        } else if (event.getAction() == EventModel.GROWN
                || event.getAction() == EventModel.CLEANED
                || event.getAction() == EventModel.OTHER) {

            toothModel.setState(ToothModel.NORMAL);

        }
    }

    private void setVisibilities(Context context) {
        MainActivity mainActivity = (MainActivity) context;

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            mainActivity.binding.getModel().setEventFragmentVisibilityData(View.GONE);
            mainActivity.binding.getModel().setTeethFormulaFragmentVisibility(View.VISIBLE);
        } else {
            mainActivity.binding.getModel().setEventsListFragmentVisibilityData(View.VISIBLE);
        }

        mainActivity.binding.getModel().setNewEventFragmentVisibility(View.GONE);

        mainActivity.binding.getTeethFormulaFragment().refillEventsList();
        mainActivity.binding.getEventsListFragment().refillEventsList();
    }

    public ToothModel getToothModel(MainActivity mainActivity) {
        UserModel userModel = realm.where(UserModel.class).equalTo("id", mainActivity.user_id).findFirst();

        return userModel.getToothModels().where().equalTo("id", mainActivity.binding.getModel().getChosenToothID()).findFirst();
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

    public void deleteSelectedPhotos() {

        for (String uri : photosListForDeleting) {
            if (checkUriInOtherEvents(uri)) {
                File file = new File(uri);
                file.delete();
            }
        }
        photosListForDeleting.clear();
    }
}