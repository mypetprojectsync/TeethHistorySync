package com.appsverse.teethhistory.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.Event;
import com.appsverse.teethhistory.databinding.FragmentEditEventBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.EditEventViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditEventFragment extends Fragment {

    EditEventViewModel model;
    FragmentEditEventBinding binding;
    final String TAG = "myLogs";

    Event event;
    ArrayAdapter adapter;
    List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_event, container, false);

        model = new ViewModelProvider(this).get(EditEventViewModel.class);
        binding.setModel(model);

       event = new Event(model.getId(), model.getPosition(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes(), model.getActions());
        //event = new Event(model.getId(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes());
        binding.setEvent(event);

        setDatePicker(event);


        //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
        /*String[] items;
        ToothModel toothModel = model.getToothModel((MainActivity) getActivity());
        if (toothModel != null) {
        if (!toothModel.isExist()) {
            if (toothModel.isBabyTooth()) {
                items = getResources().getStringArray(R.array.no_grown_tooth_actions);
            } else if (toothModel.isPermanentTooth()) {
                items = getResources().getStringArray(R.array.no_grown_tooth_actions);
            } else  if (toothModel.isImplant()) {
                items = getResources().getStringArray(R.array.extracted_permanent_tooth_actions);
            }else {
                items = new String[0];
            }
        } else {
            if (toothModel.isBabyTooth()) {
                items = getResources().getStringArray(R.array.baby_tooth_actions);
            } else if (toothModel.isPermanentTooth()) {
                items = getResources().getStringArray(R.array.permanent_tooth_actions);
            } else  if (toothModel.isImplant()) {
                items = getResources().getStringArray(R.array.implanted_tooth_actions);
            } else {
                items = new String[0];
            }
        }
        } else {
            items = new String[0];
        }*/
        adapter = new ArrayAdapter<>(this.getContext(), R.layout.dropdown_menu_popup_item, list);

        binding.editToothActionACTV.setAdapter(adapter);

        //todo list lost when chosen some item and orientation changed. Issue https://github.com/material-components/material-components-android/issues/1464
       // binding.editToothActionACTV.setText(event.getAction(),false);
        binding.editToothActionACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                event.setAction(list.get(position));
            }
        });

        binding.editGuaranteeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                event.setGuarantee(Math.round(value));
            }
        });
        setTextActionACTV();
        return binding.getRoot();
    }

    private void setDatePicker(Event event) {

        binding.editEventDateTv.setOnClickListener(v -> {

            MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
            builder.setSelection(event.getDate().getTime());
            MaterialDatePicker picker = builder.build();

            picker.show(this.getActivity().getSupportFragmentManager(), picker.toString());
            picker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selection);
                event.setDate(new Date((Long) selection));
                Log.d(TAG, date);
            });
        });
    }

    public void setTextActionACTV() {
        Log.d(TAG, "setTextActionACTV()");

        ToothModel toothModel = model.getToothModel((MainActivity) getActivity());
        if (toothModel != null) {
            list.clear();
            if (event.getActions() != null) {
                list.addAll(event.getActions());

                binding.editToothActionACTV.setText(event.getAction(), false);
                event.setAction(list.get(0));
                //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setEvent(EventModel event){
        this.event.setId(event.getId());
        this.event.setDate(event.getDate());
        this.event.setAction(event.getAction());
        this.event.setGuarantee(event.getGuarantee());
        this.event.setNotes(event.getNotes());
        this.event.setActions(event.getActions());

        binding.editToothActionACTV.setText(event.getAction(),false);
        setTextActionACTV();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        model.setId(event.getId());
        model.setPosition(event.getPosition());
        model.setDate(event.getDate());
        model.setAction(event.getAction());
        model.setGuarantee(event.getGuarantee());
        model.setNotes(event.getNotes());
        model.setActions(event.getActions());
    }
}
