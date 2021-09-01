package com.appsverse.teethhistory.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.appsverse.teethhistory.databinding.DialogToothStateBinding;
import com.appsverse.teethhistory.databinding.FragmentTeethFormulaBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.TeethFormulaFragmentViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeethFormulaFragment extends Fragment {

    final int MINIMAL_POSITION_IMAGE_ID = 1000;

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
    List<ToothModel> toothModels;

    int orientation;

    DialogToothStateBinding dialogToothStateBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "TeethFormulaFragment on createView started");

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

            toothModels = model.getAllToothModelsForUser(user_id);

            tooth = new Tooth(model.getChosenToothID(), model.getChosenToothPosition());

            binding.setTooth(tooth);

            //todo hide teeth layout when events list recyclerview scroll down and show when it scroll up

            for (int i = 0; i < 16; i++) {
                binding.llTeethFirstRow.addView(setToothImage(i));
            }

            for (int i = 16; i < 32; i++) {
                binding.llTeethSecondRow.addView(setToothImage(i));
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

    private LinearLayout setToothImage(int i) {

        LinearLayout linearLayout = new LinearLayout(this.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView toothIV = getToothImageView(i);
        int id;

        ImageView toothPositionIV = new ImageView(this.getContext());
        toothPositionIV.setScaleType(ImageView.ScaleType.FIT_START);

        String toothNumber = "ic_" + toothModels.get(i).getPosition();
        id = getResources().getIdentifier(toothNumber, "drawable", getActivity().getPackageName());
        toothPositionIV.setImageResource(id);
        toothPositionIV.setAdjustViewBounds(true);
        toothPositionIV.setId(toothModels.get(i).getId()+MINIMAL_POSITION_IMAGE_ID);

        if (i >= 0 && i < 16) {
            linearLayout.addView(toothIV);
            linearLayout.addView(toothPositionIV);
        } else {
            linearLayout.addView(toothPositionIV);
            linearLayout.addView(toothIV);

        }

        toothIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toothClicked(v);
            }
        });

        toothIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                toothClicked(v);

                toothLongClicked(v);

                return false;
            }
        });

        return linearLayout;
    }

    private void toothLongClicked(View v) {

        dialogToothStateBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_tooth_state, null, false);
        dialogToothStateBinding.setTooth(tooth);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());

        dialogBuilder.setView(dialogToothStateBinding.getRoot().getRootView());

        dialogBuilder.setTitle(getString(R.string.edit_tooth_state) + tooth.getPosition() + getString(R.string.state));
        dialogBuilder.setPositiveButton(R.string.ok, (dialog, which) -> {
            model.saveTooth(tooth, (MainActivity) getActivity());

            ImageView toothPositionIV = mainActivity.binding.getTeethFormulaFragment().binding.getRoot().findViewById(tooth.getId()+1000);
            String toothNumber = "ic_" + tooth.getPosition();
            int id = getContext().getResources().getIdentifier(toothNumber, "drawable", getActivity().getPackageName());
            toothPositionIV.setImageResource(id);
            toothPositionIV.setAdjustViewBounds(true);
        });
        dialogBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> {});
        dialogBuilder.show();

    }

    private ImageView getToothImageView(int i) {

        ImageView toothIV = new ImageView(this.getContext());
        toothIV.setScaleType(ImageView.ScaleType.FIT_START);


        if (toothModels.get(i).isExist()) {

            int position = toothModels.get(i).getPosition();

            if (position > 50) position = position - 40;

            String toothIcon = "ic_" + position + chooseToothState(i);
            int id = getResources().getIdentifier(toothIcon, "drawable", getActivity().getPackageName());
            toothIV.setImageResource(id);
        } else {
            setGum(toothIV, toothModels.get(i).getId());
        }
        toothIV.setAdjustViewBounds(true);
        toothIV.setTag(toothModels.get(i).getPosition());
        toothIV.setId(toothModels.get(i).getId());
        return toothIV;
    }

    private String chooseToothState() {

        if (tooth.isImplant()) {
            return "i";
        } else if (tooth.isFilling()) {
            return "f";
        } else {
            return "g";
        }

    }

    private String chooseToothState(int i) {
        if (toothModels.get(i).isImplant()) {
            return "i";
        } else if (toothModels.get(i).isFilling()) {
            return "f";
        } else {
            return "g";
        }
    }

    private void toothClicked(View view) {

        if (activityMainBinding.getModel().getChosenToothID() > 0) {
            Log.d(TAG, "previous tooth condition: " + tooth.getToothState());


            ImageView toothIV = (ImageView) binding.getRoot().findViewById(tooth.getId());

            if (tooth.isExist()) {

                String toothDrawableId = "ic_" + tooth.getId() + chooseToothState();
                int id = getResources().getIdentifier(toothDrawableId, "drawable", getActivity().getPackageName());
                toothIV.setImageResource(id);
            } else {
                setGum(toothIV, tooth.getId());
            }
        }

        activityMainBinding.getModel().setChosenToothID((Integer) view.getId());
        setTooth();

        String toothDrawableId = "ic_" + tooth.getId() + "g_selected";
        int id = getResources().getIdentifier(toothDrawableId, "drawable", getActivity().getPackageName());
        ((ImageView) binding.getRoot().findViewById(tooth.getId())).setImageResource(id);

        Log.d(TAG, "chosen tooth condition: " + tooth.getToothState());

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

    private void setGum(ImageView toothIV, int toothId) {
        if (toothId > 10 && toothId < 30) {
            toothIV.setImageResource(R.drawable.ic_gum_top);
        } else {
            toothIV.setImageResource(R.drawable.ic_gum_bottom);
        }
    }

    public void setTooth() {
        tooth = model.setTooth(tooth, ((MainActivity) getActivity()));
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
                                    mainActivity.deleteEvent(eventModels.get(position));
                                    deleteEventAnimation(position);
                                });
                                dialogBuilder.setNegativeButton("cancel", (dialog, which) -> {
                                });
                                dialogBuilder.show();
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
