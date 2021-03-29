package com.appsverse.teethhistory.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
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
import com.appsverse.teethhistory.databinding.FragmentEventsListBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.viewModels.EventsListViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class EventsListFragment extends Fragment {
    EventsListViewModel model;
    FragmentEventsListBinding binding;
    final String TAG = "myLogs";

    MainActivity mainActivity;
    ActivityMainBinding activityMainBinding;

    RecyclerView recyclerView;
    EventsListAdapter adapter;

    int userID;
    public int chosenToothId;

    List<EventModel> eventModels = new ArrayList<>();

    int orientation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainActivity = (MainActivity) this.getActivity();
        activityMainBinding = mainActivity.getBinding();

        userID = mainActivity.user_id;
        chosenToothId = activityMainBinding.getModel().getChosenToothID();

        orientation = getResources().getConfiguration().orientation;

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events_list, container, false);

        model = new ViewModelProvider(this).get(EventsListViewModel.class);
        binding.setModel(model);

        Tooth tooth = activityMainBinding.getTeethFormulaFragment().binding.getTooth();
        binding.setTooth(tooth);


        binding.floatingActionButton.setOnClickListener(v -> {
            Log.d(TAG, "floating button was clicked");

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activityMainBinding.getViewData().setTeethFormulaFragmentVisibilityData(View.GONE);
            }

            activityMainBinding.getViewData().setEventFragmentVisibilityData(View.VISIBLE);
            activityMainBinding.getViewData().setNewEventFragmentVisibilityData(View.VISIBLE);
            activityMainBinding.getViewData().setEditEventFragmentVisibilityData(View.GONE);
            activityMainBinding.getViewData().setEventsListFragmentVisibilityData(View.GONE);

            activityMainBinding.getTeethFormulaFragment().refillEventsList();
            activityMainBinding.getNewEventFragment().event.setDate(new Date());
        });


        if (userID >= 0) createEventsList();

        if (chosenToothId > 0) {
            refillEventsList();
        }

        return binding.getRoot();
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
                                    //model.deleteEvent(eventModels.get(position));
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

    private void setVisibilities() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            activityMainBinding.getViewData().setTeethFormulaFragmentVisibilityData(View.GONE);
        }

        mainActivity.binding.getViewData().setEditEventFragmentVisibilityData(View.VISIBLE);
        mainActivity.binding.getViewData().setNewEventFragmentVisibilityData(View.GONE);
        mainActivity.binding.getViewData().setEventsListFragmentVisibilityData(View.GONE);

        mainActivity.binding.getTeethFormulaFragment().refillEventsList();
    }


    public void refillEventsList() {
        if (mainActivity.binding.getViewData().getEventsListFragmentVisibilityData() == View.VISIBLE) {
            eventModels.clear();
            //eventModels.addAll(model.getEventModelsList(userID, binding.getTooth().getId()));
            eventModels.addAll(mainActivity.getSortedEventsList());
            adapter.notifyDataSetChanged();
        }
    }

    public void deleteEventAnimation(int position) {
        eventModels.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
