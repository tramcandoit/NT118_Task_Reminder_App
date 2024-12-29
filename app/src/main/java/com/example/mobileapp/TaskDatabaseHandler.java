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

public class TaskDatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "TasksManager";
    // Contacts table name
    private static final String TABLE_TASKS = "Tasks";
    // Contacts Table Columns names
    private static final String KEY_TASKID = "taskId";
    private static final String KEY_USERID = "userId";
    private static final String KEY_CATEGORYID = "categoryId";
    private static final String KEY_NAME = "ten_nhiem_vu";
    private static final String KEY_PRIORITY = "muc_do_uu_tien";
    private static final String KEY_STATUS = "trang_thai";
    private static final String KEY_DATE = "ngay_thuc_hien";
    private static final String KEY_TIME = "thoi_gian_thuc_hien";
    private static final String KEY_FREQUENCY = "tan_suat_lap_lai";
    private static final String KEY_DESCRIPTION = "mo_ta";

    public TaskDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " +
                TABLE_TASKS + "("
                + KEY_TASKID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERID + " INTEGER ,"
                + KEY_CATEGORYID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_PRIORITY + " INTEGER,"
                + KEY_STATUS + " INTEGER,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_FREQUENCY + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public long addTask(Task task) {
        Log.d(TAG, "Adding event: " + task.getName() + " on " +task.getDate());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, task.getUserId());
        values.put(KEY_CATEGORYID, task.getCategoryId());
        values.put(KEY_NAME, task.getName());
        values.put(KEY_PRIORITY, task.getPriority());
        values.put(KEY_STATUS, task.getStatus());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_FREQUENCY, task.getRepeat_frequency());
        values.put(KEY_DESCRIPTION, task.getDescription());

        // Inserting Row
        long id = db.insert(TABLE_TASKS, null, values);
        if (id == -1) {
            Log.e(TAG, "Failed to insert event: " + task.getName());
        } else {
            Log.d(TAG, "Event added with id: ");
        }
        db.close(); // Close database connection
        return id;
    }

    // Getting single Task
    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[] { KEY_TASKID, KEY_USERID, KEY_CATEGORYID, KEY_NAME, KEY_PRIORITY, KEY_STATUS, KEY_DATE, KEY_TIME, KEY_FREQUENCY, KEY_DESCRIPTION },
                KEY_TASKID + "=?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        // Return null nếu không tìm thấy Contact
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        Task task = new Task(
                Integer.parseInt(cursor.getString(0)),   // getTaskId
                Integer.parseInt(cursor.getString(1)),    // getUserId
                Integer.parseInt(cursor.getString(2)),    // getCategoryId
                cursor.getString(3),    // getName
                cursor.getString(4),    // getPriority
                cursor.getString(5),    // getStatus
                cursor.getString(6),    // getDate
                cursor.getString(7),    // getTime
                cursor.getString(8),    // getRepeat_frequency
                cursor.getString(9)    // getDescription
        );
        return task;
    }

    // Getting All Tasks
    public List<Task> getAllTasks() {
        List<Task> TaskList = new ArrayList<Task>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task Task = new Task();
                Task.setTaskId(Integer.parseInt(cursor.getString(0)));
//                Task.setUserId(Integer.parseInt(cursor.getString(1)));
                Task.setCategoryId(Integer.parseInt(cursor.getString(2)));
                Task.setName(cursor.getString(3));
                Task.setPriority(cursor.getString(4));
                Task.setStatus(cursor.getString(5));
                Task.setDate(cursor.getString(6));
                Task.setTime(cursor.getString(7));
                Task.setRepeat_frequency(cursor.getString(8));
                Task.setDescription(cursor.getString(9));
                // Adding Contact to list
                TaskList.add(Task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return Task list
        return TaskList;
    }

    // Updating single contact
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, task.getUserId());
        values.put(KEY_CATEGORYID, task.getCategoryId());
        values.put(KEY_NAME, task.getName());
        values.put(KEY_PRIORITY, task.getPriority());
        values.put(KEY_STATUS, task.getStatus());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_FREQUENCY, task.getRepeat_frequency());
        values.put(KEY_DESCRIPTION, task.getDescription());


        // Updating row
        return db.update(TABLE_TASKS, values,
                KEY_TASKID + " = ?",
                new String[]{String.valueOf(task.getTaskId())}
        );
    }

    // Deleting single contact
    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_TASKID + " = ?",
                new String[] { String.valueOf(task.getTaskId()) });
        db.close();
    }

    // Deleting all Tasks
    public void deleteAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, null, null); // Xóa tất cả các hàng trong bảng Tasks
        db.close();
    }
}
