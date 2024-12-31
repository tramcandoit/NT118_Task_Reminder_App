package com.example.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class IconAdapter extends ArrayAdapter<Integer> {

    public IconAdapter(Context context, List<Integer> iconList) {
        super(context, 0, iconList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_icon_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.icon_image);
        Integer iconResId = getItem(position);

        if (iconResId != null) {
            imageView.setImageResource(iconResId);
        }

        return convertView;
    }
}