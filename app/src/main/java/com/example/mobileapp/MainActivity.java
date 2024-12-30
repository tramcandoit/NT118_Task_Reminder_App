package com.example.mobileapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; // Dùng để điều hướng giữa các fragment
    private FloatingActionButton btn_add; // Nút floating action button

    // Biến để lưu trạng thái hiển thị của FAB (VISIBLE hoặc GONE)
    private static final String FAB_VISIBILITY_STATE = "fab_visibility_state";
    private int savedVisibility = View.VISIBLE; // Mặc định là hiển thị


    // Hàm để thêm task mới (sử dụng trong HomeFragment)
    private void addTask() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (homeFragment != null) {
            AddTaskFragment addTaskFragment = AddTaskFragment.newInstance(homeFragment);
            addTaskFragment.show(getSupportFragmentManager(), "addTaskFragment");
        } else {
            // Ghi log lỗi nếu không tìm thấy HomeFragment
            Log.e("MainActivity", "HomeFragment not found!");
        }
    }

    // Hàm để thêm sự kiện mới (sử dụng trong CalendarFragment)
    private void addEvent() {
        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (calendarFragment != null) {
            AddEventFragment addEventFragment = AddEventFragment.newInstance(calendarFragment);
            addEventFragment.show(getSupportFragmentManager(), "addEventFragment");
        }
    }

    // Tạo kênh thông báo cho các sự kiện (cần thiết cho Android Oreo trở lên)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Notifications";
            String description = "Channel for event notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("events_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lấy theme đã lưu và áp dụng
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        int savedThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(savedThemeMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Áp dụng ngôn ngữ
        LanguageManager.applyLanguage(this);


        createNotificationChannel();

        // Ẩn action bar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.view_bottom_navigation);
        btn_add = findViewById(R.id.fab_add);

        // Khôi phục trạng thái hiển thị của FAB nếu đã được lưu
        if (savedInstanceState != null) {
            savedVisibility = savedInstanceState.getInt(FAB_VISIBILITY_STATE);
            btn_add.setVisibility(savedVisibility);
        }

        // Hiển thị HomeFragment mặc định khi activity được tạo lần đầu
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Thiết lập listener cho BottomNavigationView để chuyển đổi giữa các fragment
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> onNavigationItemSelected(item));

        // Xử lý sự kiện click cho FAB
        btn_add.setOnClickListener(v -> {
            if (bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
                addTask();
            } else if (bottomNavigationView.getSelectedItemId() == R.id.nav_calendar) {
                addEvent();
            }
        });
    }

    // Lưu trạng thái hiển thị của FAB khi activity bị destroy tạm thời (ví dụ: xoay màn hình)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FAB_VISIBILITY_STATE, btn_add.getVisibility());
    }


    // Hàm xử lý khi một item trong BottomNavigationView được chọn
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        // Xác định fragment cần hiển thị dựa trên item được chọn
        if (item.getItemId() == R.id.nav_calendar) {
            selectedFragment = new CalendarFragment();
        } else if (item.getItemId() == R.id.nav_list) {
            selectedFragment = new ListFragment();
        } else if (item.getItemId() == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        } else if (item.getItemId() == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        }

        // Hiển thị fragment đã chọn
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            // Cập nhật savedVisibility và hiển thị/ẩn FAB tương ứng
            if (item.getItemId() == R.id.nav_home || item.getItemId() == R.id.nav_calendar) {
                savedVisibility = View.VISIBLE; // Hiển thị FAB cho HomeFragment và CalendarFragment
            } else {
                savedVisibility = View.GONE; // Ẩn FAB cho các fragment khác
            }
            btn_add.setVisibility(savedVisibility);
        }
        return true;
    }

}