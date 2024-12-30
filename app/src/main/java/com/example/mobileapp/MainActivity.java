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
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton btn_add;
    private static final String FAB_VISIBILITY_STATE = "fab_visibility_state";
    private int savedVisibility = View.VISIBLE;

    private void addTask() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (homeFragment != null) {
            AddTaskFragment addTaskFragment = AddTaskFragment.newInstance(homeFragment);
            addTaskFragment.show(getSupportFragmentManager(), "addTaskFragment");
        } else {
            Log.e("MainActivity", "HomeFragment not found!");
        }
    }

    private void addEvent() {
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
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        int savedThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(savedThemeMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LanguageManager.applyLanguage(this);

        createNotificationChannel();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.view_bottom_navigation);
        btn_add = findViewById(R.id.fab_add);


        if (savedInstanceState != null) {
            savedVisibility = savedInstanceState.getInt(FAB_VISIBILITY_STATE);
            btn_add.setVisibility(savedVisibility);
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> onNavigationItemSelected(item));


        btn_add.setOnClickListener(v -> {
            if (bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
                addTask();
            } else if (bottomNavigationView.getSelectedItemId() == R.id.nav_calendar) {
                addEvent();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FAB_VISIBILITY_STATE, btn_add.getVisibility());
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_calendar) {
            selectedFragment = new CalendarFragment();
        } else if (item.getItemId() == R.id.nav_list) {
            selectedFragment = new ListFragment();
        } else if (item.getItemId() == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        } else if (item.getItemId() == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            if (item.getItemId() == R.id.nav_home || item.getItemId() == R.id.nav_calendar) {
                savedVisibility = View.VISIBLE;
            } else {
                savedVisibility = View.GONE;
            }
            btn_add.setVisibility(savedVisibility); // Đảm bảo đặt lại savedVisibility
        }
        return true;
    }

}