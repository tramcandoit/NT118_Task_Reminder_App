package com.example.mobileapp;


import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private void addTask() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (homeFragment != null) {
            AddTaskFragment addTaskFragment = AddTaskFragment.newInstance(homeFragment);
            addTaskFragment.show(getSupportFragmentManager(), "addTaskFragment");
        }
        else {
            // Xử lý nếu không tìm thấy HomeFragment, ví dụ: Log
            Log.e("MainActivity", "HomeFragment not found!");
        }
    }

    private void addEvent()
    {
        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (calendarFragment != null) {
            AddEventFragment addEventFragment = AddEventFragment.newInstance(calendarFragment);
            addEventFragment.show(getSupportFragmentManager(), "addEventFragment");
        }

    }

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LanguageManager.applyLanguage(this);

        createNotificationChannel();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        bottomNavigationView = findViewById(R.id.view_bottom_navigation);
        FloatingActionButton btn_add = findViewById(R.id.fab_add);



        // Load the default fragment (HomeFragment) initially
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Set up the BottomNavigationView listener to switch between fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_calendar) {
                    selectedFragment = new CalendarFragment();
                    btn_add.setVisibility(View.VISIBLE);
                } else if (item.getItemId() == R.id.nav_list) {
                    selectedFragment = new ListFragment();
                    btn_add.setVisibility(View.GONE);
                } else if (item.getItemId() == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    btn_add.setVisibility(View.GONE);
                } else if (item.getItemId() == R.id.nav_home) { // Assuming there is a home menu item
                    selectedFragment = new HomeFragment();
                    btn_add.setVisibility(View.VISIBLE);
                }

                // Replace the fragment in the container
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        btn_add.setOnClickListener(v -> {
            if (bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
                addTask();
            }
            else if (bottomNavigationView.getSelectedItemId() == R.id.nav_calendar) {
                addEvent();
            }
        });
    }
}