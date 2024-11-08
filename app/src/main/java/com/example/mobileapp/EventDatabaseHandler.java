package com.example.mobileapp;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
}
