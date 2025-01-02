package com.example.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

public class IconAdapter extends ArrayAdapter<Integer> {
    private int selectedPosition = -1;  // Biến để lưu vị trí đã chọn

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

        // Nếu item này được chọn, tô màu nền của item
        if (position == selectedPosition) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_blue));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_blue_2));
        }

        return convertView;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged(); // Cập nhật lại giao diện khi có sự thay đổi
    }
}
