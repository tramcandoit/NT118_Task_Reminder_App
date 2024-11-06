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

public class TasksArrayAdapter extends ArrayAdapter<Task> {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public TasksArrayAdapter(Context context, List<Task> tasksList) {
        super(context, 0, tasksList);
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.drawable.home_item_tasks, parent, false);
        }

        TextView tvItemName = convertView.findViewById(R.id.tv_item_name);
        TextView tvItemTime = convertView.findViewById(R.id.tv_item_time);

        Task task = getItem(position);

        if (task != null) {
            tvItemName.setText(task.getName());
            if (task.getTime() != null) {
                tvItemTime.setText(timeFormat.format(task.getTime()));
            } else {
                tvItemTime.setText(""); // or "No time set" if preferred
            }
        }

        return convertView;
    }
}
