package com.example.mobileapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
    Spinner spCategoryIcon;

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
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_addcategories, null);

        etCategoryName = dialogView.findViewById(R.id.tv_addcategories_addcategories_textbox);
        spCategoryIcon = dialogView.findViewById(R.id.sp_addcategories_categories);

        categoryDb = new CategoryDatabaseHandler(requireContext());

        // Tạo danh sách icon (thay thế bằng danh sách icon của bạn)
        iconList = new ArrayList<>();
        iconList.add(R.drawable.icon_user); // Thay bằng các drawable icon của bạn.
        iconList.add(R.drawable.icon_user);
        iconList.add(R.drawable.icon_user);
        iconList.add(R.drawable.icon_user); // Thay bằng các drawable icon của bạn.
        iconList.add(R.drawable.icon_user);
        iconList.add(R.drawable.icon_user);// Thay bằng các drawable icon của bạn.
        iconList.add(R.drawable.icon_user);
        iconList.add(R.drawable.icon_user);
        iconList.add(R.drawable.icon_user); // Thay bằng các drawable icon của bạn.
        iconList.add(R.drawable.icon_user);
        iconList.add(R.drawable.icon_user);

        // ... thêm các icon khác

        iconAdapter = new IconAdapter(requireContext(), iconList);
        spCategoryIcon.setAdapter(iconAdapter);

        spCategoryIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy icon đã chọn (nếu cần)
                int selectedIcon = iconList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    String categoryName = etCategoryName.getText().toString();
                    int selectedIcon = iconList.get(spCategoryIcon.getSelectedItemPosition());

                    if (categoryName.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a category name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CategoriesItem newCategory = new CategoriesItem();
                    newCategory.setName(categoryName);
                    newCategory.setIconResId(selectedIcon); // Sửa lỗi tại đây

                    long categoryId = categoryDb.addCategory(newCategory);
                    newCategory.setCategoryId((int) categoryId);

                    if (listener != null) {
                        listener.onCategoryAdded(newCategory);
                    }

                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    AddCategoriesFragment.this.getDialog().cancel();
                });

        return builder.create();
    }
}