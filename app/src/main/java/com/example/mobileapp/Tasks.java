package com.example.mobileapp;
public class Tasks {
    private String name;
    private String hour;

    public Tasks(String name, String hour) {
        this.name = name;
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    public String getHour() {
        return hour;
    }

    @Override
    public String toString() {
        return name + " - " + hour;
    }
}
