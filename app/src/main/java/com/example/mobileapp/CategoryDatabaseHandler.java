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
import java.util.UUID;

public class CategoryDatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    private static final int ID_BOUND = 10000;
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "CategoriesManager";
    // Contacts table name
    private static final String TABLE_CATEGORIES = "Categories";
    // Contacts Table Columns names
    private static final String KEY_CATEGORYID = "categoryId";
    private static final String KEY_USERID = "userId";
    private static final String KEY_NAME = "ten_the_loai";
    private static final String KEY_ICONRESID = "iconResId";

    public CategoryDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                TABLE_CATEGORIES + "("
                + KEY_CATEGORYID + " INTEGER PRIMARY KEY,"
                + KEY_USERID + " INTEGER ,"
                + KEY_NAME + " TEXT,"
                + KEY_ICONRESID + " INTEGER"
                + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Thêm 4 categories mặc định sau khi tạo bảng
        db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" + KEY_CATEGORYID + ", " + KEY_USERID + ", " + KEY_NAME + ", " + KEY_ICONRESID + ") VALUES " +
                "(" + generateCategoryId() + ", 111, 'Work', " + R.drawable.icon_work + "), " +
                "(" + generateCategoryId() + ", 111, 'Health', " + R.drawable.icon_user + "), " +
                "(" + generateCategoryId() + ", 111, 'Finance', " + R.drawable.icon_finance + "), " +
                "(" + generateCategoryId() + ", 111, 'Education', " + R.drawable.icon_education + ")");




    }

    // Upgrade CSDL
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        // Create tables again
        onCreate(db);
    }

    // Thêm contact
    public long addCategory(CategoriesItem category) {
        Log.d(TAG, "Adding category: " + category.getName());
        int uniqueId = generateUniqueRandomId();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORYID, uniqueId);
        values.put(KEY_USERID, category.getUserId());
        values.put(KEY_NAME, category.getName());
        values.put(KEY_ICONRESID, category.getIconResId());

        // Inserting Row
        long id = db.insert(TABLE_CATEGORIES, null, values);
        if (id == -1) {
            Log.e(TAG, "Failed to insert category: " + category.getName());
        } else {
            Log.d(TAG, "Category added with id: ");
        }
        db.close(); // Close database connection
        return id;
    }

    // Get danh mục
    public CategoriesItem getCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES,
                new String[] { KEY_CATEGORYID, KEY_USERID, KEY_NAME, KEY_ICONRESID },
                KEY_CATEGORYID + "=?",
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

        CategoriesItem category = new CategoriesItem(
                Integer.parseInt(cursor.getString(0)),      // getCategoryId
                Integer.parseInt(cursor.getString(1)),      // getUserId
                cursor.getString(2),                        // getName
                Integer.parseInt(cursor.getString(3))       // getIconResId
        );
        return category;
    }

    // // Get all danh mục
    public List<CategoriesItem> getAllCategories() {
        List<CategoriesItem> CategoryList = new ArrayList<CategoriesItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CategoriesItem Category = new CategoriesItem();
                Category.setCategoryId(Integer.parseInt(cursor.getString(0)));
//                Category.setUserId(Integer.parseInt(cursor.getString(1)));
                Category.setName(cursor.getString(2));
                Category.setIconResId(Integer.parseInt(cursor.getString(3)));
                // Adding Contact to list
                CategoryList.add(Category);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return CategoryList;
    }

    // Update danh mục
    public int updateCategory(CategoriesItem category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORYID, category.getCategoryId());
        values.put(KEY_USERID, category.getUserId());
        values.put(KEY_NAME, category.getName());
        values.put(KEY_ICONRESID, category.getIconResId());

        // Updating hàng
        return db.update(TABLE_CATEGORIES, values,
                KEY_CATEGORYID + " = ?",
                new String[]{String.valueOf(category.getCategoryId())}
        );
    }

    // Xóa danh mục
    public void deleteCategory(CategoriesItem category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, KEY_CATEGORYID + " = ?",
                new String[] { String.valueOf(category.getCategoryId()) });
        db.close();
    }

    // Xóa all danh mục
    public void deleteAllCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, null, null); // Xóa tất cả các hàng trong bảng Categories
        db.close();
    }

    private void insertDefaultCategory(SQLiteDatabase db, int userId, String name, int iconResId) {
        ContentValues values = new ContentValues();
        int categoryId = generateUniqueRandomId();
        values.put(KEY_CATEGORYID, categoryId);
        values.put(KEY_USERID, userId);
        values.put(KEY_NAME, name);
        values.put(KEY_ICONRESID, iconResId);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    private boolean checkIfIdExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + TABLE_CATEGORIES + " WHERE " + KEY_CATEGORYID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    private int generateCategoryId() {
        // Tạo key ngẫu nhiên, UUID, hoặc bất kỳ logic nào bạn muốn
        UUID uuid = UUID.randomUUID();
        return uuid.hashCode(); // Hoặc bất kỳ cách nào để chuyển đổi UUID thành int
    }

    public int generateUniqueRandomId() {
        int randomId;
        boolean idExists;
        do {
            randomId = (int) (Math.random() * ID_BOUND) + 1;
            idExists = checkIfIdExists(randomId);
        } while (idExists);
        return randomId;
    }
}
