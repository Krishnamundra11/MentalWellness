package com.krishna.navbar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.krishna.navbar.models.Track;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to manage favorite tracks
 */
public class FavoritesManager {
    private static final String PREFS_NAME = "FavoritesPrefs";
    private static final String FAVORITES_KEY = "favorites";
    
    private static FavoritesManager instance;
    private final List<Track> mockFavorites; // For demo purposes
    
    // Get singleton instance
    public static FavoritesManager getInstance() {
        if (instance == null) {
            instance = new FavoritesManager();
        }
        return instance;
    }
    
    // Private constructor
    private FavoritesManager() {
        // Initialize with some mock data for demonstration
        mockFavorites = new ArrayList<>();
        mockFavorites.add(new Track("Ocean Waves", "Nature Sounds", "5:10"));
        mockFavorites.add(new Track("Morning Light", "Spiritual Healers", "4:25"));
        mockFavorites.add(new Track("Rise and Conquer", "InspiroTunes", "3:45"));
    }
    
    /**
     * Add a track to favorites
     * @param track The track to add
     */
    public void addFavorite(Track track) {
        if (!isTrackFavorite(track)) {
            mockFavorites.add(track);
            // In a real implementation, this would save to SharedPreferences or a database
        }
    }
    
    /**
     * Remove a track from favorites
     * @param track The track to remove
     */
    public void removeFavorite(Track track) {
        // Find the track with matching title and artist
        for (int i = 0; i < mockFavorites.size(); i++) {
            Track favorite = mockFavorites.get(i);
            if (favorite.getTitle().equals(track.getTitle()) && 
                favorite.getArtist().equals(track.getArtist())) {
                mockFavorites.remove(i);
                break;
            }
        }
        // In a real implementation, this would save to SharedPreferences or a database
    }
    
    /**
     * Check if a track is in favorites
     * @param track The track to check
     * @return True if the track is a favorite
     */
    public boolean isTrackFavorite(Track track) {
        for (Track favorite : mockFavorites) {
            if (favorite.getTitle().equals(track.getTitle()) && 
                favorite.getArtist().equals(track.getArtist())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all favorite tracks
     * @return List of favorite tracks
     */
    public List<Track> getFavoriteTracks() {
        return new ArrayList<>(mockFavorites);
    }
    
    /**
     * Toggle favorite status of a track
     * @param track The track to toggle
     * @return True if the track is now a favorite, false otherwise
     */
    public boolean toggleFavorite(Track track) {
        if (isTrackFavorite(track)) {
            removeFavorite(track);
            return false;
        } else {
            addFavorite(track);
            return true;
        }
    }
    
    // In a real implementation, these methods would use SharedPreferences or a database
    
    /*
    // Example implementation with SharedPreferences
    private void saveFavoritesToPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        Set<String> favoriteStrings = new HashSet<>();
        for (Track track : mockFavorites) {
            // Format: title|artist|duration
            favoriteStrings.add(track.getTitle() + "|" + track.getArtist() + "|" + track.getDuration());
        }
        
        editor.putStringSet(FAVORITES_KEY, favoriteStrings);
        editor.apply();
    }
    
    private void loadFavoritesFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favoriteStrings = prefs.getStringSet(FAVORITES_KEY, new HashSet<>());
        
        mockFavorites.clear();
        for (String favoriteString : favoriteStrings) {
            String[] parts = favoriteString.split("\\|");
            if (parts.length == 3) {
                mockFavorites.add(new Track(parts[0], parts[1], parts[2]));
            }
        }
    }
    */
} 