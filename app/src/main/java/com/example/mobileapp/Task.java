package com.example.mobileapp;

public class Task {
    private int taskId;
    private int userId;
    private String name;
    private String priority;
    private String status;
    private String date;
    private String time;
    private String repeat_frequency;
    private String description;

    public Task() {
    }

    public Task(int taskId, int userId, String name, String priority, String status, String date, String time, String repeat_frequency, String description) {
        this.taskId = taskId;
        this.userId = userId;
        this.name = name;
        this.priority = priority;
        this.status = status;
        this.date = date;
        this.time = time;
        this.repeat_frequency = repeat_frequency;
        this.description = description;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeat_frequency() {
        return repeat_frequency;
    }

    public void setRepeat_frequency(String repeat_frequency) {
        this.repeat_frequency = repeat_frequency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
