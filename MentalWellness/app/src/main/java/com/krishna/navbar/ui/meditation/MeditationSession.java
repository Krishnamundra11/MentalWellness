package com.krishna.navbar.ui.meditation;

public class MeditationSession {
    private String id;
    private String title;
    private String description;
    private int durationMinutes;
    private String imageUrl;
    private String audioUrl;

    public MeditationSession() {
        // Required empty constructor for Firestore
    }

    public MeditationSession(String id, String title, String description, int durationMinutes, String imageUrl, String audioUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
} 