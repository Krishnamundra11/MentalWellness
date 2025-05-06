package com.krishna.navbar.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPlaylist {
    @DocumentId
    private String id;
    private String name;
    private String colorTheme;
    private int trackCount;
    private Timestamp createdAt;
    private List<Song> songs;

    // Empty constructor for Firestore
    public UserPlaylist() {
        this.songs = new ArrayList<>();
    }

    public UserPlaylist(String name, String colorTheme) {
        this.name = name;
        this.colorTheme = colorTheme;
        this.trackCount = 0; // Default to 0 tracks
        this.createdAt = Timestamp.now();
        this.songs = new ArrayList<>();
    }

    public UserPlaylist(String name, String colorTheme, int trackCount) {
        this.name = name;
        this.colorTheme = colorTheme;
        this.trackCount = trackCount;
        this.createdAt = Timestamp.now();
        this.songs = new ArrayList<>();
    }

    // Convert this UserPlaylist object to a Map for Firestore
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("colorTheme", colorTheme);
        map.put("trackCount", trackCount);
        map.put("createdAt", createdAt);
        
        // Add songs as a list of maps
        List<Map<String, Object>> songsList = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                songsList.add(song.toMap());
            }
        }
        map.put("songs", songsList);
        
        return map;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        this.trackCount = songs != null ? songs.size() : 0;
    }

    public void addSong(Song song) {
        if (this.songs == null) {
            this.songs = new ArrayList<>();
        }
        this.songs.add(song);
        this.trackCount = this.songs.size();
    }

    public void removeSong(Song song) {
        if (this.songs != null) {
            this.songs.remove(song);
            this.trackCount = this.songs.size();
        }
    }
    
    // Get the resource ID for the background drawable based on the color theme
    @Exclude
    public int getBackgroundResourceId() {
        switch (colorTheme) {
            case "blue":
                return com.krishna.navbar.R.drawable.gradient_blue_background;
            case "yellow":
                return com.krishna.navbar.R.drawable.gradient_yellow_background;
            case "purple":
                return com.krishna.navbar.R.drawable.gradient_purple_background;
            case "green":
                return com.krishna.navbar.R.drawable.gradient_green_background;
            case "red":
                return com.krishna.navbar.R.drawable.gradient_red_background;
            case "teal":
                return com.krishna.navbar.R.drawable.gradient_teal_background;
            default:
                return com.krishna.navbar.R.drawable.gradient_blue_background;
        }
    }
    
    // Get the color resource ID for the play button tint based on the color theme
    @Exclude
    public int getColorTint() {
        switch (colorTheme) {
            case "blue":
                return com.krishna.navbar.R.color.colorBlue;
            case "yellow":
                return com.krishna.navbar.R.color.colorYellow;
            case "purple":
                return com.krishna.navbar.R.color.colorPurple;
            case "green":
                return com.krishna.navbar.R.color.colorGreen;
            case "red":
                return com.krishna.navbar.R.color.colorRed;
            case "teal":
                return com.krishna.navbar.R.color.colorTeal;
            default:
                return com.krishna.navbar.R.color.colorBlue;
        }
    }
} 