package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class CategoriesMenuFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private GridView gridView; // Sử dụng lại GridView
    private CategoriesGridViewAdapter categoriesAdapter; // Adapter mới
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
        gridView = view.findViewById(R.id.gv_categoriesmenu_list); // ID của GridView trong layout


        categoryDb = new CategoryDatabaseHandler(requireContext());

        categories = categoryDb.getAllCategories();
        categoriesAdapter = new CategoriesGridViewAdapter(requireContext(), categories);
        gridView.setAdapter(categoriesAdapter);


        tvHome.setOnClickListener(v -> {
            // Tạo và hiển thị HomeFragment
            HomeFragment homeFragment = new HomeFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .addToBackStack(null) // Thêm dòng này
                    .commit();

            // Lấy MainActivity và cập nhật BottomNavigationView
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
}