package com.appsverse.teethhistory.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.EventsListAdapter;
import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.Tooth;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.databinding.FragmentTeethFormulaBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.TeethFormulaFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class TeethFormulaFragment extends Fragment {

    TeethFormulaFragmentViewModel model;
    FragmentTeethFormulaBinding binding;
    final String TAG = "myLogs";

    MainActivity mainActivity;
    ActivityMainBinding activityMainBinding;

    RecyclerView recyclerView;
    EventsListAdapter adapter;

    int user_id;
    List<EventModel> eventModels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainActivity = (MainActivity) this.getActivity();
        activityMainBinding = mainActivity.getBinding();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        user_id = sharedPreferences.getInt("chosen_user_id", -1);

        int orientation = getResources().getConfiguration().orientation;

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_teeth_formula, container, false);

        model = new ViewModelProvider(this).get(TeethFormulaFragmentViewModel.class);
        binding.setModel(model);

        Tooth tooth = new Tooth(model.getChosenToothID());
        binding.setTooth(tooth);

        activityMainBinding.getModel().setChosenToothID(model.getChosenToothID());

        if (user_id >= 0) {

            createEventsList();

            List<ToothModel> toothModels = model.getAllToothModelsForUser(user_id);

            int chosenToothID = model.getChosenToothID();
            if (chosenToothID > 0) {refillEventsList();}

            for (int i = 0; i < 16; i++) {
                TextView toothPositionTV = new TextView(this.getContext());
                toothPositionTV.setText(String.valueOf(toothModels.get(i).getId()));

                if (toothModels.get(i).getId() == chosenToothID) toothPositionTV.setTextSize(30.0f);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10,10,10,10);
                toothPositionTV.setLayoutParams(params);

                toothPositionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textView = (TextView) v;
                        model.setChosenToothID(parseInt(textView.getText().toString()));
                        tooth.setId(model.getChosenToothID());
                        Log.d(TAG, "tv clicked, id: " + model.getChosenToothID());
                        ((TextView) v).setTextSize(30.0f);

                        activityMainBinding.getModel().setChosenToothID(model.getChosenToothID());

                        refillEventsList();
                    }
                });

                binding.llTeethFirstRow.addView(toothPositionTV);
            }

            for (int i = 16; i < 32; i++) {
                TextView toothPositionTV = new TextView(this.getContext());
                toothPositionTV.setText(String.valueOf(toothModels.get(i).getId()));

                if (toothModels.get(i).getId() == chosenToothID) toothPositionTV.setTextSize(30.0f);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10,10,10,10);
                toothPositionTV.setLayoutParams(params);

                toothPositionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textView = (TextView) v;
                        model.setChosenToothID(parseInt(textView.getText().toString()));
                        tooth.setId(model.getChosenToothID());
                        Log.d(TAG, "tv clicked, id: " + model.getChosenToothID());
                        ((TextView) v).setTextSize(30.0f);

                        activityMainBinding.getModel().setChosenToothID(model.getChosenToothID());

                        refillEventsList();
                    }
                });


                binding.llTeethSecondRow.addView(toothPositionTV);
            }

        }

        binding.floatingActionButton.setOnClickListener(v -> {
            Log.d(TAG, "floating button was clicked");

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activityMainBinding.getViewData().setTeethFormulaFragmentVisibilityData(View.GONE);
            }

            activityMainBinding.getViewData().setEventFragmentVisibilityData(View.VISIBLE);
            activityMainBinding.getViewData().setNewEventFragmentVisibilityData(View.VISIBLE);
        });

        return binding.getRoot();
    }

    private void createEventsList(){

        recyclerView = binding.eventsList;
        //todo implement binding to recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new EventsListAdapter(this.getContext(), eventModels);
        recyclerView.setAdapter(adapter);
    }

    public void refillEventsList() {
        eventModels.clear();
        eventModels.addAll(model.getEventModelsList(user_id));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
