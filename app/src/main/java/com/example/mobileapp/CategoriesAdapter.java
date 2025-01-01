package com.example.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final List<CategoriesItem> categories;
    private final Context context;
    private HashMap<String, Integer> categoryPositions = new HashMap<>();

    public CategoriesAdapter(Context context, List<CategoriesItem> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.drawable.home_item_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoriesItem category = categories.get(position);
        categoryPositions.put(category.getName(), position);
        holder.imageView.setImageResource(category.getIconResId());
        holder.textView.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_home_categories);
            textView = itemView.findViewById(R.id.tv_home_categories);
        }
    }
    public int getItemPositionByName(String categoryName) {
        return categoryPositions.getOrDefault(categoryName, -1); // Trả về -1 nếu không tìm thấy
    }

}