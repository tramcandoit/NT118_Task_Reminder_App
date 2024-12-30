package com.example.mobileapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ThemeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    LinearLayout tvSetting;
    private BottomNavigationView bottomNavigationView;

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
        View view = inflater.inflate(R.layout.fragment_theme, container, false);

        tvSetting = view.findViewById(R.id.tv_settings);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFragment settingsFragment = new SettingsFragment();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, settingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        RadioGroup rgTheme = view.findViewById(R.id.rg_theme);
        RadioButton rbLight = view.findViewById(R.id.rb_light);
        RadioButton rbDark = view.findViewById(R.id.rb_dark);

        // Lấy chế độ đã lưu trong SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        int currentMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) {
            rbLight.setChecked(true);
        } else {
            rbDark.setChecked(true);
        }

        rgTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (checkedId == R.id.rb_light) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.rb_dark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_YES);
                }
                editor.apply();
            }
        });

        return view;
    }


}
