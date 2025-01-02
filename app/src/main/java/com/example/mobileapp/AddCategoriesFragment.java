package com.example.mobileapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddCategoriesFragment extends DialogFragment {
    EditText etCategoryName;
    ImageView tvCategoryIcon;  // Sử dụng TextView để hiển thị icon đã chọn

    CategoryDatabaseHandler categoryDb;
    List<Integer> iconList; // Danh sách ID các icon
    IconAdapter iconAdapter;

    OnCategoryAddedListener listener;

    public static AddCategoriesFragment newInstance(OnCategoryAddedListener listener) {
        AddCategoriesFragment fragment = new AddCategoriesFragment();
        fragment.listener = listener;
        return fragment;
    }

    public interface OnCategoryAddedListener {
        void onCategoryAdded(CategoriesItem category);
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_addcategories, null);

        etCategoryName = dialogView.findViewById(R.id.tv_addcategories_addcategories_textbox);
        tvCategoryIcon = dialogView.findViewById(R.id.tv_addcategories_selected_icon); // TextView để hiển thị icon

        categoryDb = new CategoryDatabaseHandler(requireContext());

        // Tạo danh sách icon
        iconList = new ArrayList<>();
        iconList.add(R.drawable.icon_user);
        iconList.add(R.drawable.icon_work);
        iconList.add(R.drawable.icon_workflow);
        iconList.add(R.drawable.icon_health);
        iconList.add(R.drawable.icon_shopping);
        iconList.add(R.drawable.icon_cooking);
        iconList.add(R.drawable.icon_train);
        iconList.add(R.drawable.icon_music);
        iconList.add(R.drawable.icon_education);
        iconList.add(R.drawable.icon_gym);
        iconList.add(R.drawable.icon_running);
        iconList.add(R.drawable.icon_finance);
        iconList.add(R.drawable.icon_hobby);
        iconList.add(R.drawable.icon_meeting);
        iconList.add(R.drawable.icon_birthday);
        iconList.add(R.drawable.icon_relax);
        iconList.add(R.drawable.icon_yoga);
        iconList.add(R.drawable.icon_reading);
        iconList.add(R.drawable.icon_writing);
        iconList.add(R.drawable.icon_gardening);
        iconList.add(R.drawable.icon_housework);
        iconList.add(R.drawable.icon_photography);
        iconList.add(R.drawable.icon_socialmedia);
        iconList.add(R.drawable.icon_technology);
        iconList.add(R.drawable.icon_fashion);
        iconList.add(R.drawable.icon_beauty);
        iconList.add(R.drawable.icon_home2);
        iconList.add(R.drawable.icon_volunteer);
        iconList.add(R.drawable.icon_schedule);
        iconList.add(R.drawable.icon_relationship);
        iconList.add(R.drawable.icon_food_delivery);
        iconList.add(R.drawable.icon_watchtv);
        iconList.add(R.drawable.icon_laptop);
        iconList.add(R.drawable.icon_contact);
        iconList.add(R.drawable.icon_goal);
        iconList.add(R.drawable.icon_eating);


        iconAdapter = new IconAdapter(requireContext(), iconList);

        // Khi nhấn vào TextView, hiển thị dialog chọn icon
        tvCategoryIcon.setOnClickListener(v -> showIconSelectionDialog());

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    String categoryName = etCategoryName.getText().toString();
                    int selectedIcon = iconList.get(iconAdapter.getSelectedPosition()); // Lấy icon đã chọn từ Adapter

                    if (categoryName.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CategoriesItem newCategory = new CategoriesItem();
                    newCategory.setName(categoryName);
                    newCategory.setIconResId(selectedIcon);

                    long categoryId = categoryDb.addCategory(newCategory);
                    newCategory.setCategoryId((int) categoryId);

                    if (listener != null) {
                        listener.onCategoryAdded(newCategory);
                    }

                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }

    private void showIconSelectionDialog() {
        // Tạo một AlertDialog để chứa GridView
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Icon");

        // Tạo GridView để hiển thị các icon
        GridView gridView = new GridView(requireContext());
        gridView.setNumColumns(4);  // Số cột của GridView
        gridView.setAdapter(iconAdapter);  // Gán adapter cho GridView

        // Tạo MarginLayoutParams cho GridView và thêm margin
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (10 * getResources().getDisplayMetrics().density);  // 10dp chuyển sang px
        params.setMargins(margin, margin, margin, margin);  // Đặt margin top và margin horizontal
        gridView.setLayoutParams(params);

        // Khi người dùng chọn icon, cập nhật trực tiếp TextView (hoặc ImageView) với icon đã chọn
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy icon đã chọn và cập nhật vào TextView (hoặc ImageView)
            tvCategoryIcon.setBackgroundResource(iconList.get(position)); // Cập nhật background cho TextView
            iconAdapter.setSelectedPosition(position); // Đánh dấu vị trí được chọn trong adapter
        });

        builder.setView(gridView); // Gán GridView vào Dialog

        // Thêm nút "OK" để đóng dialog
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Nút OK đã nhấn, chỉ cần đóng dialog
            dialog.dismiss(); // Đóng dialog khi nhấn OK
        });

        // Hiển thị dialog
        builder.create().show();
    }
}