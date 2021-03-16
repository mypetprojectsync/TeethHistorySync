package com.appsverse.teethhistory.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.Event;
import com.appsverse.teethhistory.databinding.FragmentNewEventBinding;
import com.appsverse.teethhistory.databinding.FragmentTeethFormulaBinding;
import com.appsverse.teethhistory.viewModels.NewEventViewModel;
import com.appsverse.teethhistory.viewModels.TeethFormulaFragmentViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.Slider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class NewEventFragment extends Fragment {

    NewEventViewModel model;
    FragmentNewEventBinding binding;
    final String TAG = "myLogs";

    final Calendar myCalendar = Calendar.getInstance();

    Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_event, container, false);

        model = new ViewModelProvider(this).get(NewEventViewModel.class);
        binding.setModel(model);

        event = new Event(model.getDate(),model.getAction(),model.getGuarantee(),model.getNotes());
        binding.setEvent(event);

        setDatePicker(event);

        //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
        String[] items = getResources().getStringArray(R.array.tooth_actions);
        ArrayAdapter adapter = new ArrayAdapter<>(this.getContext(), R.layout.dropdown_menu_popup_item, items);

        binding.toothActionACTV.setAdapter(adapter);

        //todo list lost when chosen some item and orientation changed. Issue https://github.com/material-components/material-components-android/issues/1464
        binding.toothActionACTV.setText(event.getAction(),false);
       // binding.toothActionACTV.setText(items[0],false);
       // Log.d(TAG, "binding.toothActionACTV.setText(items[0],false); items: " + items[2]);
        binding.toothActionACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                event.setAction(items[position]);
            }
        });

        binding.guaranteeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                event.setGuarantee(Math.round(value));
            }
        });

        return binding.getRoot();
    }

    private void setDatePicker(Event event) {

        binding.newEventDateTv.setOnClickListener(v -> {

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        model.setDate(event.getDate());
        model.setAction(event.getAction());
        model.setGuarantee(event.getGuarantee());
        model.setNotes(event.getNotes());

    }
}
