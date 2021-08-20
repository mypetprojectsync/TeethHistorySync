package com.appsverse.teethhistory.adapters;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.EventsListAdapter;
import com.appsverse.teethhistory.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class EventPhotosListAdapter extends RecyclerView.Adapter<EventPhotosListAdapter.ViewHolder> {

    private final List<String> photosUri;
    //private final LayoutInflater inflater;

    private EventPhotosListAdapter.ItemClickListener itemClickListener;
    private EventPhotosListAdapter.ItemLongClickListener itemLongClickListener;

    SelectionTracker tracker;

    public EventPhotosListAdapter(List<String> photosUri) {
        this.photosUri = photosUri;
       // this.inflater = LayoutInflater.from(context);
    }

    public SelectionTracker getSelectionTracker() {
        return tracker;
    }

    public void setSelectionTracker(SelectionTracker tracker) {
        this.tracker = tracker;
    }

    @NonNull
    @Override
    public EventPhotosListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = inflater.inflate(R.layout.item_event_photo, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPhotosListAdapter.ViewHolder holder, int position) {
        Picasso.get().load("file://" + photosUri.get(position)).resize(200, 200).into(holder.photoImageButton);

        /*if (holder.itemView.isSelected()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }*/

       if (tracker != null)  holder.bind(photosUri.get(position), tracker.isSelected((long) position));

        /*Log.d("myLogs", "holder.itemView.isActivated(): " + holder.itemView.isActivated() + "\nholder.itemView.isSelected(): " + holder.itemView.isSelected());
        Log.d("myLogs", "tracker.isSelected(position): " + tracker.isSelected(position) + ", position: " + position);*/
    }

    @Override
    public int getItemCount() {
        return photosUri.size();
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    //public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, ViewHolderWithDetails {
    public class ViewHolder extends RecyclerView.ViewHolder implements ViewHolderWithDetails {

        ImageButton photoImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageButton = itemView.findViewById(R.id.image_button_photo);
            //photoImageButton.setOnClickListener(this);
           // photoImageButton.setOnLongClickListener(this);
   }

   public final void bind(String item, boolean isActive) {
       Log.d("myLogs", "bind isActive: " + isActive);
            itemView.setActivated(isActive);
      // Picasso.get().load("file://" + item).resize(200, 200).into(photoImageButton);
        }

        @Override
        public ItemDetailsLookup.ItemDetails getItemDetails(){
            Log.d("myLogs", "ItemDetailsLookup.ItemDetails getItemDetails()");
            Log.d("myLogs", "BindingAdapterPosition(): " + getBindingAdapterPosition());
            return new PhotoItemDetail(getBindingAdapterPosition(), (long) getBindingAdapterPosition() );
        }

   /*public final void bind(Item item)*/

       /* public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return new ItemDetailsLookup.ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    Log.d("myLogs", "ItemDetailsLookup.ItemDetails getPosition(): " + getBindingAdapterPosition());
                    return getBindingAdapterPosition();
                }

                @Nullable
                @Override
                public Long getSelectionKey() {
                    //return (long) getBindingAdapterPosition();
                    Log.d("myLogs", "ItemDetailsLookup.ItemDetails getSelectionKey(): " + (long) getBindingAdapterPosition());
                    return (long) getBindingAdapterPosition();
                }
            };
        }*/

        /*@Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getBindingAdapterPosition());
        }*/

/*        @Override
        public boolean onLongClick(View v) {
            itemLongClickListener.onItemLongClick(v, getBindingAdapterPosition());
            return true;
        }*/


    }

    /*public void setClickListener(EventPhotosListAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }*/

    public  void setLongClickListener(EventPhotosListAdapter.ItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

   public interface ItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

}
