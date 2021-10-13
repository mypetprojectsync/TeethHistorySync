package com.appsverse.teethhistory.viewModels;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

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

    final Realm realm = Realm.getDefaultInstance();

    private int id;
    private int position;
    private Date date;
    private int action;
    private int warranty;
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

    public void clearPhotosListToDeleting() {
        if (photosListForDeleting != null) photosListForDeleting.clear();
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void onClickCancelButton(Event event, Context context) {

        ToothModel toothModel = getToothModel((MainActivity) context);
        EventModel eventModel = toothModel.getEventModels().where().equalTo("id", event.getId()).findFirst();

        RealmList<String> eventModelRealmList = eventModel.getPhotosUri();
        List<String> photosUri = event.getPhotosUri();

        int amountOfNewPhotos = photosUri.size() - eventModelRealmList.size();

        for (int i = 1; i <= amountOfNewPhotos; i++) {

            String uri = photosUri.get(photosUri.size() - i);

            if (checkUriInOtherEvents(uri)) {
                File file = new File(uri);
                file.delete();
            }
        }
        if (photosListForDeleting != null) photosListForDeleting.clear();

        setVisibilities(context);

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

    public void onClickSaveButton(Event event, Context context) {
        MainActivity mainActivity = (MainActivity) context;

        ToothModel toothModel = getToothModel(mainActivity);

        EventModel eventModel = toothModel.getEventModels().where().equalTo("id", event.getId()).findFirst();

        int oldEventModelAction = eventModel.getAction();
        int newEventModelAction = event.getAction();

        List<String> clearList = new ArrayList<>(event.getPhotosUri());

        if (photosListForDeleting != null) clearList.removeAll(photosListForDeleting);
        event.setPhotosUri(clearList);

        realm.beginTransaction();

        eventModel.setDate(event.getDate());
        eventModel.setAction(event.getAction());
        eventModel.setWarranty(event.getWarranty());
        eventModel.setNotes(event.getNotes());

        RealmList<String> eventModelRealmList = eventModel.getPhotosUri();

        eventModelRealmList.deleteAllFromRealm();
        eventModelRealmList.addAll(event.getPhotosUri());

        RealmResults<EventModel> eventModelsResults = toothModel.getEventModels().sort("date", Sort.DESCENDING, "id", Sort.DESCENDING);

        if (eventModelsResults.get(0).getId() == eventModel.getId()
                && oldEventModelAction != newEventModelAction) {

            if (newEventModelAction == EventModel.EXTRACTED) {

                if (toothModel.getPosition() > 50)
                    toothModel.setPosition(toothModel.getPosition() - 40);

                toothModel.setState(ToothModel.NO_TOOTH);

            } else if (newEventModelAction == EventModel.IMPLANTED) {

                toothModel.setState(ToothModel.IMPLANTED);

            } else if (newEventModelAction == EventModel.GROWN
                    || newEventModelAction == EventModel.CLEANED
                    || newEventModelAction == EventModel.OTHER) {

                toothModel.setState(ToothModel.NORMAL);
            }
        }

        realm.commitTransaction();

        setVisibilities(context);

        mainActivity.binding.getTeethFormulaFragment().setTooth();


        if (photosListForDeleting != null) {
            deleteSelectedPhotos();
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

        mainActivity.binding.getModel().setEditEventFragmentVisibilityData(View.GONE);

        mainActivity.binding.getTeethFormulaFragment().binding.getModel().setEventsListSelectedPosition(RecyclerView.NO_POSITION);

        mainActivity.binding.getTeethFormulaFragment().refillEventsList();
        mainActivity.binding.getEventsListFragment().refillEventsList();
    }

    public ToothModel getToothModel(MainActivity mainActivity) {
        UserModel userModel = realm.where(UserModel.class).equalTo("id", mainActivity.user_id).findFirst();
        MainActivityViewModel mainActivityViewModel = mainActivity.binding.getModel();
        return userModel.getToothModels().where().equalTo("id", mainActivityViewModel.getChosenToothID()).findFirst();
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
