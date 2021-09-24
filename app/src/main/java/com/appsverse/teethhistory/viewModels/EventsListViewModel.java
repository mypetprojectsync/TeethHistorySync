package com.appsverse.teethhistory.viewModels;

import androidx.lifecycle.ViewModel;

import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import java.util.List;

import io.realm.Realm;

public class EventsListViewModel extends ViewModel {
    final Realm realm = Realm.getDefaultInstance();

    public List<EventModel> getEventModelsList(int userId, int chosenToothID){

        UserModel userModel = realm.where(UserModel.class).equalTo("id",userId).findFirst();
        ToothModel toothModel = userModel.getToothModels().where().equalTo("id", chosenToothID).findFirst();
        return toothModel.getEventModels();
    }

    public void deleteEvent(EventModel eventModel){
        realm.beginTransaction();
        eventModel.deleteFromRealm();
        realm.commitTransaction();
    }
}
