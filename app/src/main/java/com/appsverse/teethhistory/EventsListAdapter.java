package com.appsverse.teethhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.TimeUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.appsverse.teethhistory.repository.EventModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    private final List<EventModel> eventModels;
    private final LayoutInflater inflater;

    private ItemClickListener itemClickListener;

    public EventsListAdapter(Context context, List<EventModel> eventModels) {
        this.eventModels = eventModels;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EventModel event = eventModels.get(position);
            //todo format to dd.MM.yyyy

            Date guarantyLastDate = getGuarantyLastDate(event);
            long guarantyDaysLeft = TimeUnit.DAYS.convert(guarantyLastDate.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);

            if (guarantyDaysLeft>0) {
                holder.dateTV.setText(new SimpleDateFormat("dd.MM.yyyy").format(event.getDate()) + "\n" + guarantyDaysLeft + " days of guarantee left");
            }else {
                holder.dateTV.setText(new SimpleDateFormat("dd.MM.yyyy").format(event.getDate()));
            }

        holder.actionTV.setText(event.getAction());
    }

    private Date getGuarantyLastDate(EventModel event) {
        Date referenceDate = event.getDate();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, event.getGuarantee());
        return c.getTime();
    }

    @Override
    public int getItemCount() {
         return eventModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView dateTV, actionTV, optionsMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.item_event_date);
            actionTV = itemView.findViewById(R.id.item_event_action);
            optionsMenu = itemView.findViewById(R.id.itemEventOptions);
            optionsMenu.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
