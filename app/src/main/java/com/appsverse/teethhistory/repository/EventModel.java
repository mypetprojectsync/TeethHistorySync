package com.appsverse.teethhistory.repository;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class EventModel extends RealmObject {

    public static final int CLEANED = 0;
    public static final int FILLED = 1;
    public static final int EXTRACTED = 2;
    public static final int GROWN = 3;
    public static final int IMPLANTED = 4;
    public static final int OTHER = 5;

    private int id;
    private int position;
    private long date;
    private int action;
    private int warranty;
    private String notes;
    private RealmList<String> photosUri;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return new Date(date);
    }

    public void setDate(Date date) {
        this.date = date.getTime();
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public RealmList<String> getPhotosUri() {
        return photosUri;
    }

    public void setPhotosUri(RealmList<String> photosUri) {
        this.photosUri = photosUri;
    }

}
