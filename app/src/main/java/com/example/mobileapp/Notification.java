package com.example.mobileapp;

public class Notification {
    private int notificationId;
    private int userId;
    private int sound;
    private int vibration;

    public Notification() {
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int getVibration() {
        return vibration;
    }

    public void setVibration(int vibration) {
        this.vibration = vibration;
    }
}
