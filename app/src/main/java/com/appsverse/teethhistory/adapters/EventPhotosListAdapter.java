package com.appsverse.teethhistory.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventPhotosListAdapter extends RecyclerView.Adapter<EventPhotosListAdapter.ViewHolder> {

    private final List<String> photosUri;
    private ItemClickListener itemClickListener;

    SelectionTracker tracker;

    public EventPhotosListAdapter(List<String> photosUri) {
        this.photosUri = photosUri;
    }

    public void setSelectionTracker(SelectionTracker tracker) {
        this.tracker = tracker;
    }

    @NonNull
    @Override
    public EventPhotosListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPhotosListAdapter.ViewHolder holder, int position) {

        Picasso.get().load("file://" + photosUri.get(position)).resize(200, 200).into(holder.photoImageButton);

        if (tracker != null)
            holder.bind(tracker.isSelected((long) position));

    }

    @Override
    public int getItemCount() {
        return photosUri.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ViewHolderWithDetails, View.OnClickListener {

        ImageButton photoImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageButton = itemView.findViewById(R.id.image_button_photo);
            photoImageButton.setOnClickListener(this);
        }

        public final void bind(boolean isActive) {
            itemView.setActivated(isActive);
        }

        @Override
        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new PhotoItemDetail(getBindingAdapterPosition(), getBindingAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(v, getBindingAdapterPosition());
        }

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
