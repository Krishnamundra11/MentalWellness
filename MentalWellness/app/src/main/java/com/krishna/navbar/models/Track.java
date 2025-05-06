package com.krishna.navbar.models;

public class Track {
    private String title;
    private String artist;
    private String duration;
    private boolean favorite;

    public Track(String title, String artist, String duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.favorite = false;
    }
    
    public Track(String title, String artist, String duration, boolean favorite) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.favorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public boolean isFavorite() {
        return favorite;
    }
    
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    
    public boolean toggleFavorite() {
        this.favorite = !this.favorite;
        return this.favorite;
    }
} 