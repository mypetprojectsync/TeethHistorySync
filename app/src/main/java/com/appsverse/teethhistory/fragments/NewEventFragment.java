package com.appsverse.teethhistory.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.adapters.EventPhotosListAdapter;
import com.appsverse.teethhistory.data.Event;
import com.appsverse.teethhistory.databinding.FragmentNewEventBinding;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.NewEventViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewEventFragment extends Fragment {

    NewEventViewModel model;
    public FragmentNewEventBinding binding;
    final String TAG = "myLogs";

    final Calendar myCalendar = Calendar.getInstance();

    Event event;

    ArrayAdapter adapter;
    List<String> list = new ArrayList<>();

    File directory;
    Uri publicPhotoUri;

    RecyclerView recyclerView;
    public EventPhotosListAdapter eventPhotosListAdapter;
    List<String> photosUri = new ArrayList<>();

    ActivityResultLauncher<Uri> mGetContent = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {

                        photosUri.add(publicPhotoUri.toString());
                        event.setPhotosUri(photosUri);

                        MediaScannerConnection.scanFile(getContext(), new String[]{publicPhotoUri.toString()}, null, null);

                        eventPhotosListAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "mGetContent photo canceled");
                    }
                }
            });

    ActivityResultLauncher<Intent> mGetGalleryContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Uri uri = result.getData().getData();

                        photosUri.add(getPathFromUri(uri));
                        event.setPhotosUri(photosUri);
                        eventPhotosListAdapter.notifyDataSetChanged();
                    }
                }
            }
    );

    ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Log.d(TAG, "permission result: " + result);

            final boolean[] permission = {true};

            result.forEach((k,v)-> {
                if (!v) permission[0] =false;
            });

            if (permission[0]) mGetContent.launch(generateFileUri());
        }
    });

    ActivityResultLauncher<String[]> galleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            result.forEach((k,v)-> {
                if (v) galleryButtonClicked();
            });
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "NewEventFragment onCreateView");

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_event, container, false);

        model = new ViewModelProvider(this).get(NewEventViewModel.class);
        binding.setModel(model);

       Log.d(TAG, model.newEventViewModelState());

        event = new Event(model.getId(), model.getPosition(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes(), model.getActions(), model.getPhotosUri());

        binding.setEvent(event);

        setDatePicker(event);

         //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android

        adapter = new ArrayAdapter<>(this.getContext(), R.layout.dropdown_menu_popup_item, list);
        binding.toothActionACTV.setAdapter(adapter);

        //todo list lost when chosen some item and orientation changed. Issue https://github.com/material-components/material-components-android/issues/1464

        binding.toothActionACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                event.setAction(list.get(position));
            }
        });
        binding.guaranteeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                event.setGuarantee(Math.round(value));
            }
        });
        setTextActionACTV();

        binding.photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCameraPermissions();
            }
        });

        binding.galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT > 29) {
                    galleryButtonClicked();
                } else {
                    galleryPermissionLauncher.launch(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    });
                }
            }
        });

        createDirectory();
        createEventPhotosList();

        return binding.getRoot();
    }


    private String getPathFromUri(Uri data) {
        Context context = getContext();
        Cursor cursor = context.getContentResolver().query(data, null, null, null, null);
        cursor.moveToFirst();
        String image_id = cursor.getString(0);
        image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = context.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void createEventPhotosList() {

        recyclerView = binding.listEventPhotos;

        eventPhotosListAdapter = new EventPhotosListAdapter(photosUri);
        recyclerView.setAdapter(eventPhotosListAdapter);

        eventPhotosListAdapter.setClickListener(new EventPhotosListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse(photosUri.get(position)), "image/*");
                startActivity(intent);
            }
        });
    }

    private void galleryButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetGalleryContent.launch(intent);
    }

    private Uri generateFileUri() {

        MainActivity mainActivity = (MainActivity) getActivity();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File file = new File(directory.getAbsolutePath(),
                mainActivity.binding.getUser().getName() + "_"
                        + mainActivity.binding.getModel().getChosenToothID() + "_"
                        + event.getId() + "_"
                        + timeStamp + ".jpg");

        Uri imageUri = FileProvider.getUriForFile(this.getContext(), "com.appsverse.teethhistory.fileprovider", file);

        publicPhotoUri = Uri.parse(file.getAbsolutePath());
        return imageUri;
    }

    private void verifyCameraPermissions() {

        if (android.os.Build.VERSION.SDK_INT <= 29) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA
            });
        }
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "TeethHistory");

        if (!directory.exists()) directory.mkdirs();
    }

    private void setDatePicker(Event event) {

        binding.newEventDateTv.setOnClickListener(v -> {

            MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
            builder.setSelection(event.getDate().getTime());
            MaterialDatePicker picker = builder.build();

            picker.show(this.getActivity().getSupportFragmentManager(), picker.toString());
            picker.addOnPositiveButtonClickListener(selection -> {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selection);
                event.setDate(new Date((Long) selection));
                Log.d(TAG, date);
            });
        });
    }

    public void setTextActionACTV() {
        Log.d(TAG, "setTextActionACTV()");

        ToothModel toothModel = model.getToothModel((MainActivity) getActivity());
        String[] items;
        if (toothModel != null) {

            if (!toothModel.isExist()) {
                if (toothModel.isBabyTooth()) {
                    items = getResources().getStringArray(R.array.no_grown_tooth_actions);
                } else if (toothModel.isPermanentTooth()) {
                    items = getResources().getStringArray(R.array.no_grown_tooth_actions);
                } else {
                    items = getResources().getStringArray(R.array.extracted_permanent_tooth_actions);
                }

            } else {
                if (toothModel.isBabyTooth()) {
                    items = getResources().getStringArray(R.array.baby_tooth_actions);
                } else if (toothModel.isPermanentTooth()) {
                    items = getResources().getStringArray(R.array.permanent_tooth_actions);
                } else if (toothModel.isImplant()) {
                    items = getResources().getStringArray(R.array.implanted_tooth_actions);
                } else {
                    items = new String[0];
                }
            }

            list.clear();
            Collections.addAll(list, items);

            Log.d(TAG, "setTextActionACTV() list: " + list.toString());

            binding.toothActionACTV.setText(list.get(0), false);
            event.setAction(list.get(0));
            event.setActions(list);
            //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "NewEventFragment onDestroy");

        model.setPosition(event.getPosition());
        model.setDate(event.getDate());
        model.setAction(event.getAction());
        model.setGuarantee(event.getGuarantee());
        model.setNotes(event.getNotes());
        model.setActions(event.getActions());
        model.setPhotosUri(event.getPhotosUri());
    }
}
