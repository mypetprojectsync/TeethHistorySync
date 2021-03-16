package com.appsverse.teethhistory.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.appsverse.teethhistory.BR;

public class MainActivityViewData extends BaseObservable {

    private int teethFormulaFragmentVisibilityData;
    private int newEventFragmentVisibilityData;
    private int editEventFragmentVisibilityData;
    private int eventFragmentVisibilityData;

    public MainActivityViewData(int teethFormulaFragmentVisibilityData, int newEventFragmentVisibilityData, int editEventFragmentVisibilityData, int eventFragmentVisibilityData){
        setTeethFormulaFragmentVisibilityData(teethFormulaFragmentVisibilityData);
        setNewEventFragmentVisibilityData(newEventFragmentVisibilityData);
        setEditEventFragmentVisibilityData(editEventFragmentVisibilityData);
        setEventFragmentVisibilityData(eventFragmentVisibilityData);
    }

    @Bindable
    public int getTeethFormulaFragmentVisibilityData() {
        return teethFormulaFragmentVisibilityData;
    }

    public void setTeethFormulaFragmentVisibilityData(int teethFormulaFragmentVisibilityData) {
        this.teethFormulaFragmentVisibilityData = teethFormulaFragmentVisibilityData;
        notifyPropertyChanged(BR.teethFormulaFragmentVisibilityData);
    }

    @Bindable
    public int getNewEventFragmentVisibilityData() {
        return newEventFragmentVisibilityData;
    }

    public void setNewEventFragmentVisibilityData(int newEventFragmentVisibilityData) {
        this.newEventFragmentVisibilityData = newEventFragmentVisibilityData;
        notifyPropertyChanged(BR.newEventFragmentVisibilityData);
    }

    @Bindable
    public int getEditEventFragmentVisibilityData() {
        return editEventFragmentVisibilityData;
    }

    public void setEditEventFragmentVisibilityData(int editEventFragmentVisibilityData) {
        this.editEventFragmentVisibilityData = editEventFragmentVisibilityData;
        notifyPropertyChanged(BR.editEventFragmentVisibilityData);
    }

    @Bindable
    public int getEventFragmentVisibilityData() {
        return eventFragmentVisibilityData;
    }

    public void setEventFragmentVisibilityData(int eventFragmentVisibilityData) {
        this.eventFragmentVisibilityData = eventFragmentVisibilityData;
        notifyPropertyChanged(BR.eventFragmentVisibilityData);
    }
}
