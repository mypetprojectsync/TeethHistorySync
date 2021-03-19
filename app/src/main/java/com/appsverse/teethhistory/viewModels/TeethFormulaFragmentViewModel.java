package com.appsverse.teethhistory.viewModels;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;

import java.util.List;

import io.realm.Realm;

public class TeethFormulaFragmentViewModel extends ViewModel {
    final String TAG = "myLogs";
    final Realm realm = Realm.getDefaultInstance();

    private int chosenToothID;
    private int layoutVisibility = -1;


    public int getChosenToothID() {
        return chosenToothID;
    }

    public void setChosenToothID(int chosenToothID) {
        this.chosenToothID = chosenToothID;
    }

    public int getLayoutVisibility() {
        return layoutVisibility;
    }

    public void setLayoutVisibility(int layoutVisibility) {
        this.layoutVisibility = layoutVisibility;
    }

    public List<ToothModel> getAllToothModelsForUser(int user_id){
        UserModel userModel = realm.where(UserModel.class).equalTo("id",user_id).findFirst();
        return userModel.getToothModels();
    }

    public  List<EventModel> getEventModelsList(int user_id){


            UserModel userModel = realm.where(UserModel.class).equalTo("id",user_id).findFirst();
            ToothModel toothModel = userModel.getToothModels().where().equalTo("id", chosenToothID).findFirst();
            return toothModel.getEventModels();

    }

    public void deleteEvent(EventModel eventModel){
        realm.beginTransaction();
        eventModel.deleteFromRealm();
        realm.commitTransaction();
        //todo hide editEventFragment if delete event which opened on new event fragment
    }


}
