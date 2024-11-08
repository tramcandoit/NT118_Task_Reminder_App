package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventsArrayAdapter extends ArrayAdapter<Event> {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public EventsArrayAdapter(Context context, List<Event> eventsList) {
        super(context, 0, eventsList);
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.drawable.home_item_events, parent, false);
        }

        TextView tvItemName = convertView.findViewById(R.id.tv_item_name);
        TextView tvItemTime = convertView.findViewById(R.id.tv_item_date);

        Event event = getItem(position);

        if (event != null) {
            tvItemName.setText(event.getName());
            if (event.getDate() != null) {
                tvItemTime.setText((event.getDate()));
            } else {
                tvItemTime.setText(""); // or "No time set" if preferred
            }
        }

        return convertView;
    }
}
