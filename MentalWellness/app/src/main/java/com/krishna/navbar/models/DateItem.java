package com.krishna.navbar.models;

import java.util.Date;

public class DateItem {
    private String dayName;
    private String dayNumber;
    private Date date;
    private boolean isSelected;

    public DateItem(String dayName, String dayNumber, Date date) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.date = date;
        this.isSelected = false;
    }

    public String getDayName() {
        return dayName;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
} 