package com.appsverse.teethhistory.handlers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.appsverse.teethhistory.CreateNewUserActivity;
import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.data.User;
import com.appsverse.teethhistory.databinding.ActivityMainBinding;
import com.appsverse.teethhistory.databinding.EditUsernameDialogBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.repository.UserModel;
import com.appsverse.teethhistory.viewModels.MainActivityViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;

public class OnClickHandler {

    final String TAG = "myLogs";

    @SuppressLint("NonConstantResourceId")
    public void onMainActivityClick(ActivityMainBinding binding, ActivityResultLauncher<String> mGetContent, MenuItem item) {

            Log.d(TAG, "Clicked on submenu item id: " + item.getItemId() + " name: " + item.getTitle());

        MainActivityViewModel model = binding.getModel();

            switch (item.getItemId()) {
                case R.id.share_database_menu_item:
                    shareDatabase(binding, model);
                    break;
                case R.id.import_menu_item:
                    verifyStoragePermissions((MainActivity) binding.getRoot().getContext(), mGetContent);

                    break;
                case R.id.activity_main_settings_menu_item:
                    break;
                case R.id.create_new_user_menu_item:
                    createNewUserActivityStart(binding);
                    break;
                case R.id.choose_user_menu_item:
                    createChooseUserSubmenu(item, model, binding);
                    break;
                case R.id.edit_user_menu_item:
                    createEditUserNameDialog(binding);
                    break;
                case R.id.delete_user_menu_item:
                    deleteUser(binding, model);
                    break;
                default:
                    setChosenUser(item, binding);
                    restartMainActivity(binding);
                    break;
            }
    }

    public void verifyStoragePermissions(Activity activity, ActivityResultLauncher<String> mGetContent) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else {
            //todo! bug in toolbar menu (choose user) when file imported
            importFile(mGetContent);
        }
    }

    private void importFile(ActivityResultLauncher<String> mGetContent) {
        mGetContent.launch("text/*");
    }



    private void shareDatabase(ActivityMainBinding binding, MainActivityViewModel model) {


        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            binding.getRoot().getContext().openFileOutput("fileForShare.txt", Context.MODE_PRIVATE)));
            bufferedWriter.write(model.getDatabaseInJson(binding));
            bufferedWriter.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File tempFile = new File(binding.getRoot().getContext().getFilesDir(),"fileForShare.txt");
        Uri uri = FileProvider.getUriForFile(binding.getRoot().getContext(),"com.appsverse.teethhistory.fileprovider",tempFile);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
Log.d(TAG, binding.getRoot().getContext().getFilesDir().getAbsolutePath());
        intent.setType("text/*");

        binding.getRoot().getContext().startActivity(Intent.createChooser(intent, "Поделиться"));

    }

    private void createNewUserActivityStart(ActivityMainBinding binding) {
        Intent intent = new Intent(binding.getRoot().getContext(), CreateNewUserActivity.class);
        binding.getRoot().getContext().startActivity(intent);
    }

    //todo найти способ добыть контекст из menuitem
    public void createChooseUserSubmenu(MenuItem item, MainActivityViewModel model, ActivityMainBinding binding) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(binding.getRoot().getContext());
        int user_id = sharedPreferences.getInt("chosen_user_id", -1);

        SubMenu subMenu = item.getSubMenu();

        for (UserModel user_model : model.getAllUsers()) {
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

        User user = binding.getUser();
        MainActivityViewModel model = binding.getModel();

        EditUsernameDialogBinding editUsernameBinding = DataBindingUtil.inflate(LayoutInflater.from(binding.getRoot().getContext()), R.layout.edit_username_dialog, null, false);
        editUsernameBinding.setUser(user);

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

        //todo try another type of TIET layout (more information on material.io)
        //todo sometimes keyboard didn't show when orientation changed
        //todo clear spaces after save

        Activity activity = (Activity) binding.getRoot().getContext();
        Single.fromCallable(() -> requestFocusWithDelay(300, editUsernameBinding, activity)).subscribeOn(Schedulers.io()).subscribe();

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
                } else if (s.length() > 0){
                    editUsernameBinding.editNameTIL.setError(null);
                    model.getEditUserDialog().getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    editUsernameBinding.editNameTIL.setError(null);
                    model.getEditUserDialog().getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        dialog.setPositiveButton(R.string.ok, (dialog1, which) -> {
            model.updateUsername(user.getName());
            ((MainActivity) activity).getSupportActionBar().setTitle(model.getUsername());
        });

        dialog.setNegativeButton(R.string.cancel, (dialog12, which) -> {});

        dialog.setOnDismissListener(dialog13 -> {

            if (!model.getUsername().equals(model.getUsernameFromRealm()) && !model.getEditUserDialog().isShowing()) {
                model.setUsername(model.getUsernameFromRealm());
                user.setName(model.getUsername());
            }

            if (model.getEditUserDialog() != null && !model.getEditUserDialog().isShowing()) {
                model.setEditUsernameDialogActive(false);
            }
        });

        model.setEditUserDialog(dialog.create());

        model.getEditUserDialog().show();
        if (editUsernameBinding.editNameTIET.getText().length() < 1) model.getEditUserDialog().getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        model.setEditUsernameDialogActive(true);
    }

    private int requestFocusWithDelay(int delay, EditUsernameDialogBinding editUsernameBinding, Activity activity) {
        SystemClock.sleep(delay);
        activity.runOnUiThread(() -> {
            editUsernameBinding.editNameTIET.requestFocus();
            editUsernameBinding.editNameTIET.setSelection(editUsernameBinding.editNameTIET.length());
        });
        return 0;
    }

    public void deleteUser(ActivityMainBinding binding, MainActivityViewModel model) {

        final int[] newUserID = new int[1];

        MaterialAlertDialogBuilder dialogBuider = new MaterialAlertDialogBuilder(binding.getRoot().getContext());
        dialogBuider.setTitle(R.string.question_delete_user);
        dialogBuider.setPositiveButton(R.string.ok, (dialog1, which) -> {

            model.deleteUserPhotos();

            model.deleteUser();

            if (model.isUserExist()) {

                newUserID[0] = model.getFirstUserID();

                model.setMainActivityViewModelData(newUserID[0]);
                binding.setUser(new User(model.getUsername(), model.isNoTeeth(), model.isBabyTeeth()));

                ((MainActivity) binding.getRoot().getContext()).getSupportActionBar().setTitle(model.getUsername());

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

        dialogBuider.setNegativeButton(R.string.cancel, null);
        dialogBuider.setOnDismissListener(dialog12 -> {
            if (model.getDeleteUserDialog() != null && !model.getDeleteUserDialog().isShowing()) {
                model.setDeleteUserDialogActive(false);
            }
        });

        model.setDeleteUserDialog(dialogBuider.create());
        model.getDeleteUserDialog().show();

        model.setDeleteUserDialogActive(true);

        //todo update activity after delete user. Take code from change user

    }

}
