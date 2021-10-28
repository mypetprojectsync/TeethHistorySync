package com.appsverse.teethhistory.handlers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.appsverse.teethhistory.CreateNewUserActivity;
import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.databinding.EditUsernameDialogBinding;
import com.appsverse.teethhistory.repository.UserModel;
import com.appsverse.teethhistory.viewModels.MainActivityViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class OnClickHandler {

    private static final int REQUEST_FOCUS_DELAY = 300;

    @SuppressLint("NonConstantResourceId")
    public void onMainActivityClick(ActivityMainBinding binding, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share_database_menu_item:
                shareDatabase(binding);
                break;
            case R.id.import_menu_item:
                verifyStoragePermissions(binding);
                break;
            case R.id.create_new_user_menu_item:
                createNewUserActivityStart(binding);
                break;
            case R.id.choose_user_menu_item:
                createChooseUserSubmenu(item, binding);
                break;
            case R.id.edit_user_menu_item:
                createEditUserNameDialog(binding);
                break;
            case R.id.delete_user_menu_item:
                deleteUser(binding);
                break;
            default:
                setChosenUser(item, binding);
                restartMainActivity(binding);
                break;
        }
    }

    public void verifyStoragePermissions(ActivityMainBinding binding) {

        ((MainActivity) binding.getRoot().getContext()).permissionLauncher.launch(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }

    private void shareDatabase(ActivityMainBinding binding) {

        MainActivityViewModel model = binding.getModel();

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            binding.getRoot().getContext().openFileOutput(binding.getModel().getUsername() + "_TeethHistory_backup.txt", Context.MODE_PRIVATE)));
            bufferedWriter.write(model.getChosenUserDatabaseInJson());
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File tempFile = new File(binding.getRoot().getContext().getFilesDir(), binding.getModel().getUsername() + "_TeethHistory_backup.txt");
        Uri uri = FileProvider.getUriForFile(binding.getRoot().getContext(), "com.appsverse.teethhistory.fileprovider", tempFile);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("text/*");

        binding.getRoot().getContext().startActivity(Intent.createChooser(intent, binding.getRoot().getContext().getString(R.string.share)));

    }

    private void createNewUserActivityStart(ActivityMainBinding binding) {
        Intent intent = new Intent(binding.getRoot().getContext(), CreateNewUserActivity.class);
        binding.getRoot().getContext().startActivity(intent);
    }

    public void createChooseUserSubmenu(MenuItem item, ActivityMainBinding binding) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(binding.getRoot().getContext());
        int user_id = sharedPreferences.getInt("chosen_user_id", -1);

        SubMenu subMenu = item.getSubMenu();
        subMenu.clear();

        for (UserModel user_model : binding.getModel().getAllUsers()) {
            if (user_model.getId() != user_id)
                subMenu.addSubMenu(0, user_model.getId(), Menu.NONE, user_model.getName());
        }
    }

    private void setChosenUser(MenuItem item, ActivityMainBinding binding) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(binding.getRoot().getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("chosen_user_id", item.getItemId());
        editor.apply();
    }

    private void restartMainActivity(ActivityMainBinding binding) {
        Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        binding.getRoot().getContext().startActivity(intent);
    }


    public void createEditUserNameDialog(ActivityMainBinding binding) {

        MainActivityViewModel model = binding.getModel();

        EditUsernameDialogBinding editUsernameBinding = DataBindingUtil.inflate(LayoutInflater.from(binding.getRoot().getContext()), R.layout.edit_username_dialog, null, false);

        editUsernameBinding.setModel(model);

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(binding.getRoot().getContext());
        dialog.setView(editUsernameBinding.getRoot().getRootView());
        dialog.setTitle(R.string.edit_username);

        editUsernameBinding.editNameTIET.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && editUsernameBinding.editNameTIET.isEnabled() && editUsernameBinding.editNameTIET.isFocusable()) {
                editUsernameBinding.editNameTIET.post(() -> {
                    final InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editUsernameBinding.editNameTIET, InputMethodManager.HIDE_IMPLICIT_ONLY);
                });
            }
        });

        Activity activity = (Activity) binding.getRoot().getContext();
        Single.fromCallable(() -> requestFocusWithDelay(editUsernameBinding, activity)).subscribeOn(Schedulers.io()).subscribe();

        editUsernameBinding.editNameTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                model.setUsername(s.toString());

                if (s.length() > editUsernameBinding.editNameTIL.getCounterMaxLength()) {
                    editUsernameBinding.editNameTIL.setError(activity.getString(R.string.name_error));
                    model.getEditUserDialog().getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                } else if (s.length() > 0) {
                    editUsernameBinding.editNameTIL.setError(null);
                    model.getEditUserDialog().getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    editUsernameBinding.editNameTIL.setError(null);
                    model.getEditUserDialog().getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        dialog.setPositiveButton(R.string.ok, (dialog1, which) -> {
            model.updateUsername(model.getUsername().trim());
            ((MainActivity) activity).getSupportActionBar().setTitle(model.getUsername());
        });

        dialog.setNegativeButton(R.string.cancel, (dialog12, which) -> {
        });

        dialog.setOnDismissListener(dialog13 -> {

            if (!model.getUsername().equals(model.getUsernameFromRealm()) && !model.getEditUserDialog().isShowing()) {
                model.setUsername(model.getUsernameFromRealm());
            }

            if (model.getEditUserDialog() != null && !model.getEditUserDialog().isShowing()) {
                model.setEditUsernameDialogActive(false);
            }
        });

        model.setEditUserDialog(dialog.create());

        model.getEditUserDialog().show();

        model.setEditUsernameDialogActive(true);
    }

    private int requestFocusWithDelay(EditUsernameDialogBinding editUsernameBinding, Activity activity) {
        SystemClock.sleep(REQUEST_FOCUS_DELAY);
        activity.runOnUiThread(() -> {
            editUsernameBinding.editNameTIET.requestFocus();
            editUsernameBinding.editNameTIET.setSelection(editUsernameBinding.editNameTIET.length());
        });
        return 0;
    }

    public void deleteUser(ActivityMainBinding binding) {

        final int[] newUserID = new int[1];

        MainActivityViewModel model = binding.getModel();

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(binding.getRoot().getContext());
        dialogBuilder.setTitle(R.string.question_delete_user);
        dialogBuilder.setPositiveButton(R.string.ok, (dialog1, which) -> {

            model.deleteUserPhotos();

            model.deleteUser();

            if (model.isUserExist()) {

                model.setMainActivityViewModelData(model.getFirstUserID());

                restartMainActivity(binding);

                restartMainActivity(binding);

            } else {
                newUserID[0] = -1;

                Intent intent = new Intent(binding.getRoot().getContext(), CreateNewUserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                binding.getRoot().getContext().startActivity(intent);

                Activity activity = (Activity) binding.getRoot().getContext();
                activity.finish();
            }

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(binding.getRoot().getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("chosen_user_id", newUserID[0]);
            editor.apply();
        });

        dialogBuilder.setNegativeButton(R.string.cancel, null);
        dialogBuilder.setOnDismissListener(dialog12 -> {
            if (model.getDeleteUserDialog() != null && !model.getDeleteUserDialog().isShowing()) {
                model.setDeleteUserDialogActive(false);
            }
        });

        model.setDeleteUserDialog(dialogBuilder.create());
        model.getDeleteUserDialog().show();

        model.setDeleteUserDialogActive(true);

    }

}
