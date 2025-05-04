package com.krishna.navbar.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to manage playlist categories for filtering
 */
public class PlaylistCategoryManager {
    private static PlaylistCategoryManager instance;
    private final Map<String, List<String>> categoryPlaylists = new HashMap<>();
    
    // Get singleton instance
    public static PlaylistCategoryManager getInstance() {
        if (instance == null) {
            instance = new PlaylistCategoryManager();
        }
        return instance;
    }
    
    // Private constructor
    private PlaylistCategoryManager() {
        // Initialize categories
        initCategories();
    }
    
    /**
     * Initialize playlist categories
     */
    private void initCategories() {
        // Relax category playlists
        List<String> relaxPlaylists = Arrays.asList(
            "Finding Calm",
            "Sleep Well",
            "Anxiety Relief",
            "Breathe"
        );
        categoryPlaylists.put("Relax", relaxPlaylists);
        
        // Balance category playlists
        List<String> balancePlaylists = Arrays.asList(
            "Spiritual",
            "Gratitude Journey",
            "Self Compassion"
        );
        categoryPlaylists.put("Balance", balancePlaylists);
        
        // Mind category playlists
        List<String> mindPlaylists = Arrays.asList(
            "Motivation",
            "Mindful Focus",
            "Morning Energy"
        );
        categoryPlaylists.put("Mind", mindPlaylists);
    }
    
    /**
     * Get playlists for a specific category
     * @param category The category to get playlists for
     * @return List of playlist names in the category
     */
    public List<String> getPlaylistsForCategory(String category) {
        if (categoryPlaylists.containsKey(category)) {
            return new ArrayList<>(categoryPlaylists.get(category));
        }
        return new ArrayList<>();
    }
    
    /**
     * Check if a playlist belongs to a specific category
     * @param category The category to check
     * @param playlist The playlist name
     * @return True if the playlist belongs to the category
     */
    public boolean isPlaylistInCategory(String category, String playlist) {
        if (categoryPlaylists.containsKey(category)) {
            return categoryPlaylists.get(category).contains(playlist);
        }
        return false;
    }
} 