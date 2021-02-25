package com.appsverse.teethhistory;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.databinding.ActivityCreateNewUserBinding;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.viewModels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo 1. add git
        //todo 2. add shared preferences for chosen user

        MainActivityViewModel model = new MainActivityViewModel();


        if (model.isUserExist()) {
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            User user;
            if (model.getUser() == null) {
                user = new User("kek", false);
            } else {
                user = new User(model.getUser().getName(), model.getUser().isBabyTeeth());
            }
            binding.setUser(user);

            Log.d(TAG, "user exists");
        } else {
            Intent intent = new Intent(this, CreateNewUserActivity.class);
            this.startActivity(intent);
        }
    }
}