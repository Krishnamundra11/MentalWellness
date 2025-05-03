package com.krishna.navbar.models;

public class TherapistBooking {
    private String therapistId;
    private String therapistName;
    private String date;
    private String time;
    private String mode;
    private String status;

    public TherapistBooking() {}

    public TherapistBooking(String therapistId, String therapistName, String date, String time, String mode, String status) {
        this.therapistId = therapistId;
        this.therapistName = therapistName;
        this.date = date;
        this.time = time;
        this.mode = mode;
        this.status = status;
    }

    public String getTherapistId() { return therapistId; }
    public void setTherapistId(String therapistId) { this.therapistId = therapistId; }
    public String getTherapistName() { return therapistName; }
    public void setTherapistName(String therapistName) { this.therapistName = therapistName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 