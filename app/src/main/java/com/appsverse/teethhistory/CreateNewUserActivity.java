package com.appsverse.teethhistory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.viewModels.CreateNewUserViewModel;
import com.appsverse.teethhistory.databinding.ActivityCreateNewUserBinding;

public class CreateNewUserActivity extends AppCompatActivity {

    public CreateNewUserViewModel model;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCreateNewUserBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_create_new_user);

        model = new ViewModelProvider(this).get(CreateNewUserViewModel.class);
        binding.setModel(model);
        user = new User(model.getName(), model.isBabyTeeth());
        binding.setUser(user);

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
    protected void onDestroy() {
        super.onDestroy();
        model.setName(user.getName());
        model.setBabyTeeth(user.isBabyTeeth());
    }
}
