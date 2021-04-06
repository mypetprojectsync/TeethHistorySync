package com.appsverse.teethhistory.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.appsverse.teethhistory.EventsListAdapter;
import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.Tooth;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.databinding.FragmentTeethFormulaBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.TeethFormulaFragmentViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class TeethFormulaFragment extends Fragment {

    TeethFormulaFragmentViewModel model;
    public FragmentTeethFormulaBinding binding;
    final String TAG = "myLogs";

    MainActivity mainActivity;
    ActivityMainBinding activityMainBinding;

    RecyclerView recyclerView;
    EventsListAdapter adapter;

    int user_id;
    List<EventModel> eventModels = new ArrayList<>();

    Tooth tooth;

    int orientation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainActivity = (MainActivity) this.getActivity();
        activityMainBinding = mainActivity.getBinding();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        user_id = sharedPreferences.getInt("chosen_user_id", -1);

        orientation = getResources().getConfiguration().orientation;

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_teeth_formula, container, false);

        model = new ViewModelProvider(this).get(TeethFormulaFragmentViewModel.class);
        binding.setModel(model);

        activityMainBinding.getModel().setChosenToothID(model.getChosenToothID());

        if (user_id >= 0) {

            createEventsList();

            List<ToothModel> toothModels = model.getAllToothModelsForUser(user_id);

            int chosenToothID = model.getChosenToothID();

            tooth = new Tooth(model.getChosenToothID(), model.getChosenToothPosition());
            binding.setTooth(tooth);

            //todo hide teeth layout when recyclerview scroll down and show whe scroll up

            int counter = 0;
            for (int i = 0; i < 16; i++) {
                TextView toothPositionTV = new TextView(this.getContext());
                toothPositionTV.setText(String.valueOf(toothModels.get(i).getPosition()));
                toothPositionTV.setId(toothModels.get(i).getId());

                if (toothModels.get(i).getId() == chosenToothID) toothPositionTV.setTextSize(30.0f);

                LinearLayout linearLayout = new LinearLayout(this.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);

                linearLayout.setLayoutParams(params);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                toothPositionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toothClicked((TextView) v, tooth);
                    }
                });

                ImageView toothIV = new ImageView(this.getContext());
                toothIV.setScaleType(ImageView.ScaleType.FIT_START);

                if (counter == 0) {
                    toothIV.setImageResource(R.drawable.ic_11g);
                    counter++;
                } else if (counter == 1) {
                    toothIV.setImageResource(R.drawable.ic_12g);
                    counter++;
                } else{
                    toothIV.setImageResource(R.drawable.ic_13g);
                    counter = 0;
                }

                toothIV.setAdjustViewBounds(true);
                toothIV.setTag(i);



                ImageView toothPositionIV = new ImageView(this.getContext());
                toothPositionIV.setScaleType(ImageView.ScaleType.FIT_START);

                String toothNumber = "ic_"+toothModels.get(i).getPosition();
                int id = getResources().getIdentifier(toothNumber, "drawable", getActivity().getPackageName());
                toothPositionIV.setImageResource(id);
                //toothPositionIV.setBackgroundColor(ContextCompat.getColor(this.getContext(),R.color.purple_700));
                toothPositionIV.setAdjustViewBounds(true);
                //toothPositionIV.setImageResource(R.drawable.ic_11);

                toothIV.setAdjustViewBounds(true);

                linearLayout.addView(toothIV);
                linearLayout.addView(toothPositionIV);

                toothIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "tooth clicked position: " + toothModels.get((int) v.getTag()).getPosition());
                            ((ImageView) v).setImageResource(R.drawable.ic_11g_selected);
                    }
                });

                binding.llTeethFirstRow.addView(linearLayout);
            }

            for (int i = 16; i < 32; i++) {
                TextView toothPositionTV = new TextView(this.getContext());
                toothPositionTV.setText(String.valueOf(toothModels.get(i).getPosition()));
                toothPositionTV.setId(toothModels.get(i).getId());

                if (toothModels.get(i).getId() == chosenToothID) toothPositionTV.setTextSize(30.0f);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                toothPositionTV.setLayoutParams(params);

                toothPositionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toothClicked((TextView) v, tooth);
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
            activityMainBinding.getViewData().setEditEventFragmentVisibilityData(View.GONE);

            activityMainBinding.getNewEventFragment().event.setDate(new Date());
            activityMainBinding.getNewEventFragment().event.setPosition(tooth.getPosition());
        });

        if (model.getChosenToothID() > 0) {
            refillEventsList();
        }

        return binding.getRoot();
    }

    private void toothClicked(TextView textView, Tooth tooth) {

        if (tooth.getId() > 0)
            ((TextView) binding.getRoot().findViewById(tooth.getId())).setTextSize(14.0f);

        tooth.setId(textView.getId());
        tooth.setPosition(parseInt(textView.getText().toString()));

        textView.setTextSize(30.0f);

        activityMainBinding.getModel().setChosenToothID(tooth.getId());


        if (mainActivity.binding.getViewData().getEditEventFragmentVisibilityData() == View.VISIBLE) {
            mainActivity.binding.getViewData().setEditEventFragmentVisibilityData(View.GONE);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                mainActivity.binding.getViewData().setEventsListFragmentVisibilityData(View.VISIBLE);
        }

        refillEventsList();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            mainActivity.binding.getEventsListFragment().refillEventsList();

        mainActivity.binding.getNewEventFragment().setTextActionACTV();
    }

    private void createEventsList() {

        recyclerView = binding.eventsList;
        //todo implement binding to recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation());


        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new EventsListAdapter(this.getContext(), eventModels);
        adapter.setClickListener(new EventsListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d(TAG, "rv clicked position: " + position);

                if (view.getId() == R.id.itemEventOptions) {

                    //todo try to safe in viewmodel when destroy
                    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    popupMenu.inflate(R.menu.event_item_options_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.popupEventItemEdit) {

                                mainActivity.binding.getEditEventFragment().setEvent(eventModels.get(position));

                                setVisibilities();
                                Log.d(TAG, "option edit clicked");
                            } else {

                                //todo save dialog when orientation changed
                                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                                dialogBuilder.setTitle("Delete event?");
                                dialogBuilder.setPositiveButton("ok", (dialog, which) -> {
                                    //model.deleteEvent(eventModels.get(position), mainActivity);
                                    mainActivity.deleteEvent(eventModels.get(position));
                                    deleteEventAnimation(position);
                                });
                                dialogBuilder.setNegativeButton("cancel", (dialog, which) -> {
                                });
                                dialogBuilder.show();
                                Log.d(TAG, "option delete clicked");
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                } else {
                    mainActivity.binding.getEditEventFragment().setEvent(eventModels.get(position));

                    setVisibilities();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (binding.floatingActionButton.isShown())
                        binding.floatingActionButton.hide();
                } else {
                    if (!binding.floatingActionButton.isShown())
                        binding.floatingActionButton.show();
                }

            }
        });
    }


    private void setVisibilities() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            activityMainBinding.getViewData().setTeethFormulaFragmentVisibilityData(View.GONE);
        }

        mainActivity.binding.getViewData().setEditEventFragmentVisibilityData(View.VISIBLE);
        mainActivity.binding.getViewData().setEventFragmentVisibilityData(View.VISIBLE);
        mainActivity.binding.getViewData().setNewEventFragmentVisibilityData(View.GONE);
    }


    public void refillEventsList() {
        eventModels.clear();

        if (mainActivity.binding.getViewData().getEventsListFragmentVisibilityData() == View.GONE
                || orientation == Configuration.ORIENTATION_PORTRAIT) {

            //eventModels.addAll(model.getEventModelsList(user_id, tooth));
            eventModels.addAll(mainActivity.getSortedEventsList());

            if (!binding.floatingActionButton.isShown()) binding.floatingActionButton.show();
            Log.d(TAG, "refillEventsList() | if (mainActivity.binding.getViewData().getEventsListFragmentVisibilityData() == View.GONE");
        } else {
            if (binding.floatingActionButton.isShown()) binding.floatingActionButton.hide();
            Log.d(TAG, "refillEventsList() | else");
        }
        adapter.notifyDataSetChanged();
    }

    public void deleteEventAnimation(int position) {
        eventModels.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        model.setChosenToothID(tooth.getId());
        model.setChosenToothPosition(tooth.getPosition());
    }
}
