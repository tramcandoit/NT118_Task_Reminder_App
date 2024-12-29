package com.example.mobileapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageManager {
    private static final String LANGUAGE_KEY = "language_key";
    private static final String PREFS_NAME = "LanguagePrefs";

    // Lưu ngôn ngữ được chọn
    public static void setLanguage(Context context, String languageCode) {
        // Lưu vào SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply();

        // Cập nhật ngôn ngữ
        updateResourcesLanguage(context, languageCode);
    }

    // Lấy ngôn ngữ hiện tại
    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(LANGUAGE_KEY, "en"); // Mặc định là tiếng Anh
    }

    // Cập nhật ngôn ngữ cho ứng dụng
    public static void updateResourcesLanguage(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    // Áp dụng ngôn ngữ khi khởi động ứng dụng
    public static void applyLanguage(Context context) {
        String language = getLanguage(context);
        updateResourcesLanguage(context, language);
    }
}
