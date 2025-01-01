package com.example.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CategoriesGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<CategoriesItem> categoriesList;

    public CategoriesGridViewAdapter(Context context, List<CategoriesItem> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.drawable.home_item_categories, parent, false);
        }

        ImageView categoryIcon = convertView.findViewById(R.id.img_home_categories);
        TextView categoryName = convertView.findViewById(R.id.tv_home_categories);

        CategoriesItem category = categoriesList.get(position);

        categoryIcon.setImageResource(category.getIconResId());
        categoryName.setText(category.getName());

        return convertView;
    }
}