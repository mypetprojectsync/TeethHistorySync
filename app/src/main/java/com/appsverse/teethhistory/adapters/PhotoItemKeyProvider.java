package com.appsverse.teethhistory.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.List;

//todo delete class after successful deleting recyclerview items implementing
public class PhotoItemKeyProvider  extends ItemKeyProvider {

    List<String> photosUri;

    public PhotoItemKeyProvider(int scope, List<String> photosUri) {
        super(scope);
        this.photosUri = photosUri;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        //return photosUri.get(position);
        Log.d("myLogs", "PhotoItemKeyProvider getKey");
        return (long) position;
    }

    @Override
    public int getPosition(@NonNull Object key) {
        Log.d("myLogs", "PhotoItemKeyProvider getPosition");
        return photosUri.indexOf(key);
    }
}
