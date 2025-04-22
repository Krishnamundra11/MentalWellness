package com.example.mentalwellness.ui.meditation;

import java.io.Serializable;

public class MeditationSession implements Serializable {
    
    private final int id;
    private final String title;
    private final String description;
    private final String duration;
    private final int imageResourceId;
    private final int audioResourceId;
    
    public MeditationSession(int id, String title, String description, String duration, 
                            int imageResourceId, int audioResourceId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.imageResourceId = imageResourceId;
        this.audioResourceId = audioResourceId;
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public int getImageResourceId() {
        return imageResourceId;
    }
    
    public int getAudioResourceId() {
        return audioResourceId;
    }
} 