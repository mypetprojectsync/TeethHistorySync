package com.appsverse.teethhistory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.appsverse.teethhistory.data.MainActivityViewData;
import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.fragments.EditEventFragment;
import com.appsverse.teethhistory.fragments.EventsListFragment;
import com.appsverse.teethhistory.fragments.NewEventFragment;
import com.appsverse.teethhistory.fragments.TeethFormulaFragment;
import com.appsverse.teethhistory.handlers.OnClickHandler;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.viewModels.MainActivityViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel model;
    public ActivityMainBinding binding;
    MainActivityViewData mainActivityViewData;

    public int user_id;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {

                    if (uri != null) {

                        File file = new File(uri.getPath());
                        if (file.exists()) {
                            FileInputStream fin = null;
                            try {
                                fin = new FileInputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            String ret = "";
                            try {
                                ret = convertStreamToString(fin);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                fin.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                           model.copyJsonToRealm(ret);
                        }
                    }
                }
            });

    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_id = sharedPreferences.getInt("chosen_user_id", -1);

        model = new ViewModelProvider(this).get(MainActivityViewModel.class);
        if (user_id >= 0 && model.getUsername() == null) {
            model.setMainActivityViewModelData(user_id);
        }

        if (model.isUserExist()) {

            binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            binding.setModel(model);

            mAdView = binding.adView;
            AdRequest adRequest = new AdRequest.Builder().build();


            User user = new User(model.getUsername(), model.isNoTeeth(), model.isBabyTeeth());
            binding.setUser(user);

            TeethFormulaFragment fragment = (TeethFormulaFragment) getSupportFragmentManager().findFragmentById(R.id.teeth_formula_fragment);
            binding.setTeethFormulaFragment(fragment);

            EditEventFragment editEventFragment = (EditEventFragment) getSupportFragmentManager().findFragmentById(R.id.edit_event_fragment);
            binding.setEditEventFragment(editEventFragment);

            EventsListFragment eventsListFragment = (EventsListFragment) getSupportFragmentManager().findFragmentById(R.id.events_list_fragment);
            binding.setEventsListFragment(eventsListFragment);

            NewEventFragment newEventFragment = (NewEventFragment) getSupportFragmentManager().findFragmentById(R.id.new_event_fragment);
            binding.setNewEventFragment(newEventFragment);

            mainActivityViewData = new MainActivityViewData(
                    model.getTeethFormulaFragmentVisibility(),
                    model.getNewEventFragmentVisibility(),
                    model.getEditEventFragmentVisibilityData(),
                    model.getEventFragmentVisibilityData(),
                    model.getEventsListFragmentVisibilityData());
            binding.setViewData(mainActivityViewData);

            if (model.isEditUsernameDialogActive()) {
                OnClickHandler onClickHandler = new OnClickHandler();
                onClickHandler.createEditUserNameDialog(binding);
            }

            if (model.isDeleteUserDialogActive()) {
                OnClickHandler onClickHandler = new OnClickHandler();
                onClickHandler.deleteUser(binding, model);
            }

            setFragmentsVisibilities(adRequest);

            getSupportActionBar().setTitle(user.getName());

        } else {
            Intent intent = new Intent(this, CreateNewUserActivity.class);
            this.startActivity(intent);
        }


    }

    private void setFragmentsVisibilities(AdRequest adRequest) {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            mainActivityViewData.setTeethFormulaFragmentVisibilityData(View.VISIBLE);
            mainActivityViewData.setEventFragmentVisibilityData(View.VISIBLE);

            if (model.getNewEventFragmentVisibility() == View.GONE
                    && model.getEditEventFragmentVisibilityData() == View.GONE) {
                mainActivityViewData.setEventsListFragmentVisibilityData(View.VISIBLE);
            }

        } else {
            mAdView.loadAd(adRequest);

            if (model.getNewEventFragmentVisibility() == View.VISIBLE || model.getEditEventFragmentVisibilityData() == View.VISIBLE) {
                mainActivityViewData.setTeethFormulaFragmentVisibilityData(View.GONE);
                mainActivityViewData.setEventFragmentVisibilityData(View.VISIBLE);
            } else {
                binding.getViewData().setTeethFormulaFragmentVisibilityData(View.VISIBLE);
                mainActivityViewData.setEventFragmentVisibilityData(View.GONE);
            }
            mainActivityViewData.setEventsListFragmentVisibilityData(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        OnClickHandler handler = new OnClickHandler();
        handler.onMainActivityClick(binding, mGetContent, item);

        return super.onOptionsItemSelected(item);
    }

    public ActivityMainBinding getBinding() {
        return this.binding;
    }

    public RealmResults<EventModel> getSortedEventsList() {
        return model.getSortedEventsList();
    }

    public void deleteEvent(EventModel eventModel) {
        model.deleteEvent(eventModel, this);
    }

    @Override
    public void onBackPressed() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        } else {
            if (mainActivityViewData.getNewEventFragmentVisibilityData() == View.VISIBLE || mainActivityViewData.getEditEventFragmentVisibilityData() == View.VISIBLE) {

                mainActivityViewData.setEventFragmentVisibilityData(View.GONE);
                mainActivityViewData.setNewEventFragmentVisibilityData(View.GONE);
                mainActivityViewData.setEditEventFragmentVisibilityData(View.GONE);
                mainActivityViewData.setTeethFormulaFragmentVisibilityData(View.VISIBLE);

            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (model.getEditUserDialog() != null) model.getEditUserDialog().dismiss();
        if (model.getDeleteUserDialog() != null) model.getDeleteUserDialog().dismiss();

        if (mainActivityViewData != null) {
            model.setTeethFormulaFragmentVisibility(mainActivityViewData.getTeethFormulaFragmentVisibilityData());
            model.setNewEventFragmentVisibility(mainActivityViewData.getNewEventFragmentVisibilityData());
            model.setEditEventFragmentVisibilityData(mainActivityViewData.getEditEventFragmentVisibilityData());
            model.setEventFragmentVisibilityData(mainActivityViewData.getEventFragmentVisibilityData());
            model.setEventsListFragmentVisibilityData(mainActivityViewData.getEventsListFragmentVisibilityData());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}


//TODO Когда открыто событие и оно же удаляется, фрагмент открытого события не скрывается и не обновляется