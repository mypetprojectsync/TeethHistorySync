package com.appsverse.teethhistory.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Event extends BaseObservable {

    private int id;
    private int position;
    private Date date;
    private String action;
    private int guarantee;
    private String notes;

    private List<String> photosUri;

    public Event(int id, int position, Date date, String action, int guarantee, String notes, List<String> photosUri){
        setId(id);
        setPosition(position);
        setDate(date);
        setAction(action);
        setGuarantee(guarantee);
        setNotes(notes);
        setPhotosUri(photosUri);

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

       if (date != null) date = roundToDay(date);

        this.date = date;

        notifyPropertyChanged(BR.date);
    }

    private Date roundToDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
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
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public List<String> getPhotosUri() {
        return photosUri;
    }

    public void setPhotosUri(List<String> photosUri) {
        this.photosUri = photosUri;
        notifyPropertyChanged(BR.photosUri);
    }
}
