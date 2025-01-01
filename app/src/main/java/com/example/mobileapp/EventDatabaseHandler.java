package com.example.mobileapp;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventDatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "EventDatabaseHandler";
    private static final int ID_BOUND = 10000;
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "EventManager";
    // Contacts table name
    private static final String TABLE_EVENTS = "ListEvent";
    // Contacts Table Columns names

    //private int eventId;
    //    private int userId;
    //    private String name;
    //    private String date;
    //    private String repeat_frequency;
    //    private String description;
    private static final String KEY_EVENTID = "event_id";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_FREQ = "freq";
    private static final String KEY_DESCRIPTION = "description";


    public EventDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " +
                TABLE_EVENTS + "("
                + KEY_EVENTID + " INTEGER PRIMARY KEY,"
                + KEY_USERID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_FREQ + " TEXT,"
                + KEY_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void AddEvent(Event ev) {
        Log.d(TAG, "Adding event: " + ev.getName() + " on " + ev.getDate());
        int uniqueId = generateUniqueRandomId();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EVENTID, uniqueId);
        values.put(KEY_DATE, ev.getDate());
        values.put(KEY_FREQ, ev.getRepeat_frequency());
        values.put(KEY_NAME, ev.getName());
        values.put(KEY_DESCRIPTION, ev.getDescription());
        long result = db.insert(TABLE_EVENTS, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert event: " + ev.getName());
        } else {
            Log.d(TAG, "Event added with id: " + uniqueId);
        }
        db.close();
    }

    public void RemoveEvent(int ID)
    {

            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_EVENTS, KEY_EVENTID + " = ?",
                    new String[]{String.valueOf(ID)});
            db.close();



    }

    public List<Event> getAllEvent() {
        List<Event> eventList = new ArrayList<Event>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Event ev = new Event();
                ev.setEventId(Integer.parseInt(cursor.getString(0)));
                //ev.setUserId(cursor.getString(1));
                ev.setName(cursor.getString(2));
                ev.setDate(cursor.getString(3));
                ev.setRepeat_frequency(cursor.getString(4));
                ev.setDescription(cursor.getString(5));
                eventList.add(ev);
            } while (cursor.moveToNext());
        }
        cursor.close();
        for (Event event : eventList) {
            Log.d(TAG, "Event: " + event.getName() + ", Date: " + event.getDate() +
                    ", Frequency: " + event.getRepeat_frequency() + ", Description: " + event.getDescription());
        }
        return eventList;
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    private boolean checkIfIdExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + TABLE_EVENTS + " WHERE event_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        boolean exists = cursor.moveToFirst(); // Returns true if the cursor found a record
        cursor.close();
        return exists;
    }

    public int generateUniqueRandomId() {
        int randomId;
        boolean idExists;
        do {
            randomId = (int) (Math.random() * ID_BOUND) + 1; // Generating a random number between 1 and ID_BOUND
            idExists = checkIfIdExists(randomId);
        } while (idExists);
        return randomId;
    }

    public List<Event> getEventsByDate(String date) {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn các event có ngày trùng khớp
        Cursor cursor = db.query(
                TABLE_EVENTS,
                null,
                KEY_DATE + " = ?",
                new String[]{date},
                null,
                null,
                null
        );

        // Đọc dữ liệu từ cursor
        if (cursor.moveToFirst()) {
            do {
                Event ev = new Event();
                ev.setEventId(cursor.getInt(cursor.getColumnIndex(KEY_EVENTID)));
                ev.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                ev.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                ev.setRepeat_frequency(cursor.getString(cursor.getColumnIndex(KEY_FREQ)));
                ev.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

                eventList.add(ev);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Log các sự kiện được tìm thấy
        for (Event event : eventList) {
            Log.d(TAG, "Event on " + date + ": " + event.getName());
        }

        return eventList;
    }

    // Phương thức để lấy các sự kiện sắp tới
    public List<Event> getUpcomingEvents() {
        List<Event> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy ngày hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Truy vấn các event từ ngày hiện tại trở đi
        String query = "SELECT * FROM " + TABLE_EVENTS +
                " WHERE date(substr(" + KEY_DATE + ", 7, 4) || '-' || " +
                "substr(" + KEY_DATE + ", 4, 2) || '-' || " +
                "substr(" + KEY_DATE + ", 1, 2)) >= date(?)";

        Cursor cursor = db.rawQuery(query, new String[]{currentDate});

        if (cursor.moveToFirst()) {
            do {
                Event ev = new Event();
                ev.setEventId(cursor.getInt(cursor.getColumnIndex(KEY_EVENTID)));
                ev.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                ev.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                ev.setRepeat_frequency(cursor.getString(cursor.getColumnIndex(KEY_FREQ)));
                ev.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

                eventList.add(ev);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return eventList;
    }

    // Phương thức để đặt lịch nhắc nhở cho các sự kiện
    public void scheduleEventReminders(Context context) {
        List<Event> upcomingEvents = getUpcomingEvents();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (Event event : upcomingEvents) {
            try {
                // Chuyển đổi ngày sự kiện sang Calendar
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date eventDate = sdf.parse(event.getDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(eventDate);

                // Tạo Intent cho BroadcastReceiver
                Intent intent = new Intent(context, EventNotificationReceiver.class);
                intent.putExtra("eventName", event.getName());
                intent.putExtra("eventDescription", event.getDescription());
                intent.putExtra("eventId", event.getEventId());

                // Tạo PendingIntent
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        event.getEventId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // Đặt thời gian nhắc nhở (ví dụ: vào 9 giờ sáng của ngày sự kiện)
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                // Đặt báo thức
                if (alarmManager != null) {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                    Log.d(TAG, "Scheduled reminder for event: " + event.getName());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error scheduling reminder", e);
            }
        }
    }

    // Phương thức hủy báo thức cho một sự kiện cụ thể
    public void cancelEventReminder(Context context, int eventId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, EventNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "Canceled reminder for event ID: " + eventId);
        }
    }

    public void UpdateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, event.getName());
        values.put(KEY_DATE, event.getDate());
        values.put(KEY_FREQ, event.getRepeat_frequency());
        values.put(KEY_DESCRIPTION, event.getDescription());

        db.update(TABLE_EVENTS, values, KEY_EVENTID + " = ?", new String[]{String.valueOf(event.getEventId())});
        db.close();
    }



}
