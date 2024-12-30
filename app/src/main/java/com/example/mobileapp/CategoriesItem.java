package com.example.mobileapp;

public class CategoriesItem {
    private int categoryId;
    private int userId;
    private String name;
    private int iconResId;

    public CategoriesItem() {
    }

    public CategoriesItem(int categoryId, int userId, String name, int iconResId) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
        this.iconResId = iconResId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
