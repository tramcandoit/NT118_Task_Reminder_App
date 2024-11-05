package com.example.mobileapp;

public class Event {
    private int eventId;
    private int userId;
    private String name;
    private String date;
    private String repeat_frequency;
    private String description;

    public Event() {
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
