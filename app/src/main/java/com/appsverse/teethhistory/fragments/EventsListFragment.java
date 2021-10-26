package com.appsverse.teethhistory.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.adapters.EventsListAdapter;
import com.appsverse.teethhistory.data.Tooth;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.databinding.FragmentEventsListBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsListFragment extends Fragment {

    FragmentEventsListBinding binding;

    MainActivity mainActivity;
    ActivityMainBinding activityMainBinding;

    RecyclerView recyclerView;
    EventsListAdapter adapter;

    int userID;

    List<EventModel> eventModels = new ArrayList<>();

    int orientation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainActivity = (MainActivity) this.getActivity();
        activityMainBinding = mainActivity.getBinding();

        userID = mainActivity.user_id;

        orientation = getResources().getConfiguration().orientation;

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events_list, container, false);

        Tooth tooth = activityMainBinding.getTeethFormulaFragment().binding.getTooth();
        binding.setTooth(tooth);

        binding.floatingActionButton.setOnClickListener(v -> {

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activityMainBinding.getModel().setTeethFormulaFragmentVisibility(View.GONE);
            }

            activityMainBinding.getModel().setEventFragmentVisibilityData(View.VISIBLE);
            activityMainBinding.getModel().setNewEventFragmentVisibility(View.VISIBLE);
            activityMainBinding.getModel().setEditEventFragmentVisibilityData(View.GONE);
            activityMainBinding.getModel().setEventsListFragmentVisibilityData(View.GONE);

            activityMainBinding.getTeethFormulaFragment().refillEventsList();
            activityMainBinding.getNewEventFragment().event.setDate(new Date());
        });


        if (userID >= 0) createEventsList();

        refillEventsList();

        return binding.getRoot();
    }

    private void createEventsList() {

        recyclerView = binding.eventsList;

        adapter = new EventsListAdapter(this.getContext(), eventModels);
        adapter.setClickListener((view, position) -> {

            if (view.getId() == R.id.itemEventOptions) {

                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.event_item_options_menu);

                popupMenu.setOnMenuItemClickListener(item -> {

                    if (item.getItemId() == R.id.popupEventItemEdit) {

                        mainActivity.binding.getEditEventFragment().setEvent(eventModels.get(position));

                        setVisibilities();
                    } else {

                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                        dialogBuilder.setTitle(R.string.delete_event);
                        dialogBuilder.setPositiveButton(R.string.ok, (dialog, which) -> {

                            mainActivity.deleteEvent(eventModels.get(position));

                            deleteEventAnimation(position);
                        });
                        dialogBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                        });
                        dialogBuilder.show();

                    }
                    return false;
                });
                popupMenu.show();
            } else {

                if (activityMainBinding.getModel().getChosenToothID() == 0) {
                    activityMainBinding.getModel().setChosenToothID(eventModels.get(position).getId()%100);
                    activityMainBinding.getTeethFormulaFragment().setTooth();
                }

                setSelection(position);

                mainActivity.binding.getEditEventFragment().setEvent(eventModels.get(position));

                setVisibilities();
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (binding.floatingActionButton.isShown()) binding.floatingActionButton.hide();
                } else {
                    if (!binding.floatingActionButton.isShown())
                        binding.floatingActionButton.show();
                }
            }
        });
    }

    public void setSelection(int position) {

        EventsListAdapter.selectedPos = position;
        recyclerView.smoothScrollToPosition(position);
    }

    private void setVisibilities() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            activityMainBinding.getModel().setTeethFormulaFragmentVisibility(View.GONE);
        }

        mainActivity.binding.getModel().setEditEventFragmentVisibilityData(View.VISIBLE);
        mainActivity.binding.getModel().setNewEventFragmentVisibility(View.GONE);
        mainActivity.binding.getModel().setEventsListFragmentVisibilityData(View.GONE);

        mainActivity.binding.getTeethFormulaFragment().refillEventsList();
    }


    public void refillEventsList() {
        if (mainActivity.binding.getModel().getEventsListFragmentVisibilityData() == View.VISIBLE) {

            eventModels.clear();

            if (activityMainBinding.getModel().getChosenToothID() > 0) {
                eventModels.addAll(mainActivity.getSortedEventsList());
            } else {
                eventModels.addAll(mainActivity.getSortedEventsListForAllTeeth());
            }

            adapter.notifyDataSetChanged();

            recyclerView.scrollToPosition(0);

            EventsListAdapter.selectedPos = RecyclerView.NO_POSITION;
        }
    }

    public void deleteEventAnimation(int position) {
        eventModels.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
