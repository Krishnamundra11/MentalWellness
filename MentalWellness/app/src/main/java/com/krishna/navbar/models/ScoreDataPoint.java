package com.krishna.navbar.models;

/**
 * Model class representing a data point for charting
 */
public class ScoreDataPoint {
    private String date;
    private int score;
    private String dayOfWeek;
    
    public ScoreDataPoint() {
        // Required empty constructor for Firestore
    }
    
    public ScoreDataPoint(String date, int score, String dayOfWeek) {
        this.date = date;
        this.score = score;
        this.dayOfWeek = dayOfWeek;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
} 