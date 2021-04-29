package com.appsverse.teethhistory.adapters;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.adapters.EventPhotosListAdapter;

public class PhotoItemDetailsLookup extends ItemDetailsLookup {

    RecyclerView recyclerView;

    public PhotoItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }


    @Nullable
    @Override
    public ItemDetails getItemDetails(@NonNull MotionEvent e) {

        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

        if (view != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof EventPhotosListAdapter.ViewHolder) {
                return ((EventPhotosListAdapter.ViewHolder) holder).getItemDetails();
            }
        }

        return null;
    }
}
