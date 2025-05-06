package com.krishna.navbar.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.HashMap;
import java.util.Map;

public class Song {
    @DocumentId
    private String id;
    private String songTitle;
    private String artistName;
    private String songUrl;
    private Timestamp addedAt;
    private boolean favorite;

    // Empty constructor for Firestore
    public Song() {
    }

    public Song(String songTitle, String artistName, String songUrl) {
        this.songTitle = songTitle;
        this.artistName = artistName;
        this.songUrl = songUrl;
        this.addedAt = Timestamp.now();
        this.favorite = false;
    }

    // Convert this Song object to a Map for Firestore
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("songTitle", songTitle);
        map.put("artistName", artistName);
        map.put("songUrl", songUrl);
        map.put("addedAt", addedAt);
        map.put("favorite", favorite);
        return map;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
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