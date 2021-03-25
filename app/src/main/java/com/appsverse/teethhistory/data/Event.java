package com.appsverse.teethhistory.data;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

import java.util.Date;
import java.util.List;

public class Event extends BaseObservable {

    final String TAG = "myLogs";

    private int id;
    private Date date;
    private String action;
    private int guarantee;
    private String notes;

    private List<String> actions;

    //public Event(int id, Date date, String action, int guarantee, String notes){
    public Event(int id, Date date, String action, int guarantee, String notes, List<String> actions){
        setId(id);
        setDate(date);
        setAction(action);
        setGuarantee(guarantee);
        setNotes(notes);
        setActions(actions);
    }
    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        notifyPropertyChanged(BR.date);
    }

    @Bindable
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
        notifyPropertyChanged(BR.action);
    }

    @Bindable
    public int getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(int guarantee) {
        this.guarantee = guarantee;
        notifyPropertyChanged(BR.guarantee);
        Log.d(TAG, "setGuarantee: " + guarantee);
    }

    @Bindable
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        notifyPropertyChanged(BR.notes);
    }

   @Bindable
    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
        notifyPropertyChanged(BR.actions);
    }
}
