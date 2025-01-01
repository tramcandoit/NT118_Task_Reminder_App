package com.example.mobileapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TasksArrayAdapter extends ArrayAdapter<Task> {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private final CategoryDatabaseHandler categoryDb; // Add this

    public TasksArrayAdapter(Context context, List<Task> tasksList, CategoryDatabaseHandler categoryDb) { // Modified constructor
        super(context, 0, tasksList);
        this.categoryDb = categoryDb; // Initialize
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.drawable.home_item_tasks, parent, false); // Correct layout file
        }

        TextView tvItemName = convertView.findViewById(R.id.tv_item_name);
        tvItemName.setSelected(true);
        TextView tvItemTime = convertView.findViewById(R.id.tv_item_time);
        TextView tvCategoryName = convertView.findViewById(R.id.tv_item_category_name);  // Category name
        ImageView imgCategoryIcon = convertView.findViewById(R.id.img_item_category_image); // Category image

        Task task = getItem(position);



        if (task != null) {
            tvItemName.setText(task.getName());
            tvItemTime.setText(task.getTime() != null ? task.getTime() : "");

            // Get Category info
            CategoriesItem category = categoryDb.getCategory(task.getCategoryId());
            if (category != null) {
                tvCategoryName.setText(category.getName());
                imgCategoryIcon.setImageResource(category.getIconResId()); // Set Image
            } else {
                // Handle case where category is not found (e.g., set default values)
                tvCategoryName.setText("Unknown Category");
                imgCategoryIcon.setImageResource(R.drawable.icon_user); // Default image

            }
        }


        return convertView;
    }
}