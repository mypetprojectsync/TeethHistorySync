package com.appsverse.teethhistory.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.appsverse.teethhistory.databinding.FragmentNewEventBinding;
import com.appsverse.teethhistory.repository.EventModel;
import com.appsverse.teethhistory.repository.ToothModel;
import com.appsverse.teethhistory.viewModels.NewEventViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewEventFragment extends Fragment {

    final int MAX_WARRANTY = 360;

    NewEventViewModel model;
    public FragmentNewEventBinding binding;

    Event event;

    ArrayAdapter<String> adapter;
    String[] actions;

    File directory;
    Uri publicPhotoUri;

    RecyclerView recyclerView;
    public EventPhotosListAdapter eventPhotosListAdapter;
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

                            if (model.getPhotosListForDeleting() != null)
                                model.removeItemFromListToPhotosListToDeleting(uri);
                        }
                    }
                }
            }
    );

    ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

        final boolean[] permission = {true};

        result.forEach((k, v) -> {
            if (!v) permission[0] = false;
        });

        if (permission[0]) mGetContent.launch(generateFileUri());
    });

    ActivityResultLauncher<String[]> galleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> result.forEach((k, v) -> {
        if (v) galleryButtonClicked();
    }));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_event, container, false);

        actions = getResources().getStringArray(R.array.actions);

        model = new ViewModelProvider(this).get(NewEventViewModel.class);
        binding.setModel(model);

        event = new Event(model.getId(), model.getPosition(), model.getDate(), model.getAction(), model.getWarranty(), model.getNotes(), model.getPhotosUri());

        binding.setEvent(event);

        setDatePicker(event);

        adapter = new ArrayAdapter<>(this.getContext(), R.layout.dropdown_menu_popup_item, actions);
        binding.toothActionACTV.setAdapter(adapter);

        binding.toothActionACTV.setOnItemClickListener((parent, view, position, id) -> {

            event.setAction(position);

            if (position == EventModel.GROWN) {
                event.setWarranty(0);
            } else if (event.getWarranty() == 0) {
                event.setWarranty(12);
            }
        });

        binding.warrantySlider.addOnChangeListener((slider, value, fromUser) -> event.setWarranty(Math.round(value)));

        setTextActionACTV();

        setWarrantyTIET();

        binding.photoButton.setOnClickListener(v -> verifyCameraPermissions());

        binding.galleryButton.setOnClickListener(v -> {

            galleryPermissionLauncher.launch(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });

        });

        createDirectory();
        createEventPhotosList();

        refillPhotosUriList();

        return binding.getRoot();
    }

    private void refillPhotosUriList() {

        photosUri.clear();

        if (event.getPhotosUri() != null) {
            photosUri.addAll(event.getPhotosUri());
        }

        if (model.getPhotosListForDeleting() != null) {
            photosUri.removeAll(model.getPhotosListForDeleting());
        }

        eventPhotosListAdapter.notifyDataSetChanged();

    }

    private String getPathFromUri(Uri data) {

        Context context = getContext();
        Cursor cursor = context.getContentResolver().query(data, null, null, null, null);
        cursor.moveToFirst();
        String image_id = cursor.getString(0);
        image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);

        String path = "";
        if (cursor == null) {
            path = data.getPath();

        } else {
            cursor.moveToFirst();

            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //replacement for gallery miui bug
        return ((path == null || path.isEmpty()) ? (data.getPath().replace("/raw/", "")) : path);
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

                if (tracker.hasSelection() && actionMode == null) {
                    actionMode = ((MainActivity) getActivity()).startSupportActionMode(actionModeCallback);

                    actionMode.setTitle(String.valueOf(tracker.getSelection().size()));

                } else if (!tracker.hasSelection() && actionMode != null) {

                    actionMode.finish();
                    actionMode = null;

                } else {
                    if (actionMode != null)
                        actionMode.setTitle(String.valueOf(tracker.getSelection().size()));
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

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
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

            List<String> listForRemove = new ArrayList<>();

            for (int i : getSelectedItemsIndexList()) {
                listForRemove.add(photosUri.get(i));
            }

            if (model.getPhotosListForDeleting() == null) {
                model.setPhotosListForDeleting(listForRemove);
            } else {
                model.addListToPhotosListToDeleting(listForRemove);
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

        for (long selectedId : tracker.getSelection()) {
            selectedIdList.add((int) selectedId);
        }
        return selectedIdList;
    }

    private void galleryButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mGetGalleryContent.launch(intent);
    }

    private Uri generateFileUri() {

        MainActivity mainActivity = (MainActivity) getActivity();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File file = new File(directory.getAbsolutePath(),
                mainActivity.binding.getModel().getUsername() + "_"
                        + mainActivity.binding.getModel().getChosenToothID() + "_"
                        + event.getId() + "_"
                        + timeStamp + ".jpg");

        Uri imageUri = FileProvider.getUriForFile(this.getContext(), "com.appsverse.teethhistory.fileprovider", file);

        publicPhotoUri = Uri.parse(file.getAbsolutePath());
        return imageUri;
    }

    private void verifyCameraPermissions() {

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
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
            picker.addOnPositiveButtonClickListener(selection -> event.setDate(new Date((Long) selection)));
        });
    }

    public void setDefaultTextActionACTV() {

        ToothModel toothModel = model.getToothModel((MainActivity) getActivity());

        if (toothModel != null) {

            binding.toothActionACTV.setText(actions[0], false);
            binding.getEvent().setAction(0);

            adapter.notifyDataSetChanged();
        }
    }

    public void setTextActionACTV() {

        ToothModel toothModel = model.getToothModel((MainActivity) getActivity());

        if (toothModel != null) {

            if (event.getAction() == 0) {
                binding.toothActionACTV.setText(actions[0], false);
                event.setAction(0);
            } else {
                binding.toothActionACTV.setText(getResources().getStringArray(R.array.actions)[event.getAction()], false);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void setWarrantyTIET() {

        binding.warrantyTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().equals("0")) setSelectionWithDelay(1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    binding.warrantyTIET.setText("0");
                    setSelectionWithDelay(1);

                } else if (Integer.parseInt(s.toString()) > MAX_WARRANTY) {
                    event.setWarranty(MAX_WARRANTY);
                    setSelectionWithDelay(3);
                }
            }
        });
    }

    private void setSelectionWithDelay(int i) {
        Handler handler = new Handler();
        handler.postDelayed(() -> binding.warrantyTIET.setSelection(i), 10);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        model.setPosition(event.getPosition());
        model.setDate(event.getDate());
        model.setAction(event.getAction());
        model.setWarranty(event.getWarranty());
        model.setNotes(event.getNotes());
        model.setPhotosUri(event.getPhotosUri());
    }
}

//todo change radiobutton colors

//TODO implement working with external files in sdk ver. 30