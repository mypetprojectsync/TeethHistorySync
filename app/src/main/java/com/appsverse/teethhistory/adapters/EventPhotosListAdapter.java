package com.appsverse.teethhistory.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.EventsListAdapter;
import com.appsverse.teethhistory.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class EventPhotosListAdapter extends RecyclerView.Adapter<EventPhotosListAdapter.ViewHolder> {

    private final List<String> photosUri;
    private final LayoutInflater inflater;

    private EventPhotosListAdapter.ItemClickListener itemClickListener;

    public EventPhotosListAdapter(Context context, List<String> photosUri) {
        this.photosUri = photosUri;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventPhotosListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_event_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPhotosListAdapter.ViewHolder holder, int position) {
        Picasso.get().load("file://"+photosUri.get(position)).resize(200,200).into(holder.photoImageButton);
    }

    @Override
    public int getItemCount() {
        return photosUri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageButton photoImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageButton = itemView.findViewById(R.id.image_button_photo);
            photoImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
 itemClickListener.onItemClick(v,getAdapterPosition());
        }
    }
    public void setClickListener(EventPhotosListAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
