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
import com.appsverse.teethhistory.databinding.FragmentNewEventBinding;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.NewEventViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class NewEventFragment extends Fragment {

    NewEventViewModel model;
    FragmentNewEventBinding binding;
    final String TAG = "myLogs";

    final Calendar myCalendar = Calendar.getInstance();

    Event event;

    ArrayAdapter adapter;
    List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_event, container, false);

        model = new ViewModelProvider(this).get(NewEventViewModel.class);
        binding.setModel(model);

        event = new Event(model.getId(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes(), model.getActions());
        //event = new Event(model.getId(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes());
        binding.setEvent(event);

        setDatePicker(event);

         //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android

        adapter = new ArrayAdapter<>(this.getContext(), R.layout.dropdown_menu_popup_item, list);
        binding.toothActionACTV.setAdapter(adapter);

        //todo list lost when chosen some item and orientation changed. Issue https://github.com/material-components/material-components-android/issues/1464
       // binding.toothActionACTV.setText(event.getAction(), false);
        binding.toothActionACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                event.setAction(list.get(position));
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

    public void setTextActionACTV() {
        Log.d(TAG, "setTextActionACTV()");

        ToothModel toothModel = model.getToothModel((MainActivity) getActivity());
        String[] items;
        if (toothModel != null) {

            if (!toothModel.isExist()) {
                if (toothModel.isBabyTooth()) {
                    items = getResources().getStringArray(R.array.no_grown_tooth_actions);
                } else if (toothModel.isPermanentTooth()) {
                    items = getResources().getStringArray(R.array.no_grown_tooth_actions);
                } else  {
                    items = getResources().getStringArray(R.array.extracted_permanent_tooth_actions);
                }

            } else {
                if (toothModel.isBabyTooth()) {
                    items = getResources().getStringArray(R.array.baby_tooth_actions);
                } else if (toothModel.isPermanentTooth()) {
                    items = getResources().getStringArray(R.array.permanent_tooth_actions);
                } else if (toothModel.isImplant()) {
                    items = getResources().getStringArray(R.array.implanted_tooth_actions);
                } else {
                    items = new String[0];
                }
            }
        } else {
            items = new String[0];
        }

        list.clear();
        Collections.addAll(list, items);

        binding.toothActionACTV.setText(list.get(0), false);
        event.setAction(list.get(0));
        event.setActions(list);
        //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        model.setDate(event.getDate());
        model.setAction(event.getAction());
        model.setGuarantee(event.getGuarantee());
        model.setNotes(event.getNotes());
        model.setActions(event.getActions());

    }
}
