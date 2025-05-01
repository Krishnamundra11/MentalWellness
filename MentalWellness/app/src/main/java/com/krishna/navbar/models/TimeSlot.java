package com.krishna.navbar.models;

public class TimeSlot {
    private String time;
    private boolean isSelected;
    private boolean isAvailable;

    public TimeSlot(String time) {
        this.time = time;
        this.isSelected = false;
        this.isAvailable = true;
    }

    public TimeSlot(String time, boolean isAvailable) {
        this.time = time;
        this.isSelected = false;
        this.isAvailable = isAvailable;
    }

    public String getTime() {
        return time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
} 