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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.MainActivity;
import com.appsverse.teethhistory.R;
import com.appsverse.teethhistory.adapters.EventPhotosListAdapter;
import com.appsverse.teethhistory.adapters.PhotoItemDetailsLookup;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditEventFragment extends Fragment {

    EditEventViewModel model;
    FragmentEditEventBinding binding;
    final String TAG = "myLogs";

    Event event;
    ArrayAdapter adapter;
    List<String> actions = new ArrayList<>();

    File directory;

    Uri publicPhotoUri;

    RecyclerView recyclerView;
    EventPhotosListAdapter eventPhotosListAdapter;
    List<String> photosUri = new ArrayList<>();
    private ActionMode actionMode;
    SelectionTracker<Long> tracker;

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

                        String uri = getPathFromUri(result.getData().getData());

                        if (photosUri.contains(uri)) {
                            Toast.makeText(getActivity().getBaseContext(), R.string.photo_already_added, Toast.LENGTH_SHORT).show();
                        } else {
                            photosUri.add(uri);
                            event.setPhotosUri(photosUri);
                            eventPhotosListAdapter.notifyDataSetChanged();

                            if (model.getPhotosListForDeleting() != null) model.removeItemFromListToPhotosListToDeleting(uri);
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {

            final boolean[] permission = {true};

            result.forEach((k, v) -> {
                if (!v) permission[0] = false;
            });

            if (permission[0]) mGetContent.launch(generateFileUri());
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_event, container, false);

        model = new ViewModelProvider(this).get(EditEventViewModel.class);
        binding.setModel(model);

        event = new Event(model.getId(), model.getPosition(), model.getDate(), model.getAction(), model.getGuarantee(), model.getNotes(), model.getActions(), model.getPhotosUri());
        binding.setEvent(event);

        setDatePicker(event);


        //todo add to DataBindingAdapters chosenValue"@={event.action} https://stackoverflow.com/questions/58737505/autocompletetextview-or-spinner-data-binding-in-android
        adapter = new ArrayAdapter<>(this.getContext(), R.layout.dropdown_menu_popup_item, actions);

        binding.editToothActionACTV.setAdapter(adapter);

        //todo list lost when chosen some item and orientation changed. Issue
        // binding.editToothActionACTV.setText(event.getAction(),false);
        binding.editToothActionACTV.setOnItemClickListener((parent, view, position, id) -> event.setAction(actions.get(position)));

        binding.editGuaranteeSlider.addOnChangeListener((slider, value, fromUser) -> event.setGuarantee(Math.round(value)));
        setTextActionACTV();

        binding.photoButton.setOnClickListener(v -> verifyCameraPermissions());

        binding.galleryButton.setOnClickListener(v -> galleryButtonClicked());

        createDirectory();
        createEventPhotosList();

        refillPhotosUriList();

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

    private void galleryButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetGalleryContent.launch(intent);
    }

    private void refillPhotosUriList() {
        Log.d(TAG, "refillPhotosUriList()");

        photosUri.clear();

        if (event.getPhotosUri() != null) {
            photosUri.addAll(event.getPhotosUri());
        }

        if (model.getPhotosListForDeleting() != null) {
            photosUri.removeAll(model.getPhotosListForDeleting());
        }

        clearPhotosUriFromDeletedItems();

        eventPhotosListAdapter.notifyDataSetChanged();
    }

    private void createEventPhotosList() {

        recyclerView = binding.listEventPhotos;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        eventPhotosListAdapter = new EventPhotosListAdapter(photosUri);
        eventPhotosListAdapter.setHasStableIds(true);

        recyclerView.setAdapter(eventPhotosListAdapter);

        tracker = new SelectionTracker.Builder<Long>(
                "photosSelection",
                recyclerView,
                new StableIdKeyProvider(recyclerView),
                new PhotoItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()
        ).build();

        tracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onItemStateChanged(@NonNull Long key, boolean selected) {
                super.onItemStateChanged(key, selected);
            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();

                Log.d(TAG, "onSelectionChanged() started");

                if (tracker.hasSelection() && actionMode == null) {
                    actionMode = ((MainActivity) getActivity()).startSupportActionMode(actionModeCallback);

                    actionMode.setTitle(String.valueOf(tracker.getSelection().size()));

                } else if (!tracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                } else {
                    if (actionMode != null) actionMode.setTitle(String.valueOf(tracker.getSelection().size()));
                }
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });
        eventPhotosListAdapter.setSelectionTracker(tracker);

        eventPhotosListAdapter.setClickListener((view, position) -> {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse(photosUri.get(position)), "image/*");
            startActivity(intent);
        });
    }

    private void clearPhotosUriFromDeletedItems() {

        List<String> tempList = new ArrayList<>();

        for (String uri : photosUri) {
            File file = new File(uri);
            if (file.exists()) {
                tempList.add(uri);
            }
        }

        photosUri.clear();
        photosUri.addAll(tempList);

    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_bar_photo_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d(TAG, "Action menu clicked: " + item.getTitle());

                List<String> listForRemove = new ArrayList<>();

                for (int i : getSelectedItemsIndexList()) {
                    listForRemove.add(photosUri.get(i));
                }

                if (model.getPhotosListForDeleting() == null) {
                    model.setPhotosListForDeleting(listForRemove);
                    Log.d(TAG, "model.setPhotosListForDeleting: " + model.getPhotosListForDeleting());
                } else {
                    model.addListToPhotosListToDeleting(listForRemove);
                    Log.d(TAG, "model.addListToPhotosListForDeleting: " + model.getPhotosListForDeleting());
                }
                photosUri.removeAll(listForRemove);

            actionMode.finish();

            eventPhotosListAdapter.notifyDataSetChanged();

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            tracker.clearSelection();
        }
    };

    private List<Integer> getSelectedItemsIndexList() {
        List<Integer> selectedIdList = new ArrayList<>();
        Iterator<Long> iterator = tracker.getSelection().iterator();

        while (iterator.hasNext()) {
            long selectedId = iterator.next();
            selectedIdList.add((int) selectedId);
        }
        return selectedIdList;
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "TeethHistory");

        if (!directory.exists()) directory.mkdirs();
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
            actions.clear();
            if (event.getActions() != null) {
                actions.addAll(event.getActions());

                binding.editToothActionACTV.setText(event.getAction(), false);
                event.setAction(actions.get(0));
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
        this.event.setPhotosUri(event.getPhotosUri());


        binding.editToothActionACTV.setText(event.getAction(), false);
        setTextActionACTV();

        model.clearPhotosListToDeleting();

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
        model.setPhotosUri(event.getPhotosUri());

    }
}
