package com.krishna.navbar.utils;

import com.krishna.navbar.models.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to manage playlists
 */
public class PlaylistManager {
    private static PlaylistManager instance;
    private final Map<String, List<Track>> playlists = new HashMap<>();
    
    // Get singleton instance
    public static PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }
    
    // Private constructor
    private PlaylistManager() {
        // Initialize with default playlists
        initDefaultPlaylists();
    }
    
    /**
     * Initialize the default playlists
     */
    private void initDefaultPlaylists() {
        // Finding Calm playlist
        List<Track> calmTracks = new ArrayList<>();
        calmTracks.add(new Track("Ocean Waves", "Nature Sounds", "5:10"));
        calmTracks.add(new Track("Gentle Rain", "Ambient Dreams", "4:45"));
        calmTracks.add(new Track("Mountain Stream", "Nature Sounds", "4:20"));
        calmTracks.add(new Track("Peaceful Forest", "Ambient Dreams", "5:30"));
        calmTracks.add(new Track("Evening Calm", "Deep Relax", "6:15"));
        playlists.put("Finding Calm", calmTracks);
        
        // Spiritual playlist
        List<Track> spiritualTracks = new ArrayList<>();
        spiritualTracks.add(new Track("Sacred Om", "Meditation Masters", "7:20"));
        spiritualTracks.add(new Track("Inner Temple", "Soul Harmony", "6:30"));
        spiritualTracks.add(new Track("Divine Energy", "Meditation Masters", "5:45"));
        spiritualTracks.add(new Track("Soul Journey", "Soul Harmony", "8:10"));
        spiritualTracks.add(new Track("Healing Mantra", "Spiritual Guides", "6:20"));
        playlists.put("Spiritual", spiritualTracks);
        
        // Motivation playlist
        List<Track> motivationTracks = new ArrayList<>();
        motivationTracks.add(new Track("Rise and Conquer", "InspiroTunes", "3:45"));
        motivationTracks.add(new Track("Limitless Energy", "MindBoost", "4:02"));
        motivationTracks.add(new Track("Fearless Spirit", "Peak Soundscapes", "3:30"));
        motivationTracks.add(new Track("Positive Mindset", "InspiroTunes", "4:15"));
        motivationTracks.add(new Track("Breakthrough", "MindBoost", "3:22"));
        motivationTracks.add(new Track("Inner Power", "Peak Soundscapes", "3:50"));
        playlists.put("Motivation", motivationTracks);
        
        // Breathe playlist
        List<Track> breatheTracks = new ArrayList<>();
        breatheTracks.add(new Track("Deep Breath", "Breath Works", "4:33"));
        breatheTracks.add(new Track("4-7-8 Technique", "Breath Masters", "5:15"));
        breatheTracks.add(new Track("Mindful Breathing", "Breath Works", "6:40"));
        breatheTracks.add(new Track("Cleansing Breath", "Breath Masters", "4:20"));
        breatheTracks.add(new Track("Stress Release", "Calm Mind", "5:50"));
        playlists.put("Breathe", breatheTracks);
    }
    
    /**
     * Get tracks for a playlist
     * @param playlistName The name of the playlist
     * @return List of tracks in the playlist, or empty list if playlist doesn't exist
     */
    public List<Track> getPlaylistTracks(String playlistName) {
        if (playlists.containsKey(playlistName)) {
            return new ArrayList<>(playlists.get(playlistName));
        }
        return new ArrayList<>();
    }
    
    /**
     * Create a new playlist
     * @param playlistName Name of the new playlist
     * @param tracks List of tracks to add to the playlist
     * @return True if the playlist was created, false if a playlist with that name already exists
     */
    public boolean createPlaylist(String playlistName, List<Track> tracks) {
        if (playlists.containsKey(playlistName)) {
            return false;
        }
        
        playlists.put(playlistName, new ArrayList<>(tracks));
        return true;
    }
    
    /**
     * Add a track to a playlist
     * @param playlistName The playlist to add the track to
     * @param track The track to add
     * @return True if the track was added, false if the playlist doesn't exist
     */
    public boolean addTrackToPlaylist(String playlistName, Track track) {
        if (!playlists.containsKey(playlistName)) {
            return false;
        }
        
        List<Track> playlistTracks = playlists.get(playlistName);
        playlistTracks.add(track);
        return true;
    }
    
    /**
     * Remove a track from a playlist
     * @param playlistName The playlist to remove the track from
     * @param track The track to remove
     * @return True if the track was removed, false if the playlist doesn't exist or the track wasn't found
     */
    public boolean removeTrackFromPlaylist(String playlistName, Track track) {
        if (!playlists.containsKey(playlistName)) {
            return false;
        }
        
        List<Track> playlistTracks = playlists.get(playlistName);
        for (int i = 0; i < playlistTracks.size(); i++) {
            Track t = playlistTracks.get(i);
            if (t.getTitle().equals(track.getTitle()) && t.getArtist().equals(track.getArtist())) {
                playlistTracks.remove(i);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get all playlist names
     * @return List of playlist names
     */
    public List<String> getAllPlaylistNames() {
        return new ArrayList<>(playlists.keySet());
    }
    
    /**
     * Delete a playlist
     * @param playlistName The playlist to delete
     * @return True if the playlist was deleted, false if it doesn't exist
     */
    public boolean deletePlaylist(String playlistName) {
        if (!playlists.containsKey(playlistName)) {
            return false;
        }
        
        playlists.remove(playlistName);
        return true;
    }
} 