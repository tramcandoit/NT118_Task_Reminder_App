package com.example.mobileapp;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final List<CategoriesItem> categories;
    private final Context context;

    private SparseBooleanArray selectedCategories;
    private OnItemClickListener onItemClickListener;

    private HashMap<String, Integer> categoryPositions = new HashMap<>();

    public CategoriesAdapter(Context context, List<CategoriesItem> categories, SparseBooleanArray selectedCategories) {
        this.context = context;
        this.categories = categories;
        this.selectedCategories = selectedCategories; // Gán giá trị từ tham số truyền vào
    }

    public interface OnItemClickListener { // Định nghĩa interface
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) { // Hàm setter cho listener
        this.onItemClickListener = listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.drawable.home_item_categories, parent, false); // Use R.layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoriesItem category = categories.get(position);
        categoryPositions.put(category.getName(), position);
        holder.imageView.setImageResource(category.getIconResId());
        holder.textView.setText(category.getName());

        // Set background based on selected state
        if (selectedCategories.get(categories.get(position).getCategoryId(), false)) {
            holder.itemView.setBackgroundResource(R.drawable.background_categories_item_selected); //selected background
        } else {
            holder.itemView.setBackgroundResource(R.drawable.background_categories_item);  // unselected background
        }


        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
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