package com.krishna.navbar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to manage music preferences using SharedPreferences
 */
public class MusicPreferencesManager {
    private static final String PREFS_NAME = "MeditationMusicPrefs";
    private static final String STRESS_RELIEF_TRACKS = "stress_relief_tracks";
    private static final String DEPRESSION_HELP_TRACKS = "depression_help_tracks";
    private static final String SLEEP_AID_TRACKS = "sleep_aid_tracks";
    private static final String LAST_PLAYED_CATEGORY = "last_played_category";
    private static final String LAST_PLAYED_TRACK = "last_played_track";
    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String VOLUME_LEVEL = "volume_level";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public MusicPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        
        // Initialize default tracks if they don't exist
        initializeDefaultTracksIfNeeded();
    }

    private void initializeDefaultTracksIfNeeded() {
        // Only initialize if they don't exist yet
        if (!sharedPreferences.contains(STRESS_RELIEF_TRACKS)) {
            // Initialize default stress relief tracks
            List<String> stressReliefTracks = new ArrayList<>();
            stressReliefTracks.add("meditation_example.mp3");
            saveTrackList(STRESS_RELIEF_TRACKS, stressReliefTracks);
        }

        if (!sharedPreferences.contains(DEPRESSION_HELP_TRACKS)) {
            // Initialize default depression help tracks
            List<String> depressionHelpTracks = new ArrayList<>();
            depressionHelpTracks.add("meditation_example.mp3");
            saveTrackList(DEPRESSION_HELP_TRACKS, depressionHelpTracks);
        }

        if (!sharedPreferences.contains(SLEEP_AID_TRACKS)) {
            // Initialize default sleep aid tracks
            List<String> sleepAidTracks = new ArrayList<>();
            sleepAidTracks.add("meditation_example.mp3");
            saveTrackList(SLEEP_AID_TRACKS, sleepAidTracks);
        }
    }

    private void saveTrackList(String key, List<String> trackList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(trackList);
        editor.putString(key, json);
        editor.apply();
    }

    private List<String> getTrackList(String key) {
        String json = sharedPreferences.getString(key, null);
        if (json == null) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            return gson.fromJson(json, type);
        }
    }

    /**
     * Get all music categories with their tracks
     * @return Map of category names to track lists
     */
    public Map<String, List<String>> getAllMusicData() {
        Map<String, List<String>> musicData = new HashMap<>();
        musicData.put("stressRelief", getTrackList(STRESS_RELIEF_TRACKS));
        musicData.put("depressionHelp", getTrackList(DEPRESSION_HELP_TRACKS));
        musicData.put("sleepAid", getTrackList(SLEEP_AID_TRACKS));
        return musicData;
    }

    /**
     * Get tracks for stress relief category
     * @return List of stress relief track paths
     */
    public List<String> getStressReliefTracks() {
        return getTrackList(STRESS_RELIEF_TRACKS);
    }

    /**
     * Get tracks for depression help category
     * @return List of depression help track paths
     */
    public List<String> getDepressionHelpTracks() {
        return getTrackList(DEPRESSION_HELP_TRACKS);
    }

    /**
     * Get tracks for sleep aid category
     * @return List of sleep aid track paths
     */
    public List<String> getSleepAidTracks() {
        return getTrackList(SLEEP_AID_TRACKS);
    }

    /**
     * Save the last played track information
     * @param category The music category
     * @param trackPath The track file path
     * @param position The playback position in milliseconds
     */
    public void saveLastPlayedInfo(String category, String trackPath, long position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_PLAYED_CATEGORY, category);
        editor.putString(LAST_PLAYED_TRACK, trackPath);
        editor.putLong(PLAYBACK_POSITION, position);
        editor.apply();
    }

    /**
     * Get the last played category
     * @return The last played music category
     */
    public String getLastPlayedCategory() {
        return sharedPreferences.getString(LAST_PLAYED_CATEGORY, "");
    }

    /**
     * Get the last played track path
     * @return The last played track file path
     */
    public String getLastPlayedTrack() {
        return sharedPreferences.getString(LAST_PLAYED_TRACK, "");
    }

    /**
     * Get the last playback position
     * @return The last playback position in milliseconds
     */
    public long getLastPlaybackPosition() {
        return sharedPreferences.getLong(PLAYBACK_POSITION, 0);
    }

    /**
     * Save the volume level
     * @param volume Volume level between 0.0 and 1.0
     */
    public void saveVolumeLevel(float volume) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(VOLUME_LEVEL, volume);
        editor.apply();
    }

    /**
     * Get the saved volume level
     * @return Volume level between 0.0 and 1.0
     */
    public float getVolumeLevel() {
        return sharedPreferences.getFloat(VOLUME_LEVEL, 0.7f); // Default to 70%
    }

    /**
     * Clear all stored preferences
     */
    public void clearAllPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        
        // Re-initialize default tracks
        initializeDefaultTracksIfNeeded();
    }

    /**
     * Check if stored preferences contain valid data.
     * If not, reset to defaults.
     */
    public void validateAndResetIfNeeded() {
        boolean needsReset = false;
        
        // Check if each category has at least one track
        if (getStressReliefTracks().isEmpty() || 
            getDepressionHelpTracks().isEmpty() || 
            getSleepAidTracks().isEmpty()) {
            needsReset = true;
        }
        
        // Check if last played track exists
        String lastCategory = getLastPlayedCategory();
        String lastTrack = getLastPlayedTrack();
        if (!lastCategory.isEmpty() && !lastTrack.isEmpty()) {
            List<String> tracks;
            switch (lastCategory) {
                case "stressRelief":
                    tracks = getStressReliefTracks();
                    break;
                case "depressionHelp":
                    tracks = getDepressionHelpTracks();
                    break;
                case "sleepAid":
                    tracks = getSleepAidTracks();
                    break;
                default:
                    tracks = new ArrayList<>();
            }
            
            if (!tracks.contains(lastTrack)) {
                needsReset = true;
            }
        }
        
        if (needsReset) {
            clearAllPreferences();
        }
    }
} 