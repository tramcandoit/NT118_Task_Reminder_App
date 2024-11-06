package com.example.mobileapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHandler extends SQLiteOpenHelper {



    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "UsersManager";
    // Contacts table name
    private static final String TABLE_USERS = "Users";
    // Contacts Table Columns names
    private static final String KEY_USERID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    public UserDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        String CREATE_TASKS_TABLE = "CREATE TABLE " +
                TABLE_USERS + "("
                + KEY_USERID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + "TEXT,"
                + KEY_PASSWORD + "TEXT"
                + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Create tables again
        onCreate(db);

    }

    public void AddUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, user.getUser_id());
        values.put(KEY_NAME, user.getUsername());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, user.getPassword());
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public User getUserbyID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USERID, KEY_NAME, KEY_EMAIL, KEY_PASSWORD}, KEY_USERID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User();
        user.setUser_id(Integer.parseInt(cursor.getString(0)));
        user.setUsername(cursor.getString(1));
        user.setEmail(cursor.getString(2));
        user.setPassword(cursor.getString(3));
        cursor.close();
        return user;
    }

    public void deleteUserbyID(int ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_USERID + " = ?",
                new String[]{String.valueOf(ID)});
        db.close();
    }

}
