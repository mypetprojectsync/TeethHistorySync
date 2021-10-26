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
    private int action;
    private int warranty;
    private String notes;

    private List<String> photosUri;

    public Event(int id, int position, Date date, int action, int warranty, String notes, List<String> photosUri) {
        setId(id);
        setPosition(position);
        setDate(date);
        setAction(action);
        setWarranty(warranty);
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
    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
        notifyPropertyChanged(BR.action);
    }

    @Bindable
    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
        notifyPropertyChanged(BR.warranty);
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
