package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class CategoriesMenuFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private GridView gridView;
    private CategoriesGridViewAdapter categoriesAdapter;
    private List<CategoriesItem> categories;
    private CategoryDatabaseHandler categoryDb;
    private LinearLayout tvHome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories_menu, container, false);

        tvHome = view.findViewById(R.id.tv_categoriesmenu_home);
        gridView = view.findViewById(R.id.gv_categoriesmenu_list);

        categoryDb = new CategoryDatabaseHandler(requireContext());
        categories = categoryDb.getAllCategories();
        categoriesAdapter = new CategoriesGridViewAdapter(requireContext(), categories);
        gridView.setAdapter(categoriesAdapter);

        // OnItemClickListener for GridView
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            final CategoriesItem selectedCategory = categories.get(position);

            // Tạo và hiển thị dialog
            View dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.fragment_categoriesdetail, null);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
            builder.setView(dialogView);

            // Liên kết các view trong dialog
            TextView tvCategoryName = dialogView.findViewById(R.id.tv_categoriesdetail_categoriesdetail_textbox);
            ImageView ivCategoryIcon = dialogView.findViewById(R.id.tv_categoriesdetail_categories);

            // Gán dữ liệu từ item vào dialog
            tvCategoryName.setText(selectedCategory.getName());
            ivCategoryIcon.setImageResource(selectedCategory.getIconResId());

            // Thêm nút OK để đóng dialog
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        // OnItemLongClickListener for GridView
        gridView.setOnItemLongClickListener((parent, view1, position, id) -> {
            CategoriesItem selectedCategory = categories.get(position);

            // Tạo dialog Yes/No
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
            builder.setTitle("Xóa danh mục");
            builder.setMessage("Bạn có chắc chắn muốn xóa danh mục \"" + selectedCategory.getName() + "\" không?");

            // Nút Yes
            builder.setPositiveButton("Có", (dialog, which) -> {
                // Xóa danh mục từ cơ sở dữ liệu
                categoryDb.deleteCategory(selectedCategory);
                // Xóa danh mục từ danh sách và cập nhật GridView
                categories.remove(position);
                categoriesAdapter.notifyDataSetChanged();
            });

            // Nút No
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

            builder.create().show();
            return true;
        });

        // Xử lý sự kiện nhấn vào tvHome
        tvHome.setOnClickListener(v -> {
            HomeFragment homeFragment = new HomeFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .addToBackStack(null)
                    .commit();

            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.bottomNavigationView.setSelectedItemId(R.id.nav_home);
            mainActivity.btn_add.setVisibility(View.VISIBLE); // Hiển thị nút "+"
        });

        return view;
    }

    public void onCategoryAdded(CategoriesItem category) {
        if (categoriesAdapter != null) {
            categories.add(category);
            categoriesAdapter.notifyDataSetChanged(); // Cập nhật GridView
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(gridView); // Đăng ký context menu cho GridView
    }
}
