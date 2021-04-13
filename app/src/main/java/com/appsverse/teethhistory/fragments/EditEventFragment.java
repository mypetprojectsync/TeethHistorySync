package com.appsverse.teethhistory.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.adapters.EventPhotosListAdapter;
import com.appsverse.teethhistory.data.Event;
import com.appsverse.teethhistory.databinding.FragmentEditEventBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.EditEventViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditEventFragment extends Fragment {

    EditEventViewModel model;
    FragmentEditEventBinding binding;
    final String TAG = "myLogs";

    Event event;
    ArrayAdapter adapter;
    List<String> list = new ArrayList<>();

    File directory;

    Uri nonpublicUri;
    Uri publicPhotoUri;

    ActivityResultLauncher<Uri> mGetContent = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {

                        model.addNewPhotoUri(((MainActivity) getActivity()), event, publicPhotoUri);
                        //getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, publicPhotoUri));
                        //getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, publicPhotoUri));

                        MediaScannerConnection.scanFile(getContext(), new String[]{publicPhotoUri.toString()}, null, null);

                        Log.d(TAG, "event photos Uri: " + model.getPhotosUri(((MainActivity) getActivity()), event));

                        refillPhotosUriList();

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
                    Intent data = result.getData();
                    Log.d(TAG, "mGetGalleryContent result: " + data.toString());
                }
            }
        }
);

    RecyclerView recyclerView;
    EventPhotosListAdapter eventPhotosListAdapter;
    List<String> photosUri = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_event, container, false);

        model = new ViewModelProvider(this).get(EditEventViewModel.class);
        binding.setModel(model);

        event = new Event(model.getId(), model.getPosition(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes(), model.getActions());
        //event = new Event(model.getId(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes());
        binding.setEvent(event);

        setDatePicker(event);


        //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
        /*String[] items;
        ToothModel toothModel = model.getToothModel((MainActivity) getActivity());
        if (toothModel != null) {
        if (!toothModel.isExist()) {
            if (toothModel.isBabyTooth()) {
                items = getResources().getStringArray(R.array.no_grown_tooth_actions);
            } else if (toothModel.isPermanentTooth()) {
                items = getResources().getStringArray(R.array.no_grown_tooth_actions);
            } else  if (toothModel.isImplant()) {
                items = getResources().getStringArray(R.array.extracted_permanent_tooth_actions);
            }else {
                items = new String[0];
            }
        } else {
            if (toothModel.isBabyTooth()) {
                items = getResources().getStringArray(R.array.baby_tooth_actions);
            } else if (toothModel.isPermanentTooth()) {
                items = getResources().getStringArray(R.array.permanent_tooth_actions);
            } else  if (toothModel.isImplant()) {
                items = getResources().getStringArray(R.array.implanted_tooth_actions);
            } else {
                items = new String[0];
            }
        }
        } else {
            items = new String[0];
        }*/
        adapter = new ArrayAdapter<>(this.getContext(), R.layout.dropdown_menu_popup_item, list);

        binding.editToothActionACTV.setAdapter(adapter);

        //todo list lost when chosen some item and orientation changed. Issue https://github.com/material-components/material-components-android/issues/1464
        // binding.editToothActionACTV.setText(event.getAction(),false);
        binding.editToothActionACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                event.setAction(list.get(position));
            }
        });

        binding.editGuaranteeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                event.setGuarantee(Math.round(value));
            }
        });
        setTextActionACTV();

//        createDirectory();

        binding.photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCameraPermissions();
            }
        });

        binding.galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryButtonClicked();
            }
        });
        createDirectory();
        createEventPhotosList();
        // refillPhotosUriList();

        return binding.getRoot();
    }

    private void galleryButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       // intent.setType("image/*");
       // intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setAction(Intent.ACTION_PICK);
       /* intent.setAction(Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        mGetGalleryContent.launch(intent);
    }

    private void refillPhotosUriList() {
        photosUri.clear();
        if (model.getPhotosUri(((MainActivity) getActivity()), event).size() > 0) {
            photosUri.addAll(model.getPhotosUri(((MainActivity) getActivity()), event));
            Log.d(TAG, "refillPhotosUriList() photosUri: " + photosUri);
        }
        eventPhotosListAdapter.notifyDataSetChanged();

    }

    private void createEventPhotosList() {

        recyclerView = binding.listEventPhotos;

        eventPhotosListAdapter = new EventPhotosListAdapter(this.getContext(), photosUri);

        eventPhotosListAdapter.setClickListener(new EventPhotosListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "photo uri: " + photosUri.get(position));

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse(photosUri.get(position)), "image/*");
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(eventPhotosListAdapter);

        //todo implement click listener
    }

    public void verifyCameraPermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.CAMERA);
        int permission2 = ActivityCompat.checkSelfPermission(binding.getRoot().getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Log.d(TAG, "permission CAMERA: " + permission);
        Log.d(TAG, "permission WRITE STORAGE: " + permission2);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.CAMERA,
            };
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    ((MainActivity) binding.getRoot().getContext()),
                    PERMISSIONS_STORAGE,
                    1
            );

        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
                String[] WRITE_PERMISSIONS_STORAGE = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                };
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        ((MainActivity) binding.getRoot().getContext()),
                        WRITE_PERMISSIONS_STORAGE,
                        1
                );

        } else {

            nonpublicUri = generateFileUri();
            mGetContent.launch(nonpublicUri);
        }
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
               // binding.getRoot().getContext().getFilesDir(),
                "TeethHistory");
        //directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!directory.exists())
            Log.d(TAG, "create directory");
            directory.mkdirs();
    }

    private Uri generateFileUri() {


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(directory.getAbsolutePath(), "IMG_" + timeStamp + ".jpg");

        //Uri imageUri = FileProvider.getUriForFile(this.getContext(), "com.appsverse.teethhistory.fileprovider", file);
        Uri imageUri = FileProvider.getUriForFile(this.getContext(), "com.appsverse.teethhistory.fileprovider", file);

        publicPhotoUri = Uri.parse(file.getAbsolutePath());
        Log.d(TAG, "publicUri: " + publicPhotoUri);
        Log.d(TAG, "nonpublicUri: " + imageUri);
        return imageUri;
    }

    private void setDatePicker(Event event) {

        binding.editEventDateTv.setOnClickListener(v -> {

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
        if (toothModel != null) {
            list.clear();
            if (event.getActions() != null) {
                list.addAll(event.getActions());

                binding.editToothActionACTV.setText(event.getAction(), false);
                event.setAction(list.get(0));
                //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void setEvent(EventModel event) {
        this.event.setId(event.getId());
        this.event.setDate(event.getDate());
        this.event.setAction(event.getAction());
        this.event.setGuarantee(event.getGuarantee());
        this.event.setNotes(event.getNotes());
        this.event.setActions(event.getActions());

        binding.editToothActionACTV.setText(event.getAction(), false);
        setTextActionACTV();

        refillPhotosUriList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        model.setId(event.getId());
        model.setPosition(event.getPosition());
        model.setDate(event.getDate());
        model.setAction(event.getAction());
        model.setGuarantee(event.getGuarantee());
        model.setNotes(event.getNotes());
        model.setActions(event.getActions());
    }
}
