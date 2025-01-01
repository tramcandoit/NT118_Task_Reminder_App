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

    public static String getLocalizedText(Context context, String key) {
        String language = getLanguage(context);
        switch (key) {
            // Calendar Fragment
            case "no_events_today":
                return language.equals("vi") ? "Không có sự kiện hôm nay" : "No events today";
            case "no_events_on_date":
                return language.equals("vi") ? "Không có sự kiện vào %s" : "No events on %s";
            case "event_details":
                return language.equals("vi") ? "Chi tiết sự kiện" : "Event Details";
            case "date":
                return language.equals("vi") ? "Ngày" : "Date";
            case "frequency":
                return language.equals("vi") ? "Tần suất" : "Frequency";
            case "description":
                return language.equals("vi") ? "Mô tả" : "Description";
            case "ok":
                return language.equals("vi") ? "Đồng ý" : "OK";
            case "delete":
                return language.equals("vi") ? "Xóa" : "Delete";
            case "confirm_delete":
                return language.equals("vi") ? "Xác nhận xóa" : "Confirm delete";
            case "confirm_delete_message":
                return language.equals("vi")
                        ? "Bạn có chắc chắn muốn xóa sự kiện này?"
                        : "Are you sure you want to delete this event?";
            case "yes":
                return language.equals("vi") ? "Có" : "Yes";
            case "no":
                return language.equals("vi") ? "Không" : "No";
            case "event_deleted":
                return language.equals("vi") ? "Đã xóa sự kiện thành công" : "Event deleted successfully";
            case "save":
                return language.equals("vi") ? "Lưu" : "Save";
            case "cancel":
                return language.equals("vi") ? "Thoát" : "Cancel";
            case "event_added":
                return language.equals("vi") ? "Đã thêm sự kiện thành công" : "Event added successfully";
            case "task_added":
                return language.equals("vi") ? "Đã thêm nhiệm vụ thành công" : "Task added successfully";
            case "logout_success":
                return language.equals("vi") ? "Đã đăng xuất thành công" : "Log out successfully";

            default:
                return key;
        }
    }
}
