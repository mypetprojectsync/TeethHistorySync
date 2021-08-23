package com.appsverse.teethhistory.repository;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class EventModel extends RealmObject {

    private int id;
    private int position;
    private long date;
    private String action;
    private int guarantee;
    private String notes;
    private RealmList<String> photosUri;

private RealmList<String> actions;

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

    public RealmList<String> getActions() {
        return actions;
    }

    public void setActions(RealmList<String> actions) {
        this.actions = actions;
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
