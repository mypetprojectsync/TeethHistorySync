package com.appsverse.teethhistory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.appsverse.teethhistory.databinding.ActivityCreateNewUserBinding;
import com.appsverse.teethhistory.viewModels.CreateNewUserViewModel;

public class CreateNewUserActivity extends AppCompatActivity {

    public CreateNewUserViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCreateNewUserBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_create_new_user);

        model = new ViewModelProvider(this).get(CreateNewUserViewModel.class);
        binding.setModel(model);

        binding.nameTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > binding.nameTIL.getCounterMaxLength()) {
                    binding.nameTIL.setError(getString(R.string.name_error));
                    binding.createNewUserButton.setEnabled(false);
                } else if (s.length() > 0){
                    binding.nameTIL.setError(null);
                    binding.createNewUserButton.setEnabled(true);
                } else {
                    binding.nameTIL.setError(null);
                    binding.createNewUserButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int user_id = sharedPreferences.getInt("chosen_user_id", -1);

        if (user_id == -1) {
            finishAffinity();
        }
    }
}