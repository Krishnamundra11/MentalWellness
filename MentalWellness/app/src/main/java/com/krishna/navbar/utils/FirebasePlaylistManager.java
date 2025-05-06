package com.krishna.navbar.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.krishna.navbar.models.Song;
import com.krishna.navbar.models.UserPlaylist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to manage playlists in Firebase
 */
public class FirebasePlaylistManager {
    private static final String TAG = "FirebasePlaylistManager";
    
    private static FirebasePlaylistManager instance;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final FirebaseAuth auth;
    
    // Callbacks
    public interface OnPlaylistsLoadedListener {
        void onPlaylistsLoaded(List<UserPlaylist> playlists, Exception exception);
    }
    
    public interface OnPlaylistSavedListener {
        void onPlaylistSaved(UserPlaylist playlist, Exception exception);
    }
    
    public interface OnPlaylistUpdatedListener {
        void onPlaylistUpdated(Exception exception);
    }
    
    public interface OnPlaylistDeletedListener {
        void onPlaylistDeleted(Exception exception);
    }
    
    public interface OnUrlLoadedListener {
        void onUrlLoaded(String url, Exception exception);
    }
    
    // Private constructor for singleton pattern
    private FirebasePlaylistManager() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    
    // Get singleton instance
    public static synchronized FirebasePlaylistManager getInstance() {
        if (instance == null) {
            instance = new FirebasePlaylistManager();
        }
        return instance;
    }
    
    /**
     * Get the current user ID or null if not authenticated
     */
    @Nullable
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    /**
     * Get reference to user playlists collection
     */
    public CollectionReference getUserPlaylistsCollection() {
        String userId = getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated to access playlists");
        }
        return db.collection("users").document(userId).collection("playlists");
    }
    
    /**
     * Save a playlist to Firestore
     */
    public void savePlaylist(UserPlaylist playlist, final OnPlaylistSavedListener listener) {
        getUserPlaylistsCollection()
            .add(playlist.toMap())
            .addOnCompleteListener(task -> {
                Exception exception = task.getException();
                
                if (task.isSuccessful() && task.getResult() != null) {
                    // Set the document ID on the playlist object
                    playlist.setId(task.getResult().getId());
                    Log.d(TAG, "Playlist saved with ID: " + playlist.getId());
                } else {
                    Log.e(TAG, "Error saving playlist", exception);
                }
                
                if (listener != null) {
                    listener.onPlaylistSaved(playlist, exception);
                }
            });
    }
    
    /**
     * Update an existing playlist in Firestore
     */
    public void updatePlaylist(UserPlaylist playlist, final OnPlaylistUpdatedListener listener) {
        if (playlist.getId() == null) {
            throw new IllegalArgumentException("Playlist must have an ID to update");
        }
        
        getUserPlaylistsCollection()
            .document(playlist.getId())
            .set(playlist.toMap())
            .addOnCompleteListener(task -> {
                Exception exception = task.getException();
                
                if (task.isSuccessful()) {
                    Log.d(TAG, "Playlist updated: " + playlist.getId());
                } else {
                    Log.e(TAG, "Error updating playlist", exception);
                }
                
                if (listener != null) {
                    listener.onPlaylistUpdated(exception);
                }
            });
    }
    
    /**
     * Delete a playlist from Firestore
     */
    public void deletePlaylist(UserPlaylist playlist, final OnPlaylistDeletedListener listener) {
        if (playlist.getId() == null) {
            throw new IllegalArgumentException("Playlist must have an ID to delete");
        }
        
        getUserPlaylistsCollection()
            .document(playlist.getId())
            .delete()
            .addOnCompleteListener(task -> {
                Exception exception = task.getException();
                
                if (task.isSuccessful()) {
                    Log.d(TAG, "Playlist deleted: " + playlist.getId());
                } else {
                    Log.e(TAG, "Error deleting playlist", exception);
                }
                
                if (listener != null) {
                    listener.onPlaylistDeleted(exception);
                }
            });
    }
    
    /**
     * Get all user playlists from Firestore
     */
    public void getAllPlaylists(final OnPlaylistsLoadedListener listener) {
        getUserPlaylistsCollection()
            .get()
            .addOnCompleteListener(task -> {
                List<UserPlaylist> playlists = new ArrayList<>();
                Exception exception = task.getException();
                
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserPlaylist playlist = convertDocumentToPlaylist(document);
                        if (playlist != null) {
                            playlists.add(playlist);
                        }
                    }
                    Log.d(TAG, "Retrieved " + playlists.size() + " playlists");
                } else {
                    Log.e(TAG, "Error getting playlists", exception);
                }
                
                if (listener != null) {
                    listener.onPlaylistsLoaded(playlists, exception);
                }
            });
    }

    /**
     * Listen for real-time updates to user playlists
     */
    public void listenForPlaylistUpdates(final EventListener<QuerySnapshot> listener) {
        getUserPlaylistsCollection()
            .addSnapshotListener(listener);
    }
    
    /**
     * Get a song's download URL from Firebase Storage
     */
    public void getSongDownloadUrl(String songPath, final OnUrlLoadedListener listener) {
        StorageReference songRef = storage.getReference("music").child(songPath);
        
        songRef.getDownloadUrl()
            .addOnCompleteListener(task -> {
                String url = null;
                Exception exception = task.getException();
                
                if (task.isSuccessful() && task.getResult() != null) {
                    url = task.getResult().toString();
                    Log.d(TAG, "Got download URL: " + url);
                } else {
                    Log.e(TAG, "Error getting download URL", exception);
                }
                
                if (listener != null) {
                    listener.onUrlLoaded(url, exception);
                }
            });
    }
    
    /**
     * Helper method to convert a Firestore document to a UserPlaylist
     */
    public UserPlaylist convertDocumentToPlaylist(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }
        
        try {
            String id = document.getId();
            String name = document.getString("name");
            String colorTheme = document.getString("colorTheme");
            
            UserPlaylist playlist = new UserPlaylist(name, colorTheme);
            playlist.setId(id);
            
            // Get createdAt timestamp
            if (document.getTimestamp("createdAt") != null) {
                playlist.setCreatedAt(document.getTimestamp("createdAt"));
            }
            
            // Get songs list
            List<Map<String, Object>> songsList = (List<Map<String, Object>>) document.get("songs");
            if (songsList != null) {
                for (Map<String, Object> songMap : songsList) {
                    String songTitle = (String) songMap.get("songTitle");
                    String artistName = (String) songMap.get("artistName");
                    String songUrl = (String) songMap.get("songUrl");
                    
                    Song song = new Song(songTitle, artistName, songUrl);
                    playlist.addSong(song);
                }
            }
            
            return playlist;
        } catch (Exception e) {
            Log.e(TAG, "Error converting document to playlist", e);
            return null;
        }
    }
} 