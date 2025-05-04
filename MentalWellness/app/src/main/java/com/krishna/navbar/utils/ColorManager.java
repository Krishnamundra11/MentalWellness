package com.krishna.navbar.utils;

import com.krishna.navbar.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to manage color themes for playlists
 */
public class ColorManager {
    private static ColorManager instance;
    private final Map<String, Integer> playlistBackgrounds = new HashMap<>();
    
    // Get singleton instance
    public static ColorManager getInstance() {
        if (instance == null) {
            instance = new ColorManager();
        }
        return instance;
    }
    
    // Private constructor
    private ColorManager() {
        // Initialize playlist to background mapping
        initPlaylistBackgrounds();
    }
    
    /**
     * Initialize the playlist to background drawable mappings
     */
    private void initPlaylistBackgrounds() {
        // Map each playlist to its corresponding gradient background
        playlistBackgrounds.put("Finding Calm", R.drawable.gradient_blue_background);
        playlistBackgrounds.put("Spiritual", R.drawable.gradient_yellow_background);
        playlistBackgrounds.put("Motivation", R.drawable.gradient_mint_background);
        playlistBackgrounds.put("Breathe", R.drawable.gradient_pink_background);
        playlistBackgrounds.put("Mindful Focus", R.drawable.gradient_purple_background);
        playlistBackgrounds.put("Sleep Well", R.drawable.gradient_green_background);
        playlistBackgrounds.put("Anxiety Relief", R.drawable.gradient_orange_background);
        playlistBackgrounds.put("Morning Energy", R.drawable.gradient_teal_background);
        playlistBackgrounds.put("Gratitude Journey", R.drawable.gradient_blue_background);
        playlistBackgrounds.put("Self Compassion", R.drawable.gradient_pink_background);
        
        // Default background for custom playlists
        playlistBackgrounds.put("default", R.drawable.gradient_motivation_background);
    }
    
    /**
     * Get the background drawable resource for a playlist
     * @param playlistName The name of the playlist
     * @return The drawable resource ID for the playlist's background
     */
    public int getPlaylistBackground(String playlistName) {
        if (playlistBackgrounds.containsKey(playlistName)) {
            return playlistBackgrounds.get(playlistName);
        }
        return playlistBackgrounds.get("default");
    }
} 