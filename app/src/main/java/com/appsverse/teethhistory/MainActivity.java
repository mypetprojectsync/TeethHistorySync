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
import java.io.InputStream;
import java.io.InputStreamReader;

import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel model;
    public ActivityMainBinding binding;

    public int user_id;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        try {
                            InputStream  inputStream = getContentResolver().openInputStream(uri);

                            String ret = convertStreamToString(inputStream);

                            inputStream.close();

                            model.copyJsonToRealm(ret);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result  -> result.forEach((k, v) -> {
        if (v) importFile(mGetContent);
    }));

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

            AdRequest adRequest = new AdRequest.Builder().build();

            setBinding();

            if (model.isEditUsernameDialogActive()) {
                OnClickHandler onClickHandler = new OnClickHandler();
                onClickHandler.createEditUserNameDialog(binding);
            }

            if (model.isDeleteUserDialogActive()) {
                OnClickHandler onClickHandler = new OnClickHandler();
                onClickHandler.deleteUser(binding);
            }

            setFragmentsVisibilities(adRequest);

            getSupportActionBar().setTitle(model.getUsername());

        } else {
            Intent intent = new Intent(this, CreateNewUserActivity.class);
            this.startActivity(intent);
        }
    }

    private void setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setModel(model);

        mAdView = binding.adView;


        TeethFormulaFragment fragment = (TeethFormulaFragment) getSupportFragmentManager().findFragmentById(R.id.teeth_formula_fragment);
        binding.setTeethFormulaFragment(fragment);

        EditEventFragment editEventFragment = (EditEventFragment) getSupportFragmentManager().findFragmentById(R.id.edit_event_fragment);
        binding.setEditEventFragment(editEventFragment);

        EventsListFragment eventsListFragment = (EventsListFragment) getSupportFragmentManager().findFragmentById(R.id.events_list_fragment);
        binding.setEventsListFragment(eventsListFragment);

        NewEventFragment newEventFragment = (NewEventFragment) getSupportFragmentManager().findFragmentById(R.id.new_event_fragment);
        binding.setNewEventFragment(newEventFragment);

    }

    private void setFragmentsVisibilities(AdRequest adRequest) {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            model.setTeethFormulaFragmentVisibility(View.VISIBLE);
            model.setEventFragmentVisibilityData(View.VISIBLE);

            if (model.getNewEventFragmentVisibility() == View.GONE
                    && model.getEditEventFragmentVisibilityData() == View.GONE) {
                model.setEventsListFragmentVisibilityData(View.VISIBLE);
            }

        } else {
            mAdView.loadAd(adRequest);

            if (model.getNewEventFragmentVisibility() == View.VISIBLE || model.getEditEventFragmentVisibilityData() == View.VISIBLE) {
                model.setTeethFormulaFragmentVisibility(View.GONE);
                model.setEventFragmentVisibilityData(View.VISIBLE);
            } else {
                model.setTeethFormulaFragmentVisibility(View.VISIBLE);
                model.setEventFragmentVisibilityData(View.GONE);
            }
            model.setEventsListFragmentVisibilityData(View.GONE);
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
        handler.onMainActivityClick(binding, item);

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

    private void importFile(ActivityResultLauncher<String> mGetContent) {
        mGetContent.launch("text/*");
    }

    @Override
    public void onBackPressed() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        } else {
            if (model.getNewEventFragmentVisibility() == View.VISIBLE || model.getEditEventFragmentVisibilityData() == View.VISIBLE) {

                model.setEventFragmentVisibilityData(View.GONE);
                model.setNewEventFragmentVisibility(View.GONE);
                model.setEditEventFragmentVisibilityData(View.GONE);
                model.setTeethFormulaFragmentVisibility(View.VISIBLE);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}





