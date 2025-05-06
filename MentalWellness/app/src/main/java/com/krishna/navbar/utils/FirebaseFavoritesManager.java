package com.krishna.navbar.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.krishna.navbar.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to manage favorites in Firebase
 */
public class FirebaseFavoritesManager {
    private static final String TAG = "FirebaseFavoritesManager";
    
    private static FirebaseFavoritesManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    
    // Callbacks
    public interface OnFavoritesLoadedListener {
        void onFavoritesLoaded(List<Song> favorites, Exception exception);
    }
    
    public interface OnFavoriteStatusChangedListener {
        void onFavoriteStatusChanged(Song song, boolean isFavorite, Exception exception);
    }
    
    // Private constructor for singleton pattern
    private FirebaseFavoritesManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    
    // Get singleton instance
    public static synchronized FirebaseFavoritesManager getInstance() {
        if (instance == null) {
            instance = new FirebaseFavoritesManager();
        }
        return instance;
    }
    
    /**
     * Get the current user ID or null if not authenticated
     */
    @Nullable
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // For development/testing - use a mock user ID
            // In production, you would return null here
            Log.w(TAG, "No authenticated user, using mock user ID for development");
            return "mock_user_123";
        }
    }
    
    /**
     * Get reference to user favorites collection
     */
    public CollectionReference getUserFavoritesCollection() {
        String userId = getCurrentUserId();
        if (userId == null) {
            throw new IllegalStateException("User must be authenticated to access favorites");
        }
        Log.d(TAG, "Getting favorites collection for user: " + userId);
        return db.collection("users").document(userId).collection("favorites");
    }
    
    /**
     * Add a song to favorites
     */
    public void addToFavorites(Song song, final OnFavoriteStatusChangedListener listener) {
        try {
            // Make sure song has favorite flag set
            song.setFavorite(true);
            
            // Create a document ID using songTitle for simplicity
            String docId = song.getSongTitle().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            
            Log.d(TAG, "Adding song to favorites with ID: " + docId);
            Log.d(TAG, "Song data: title=" + song.getSongTitle() + ", artist=" + song.getArtistName() + ", favorite=" + song.isFavorite());
            
            getUserFavoritesCollection()
                .document(docId)
                .set(song.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "SUCCESS: Song added to Firebase favorites: " + docId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "ERROR: Failed to add song to Firebase favorites", e);
                })
                .addOnCompleteListener(task -> {
                    Exception exception = task.getException();
                    
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Song added to favorites: " + song.getSongTitle());
                        song.setId(docId);  // Set the document ID on the song
                    } else {
                        Log.e(TAG, "Error adding song to favorites", exception);
                    }
                    
                    if (listener != null) {
                        listener.onFavoriteStatusChanged(song, true, exception);
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Exception in addToFavorites", e);
            if (listener != null) {
                listener.onFavoriteStatusChanged(song, false, e);
            }
        }
    }
    
    /**
     * Remove a song from favorites
     */
    public void removeFromFavorites(Song song, final OnFavoriteStatusChangedListener listener) {
        try {
            // Create a document ID using songTitle for consistency
            String docId = song.getSongTitle().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            
            Log.d(TAG, "Removing song from favorites with ID: " + docId);
            
            getUserFavoritesCollection()
                .document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "SUCCESS: Song removed from Firebase favorites: " + docId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "ERROR: Failed to remove song from Firebase favorites", e);
                })
                .addOnCompleteListener(task -> {
                    Exception exception = task.getException();
                    
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Song removed from favorites: " + song.getSongTitle());
                        song.setFavorite(false);
                    } else {
                        Log.e(TAG, "Error removing song from favorites", exception);
                    }
                    
                    if (listener != null) {
                        listener.onFavoriteStatusChanged(song, false, exception);
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Exception in removeFromFavorites", e);
            if (listener != null) {
                listener.onFavoriteStatusChanged(song, false, e);
            }
        }
    }
    
    /**
     * Toggle favorite status for a song
     */
    public void toggleFavorite(Song song, final OnFavoriteStatusChangedListener listener) {
        boolean newFavoriteStatus = song.toggleFavorite();
        
        if (newFavoriteStatus) {
            addToFavorites(song, listener);
        } else {
            removeFromFavorites(song, listener);
        }
    }
    
    /**
     * Check if a song is in favorites
     */
    public void isFavorite(Song song, final OnFavoriteStatusChangedListener listener) {
        try {
            // Create a document ID using songTitle for consistency
            String docId = song.getSongTitle().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
            
            Log.d(TAG, "Checking if song is favorite with ID: " + docId);
            
            getUserFavoritesCollection()
                .document(docId)
                .get()
                .addOnCompleteListener(task -> {
                    Exception exception = task.getException();
                    boolean isFavorite = false;
                    
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        isFavorite = true;
                        song.setFavorite(true);
                        Log.d(TAG, "Song is a favorite: " + song.getSongTitle());
                    } else {
                        Log.d(TAG, "Song is not a favorite: " + song.getSongTitle());
                    }
                    
                    if (listener != null) {
                        listener.onFavoriteStatusChanged(song, isFavorite, exception);
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Exception in isFavorite", e);
            if (listener != null) {
                listener.onFavoriteStatusChanged(song, false, e);
            }
        }
    }
    
    /**
     * Get all favorite songs
     */
    public void getAllFavorites(final OnFavoritesLoadedListener listener) {
        try {
            Log.d(TAG, "Fetching all favorites...");
            String userId = getCurrentUserId();
            if (userId == null) {
                Log.e(TAG, "Error: No user ID available for fetching favorites");
                if (listener != null) {
                    listener.onFavoritesLoaded(new ArrayList<>(), new IllegalStateException("No user ID"));
                }
                return;
            }
            
            Log.d(TAG, "Fetching favorites for user: " + userId);
            
            getUserFavoritesCollection()
                .get()
                .addOnCompleteListener(task -> {
                    List<Song> favorites = new ArrayList<>();
                    Exception exception = task.getException();
                    
                    if (exception != null) {
                        Log.e(TAG, "Error getting favorites", exception);
                        if (listener != null) {
                            listener.onFavoritesLoaded(favorites, exception);
                        }
                        return;
                    }
                    
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot result = task.getResult();
                        Log.d(TAG, "Firebase returned " + result.size() + " favorite document(s)");
                        
                        if (result.isEmpty()) {
                            Log.d(TAG, "No favorites found - result is empty");
                            if (listener != null) {
                                listener.onFavoritesLoaded(favorites, null);
                            }
                            return;
                        }
                        
                        for (QueryDocumentSnapshot document : result) {
                            try {
                                Log.d(TAG, "Processing favorite document: " + document.getId());
                                
                                // Try extracting fields directly first for debugging
                                String title = document.getString("songTitle");
                                String artist = document.getString("artistName");
                                Log.d(TAG, "Raw document data - title: " + title + ", artist: " + artist);
                                
                                // Now convert to Song object
                                Song song = document.toObject(Song.class);
                                if (song != null) {
                                    song.setId(document.getId());
                                    song.setFavorite(true);  // Ensure favorite flag is set
                                    
                                    Log.d(TAG, "Successfully converted to Song: " + song.getSongTitle() + " by " + song.getArtistName());
                                    favorites.add(song);
                                } else {
                                    Log.e(TAG, "Failed to convert document to Song: " + document.getId());
                                    
                                    // Try manual creation if automatic conversion fails
                                    if (title != null && artist != null) {
                                        Log.d(TAG, "Creating song manually from fields");
                                        Song manualSong = new Song(title, artist, "");
                                        manualSong.setId(document.getId());
                                        manualSong.setFavorite(true);
                                        favorites.add(manualSong);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing favorite document: " + document.getId(), e);
                            }
                        }
                    } else {
                        Log.d(TAG, "Task was not successful or result is null");
                    }
                    
                    Log.d(TAG, "Returning " + favorites.size() + " favorites to listener");
                    if (listener != null) {
                        listener.onFavoritesLoaded(favorites, null);
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAllFavorites", e);
            if (listener != null) {
                listener.onFavoritesLoaded(new ArrayList<>(), e);
            }
        }
    }
    
    /**
     * Listen for real-time updates to user favorites
     */
    public void listenForFavoritesUpdates(final EventListener<QuerySnapshot> listener) {
        getUserFavoritesCollection()
            .addSnapshotListener(listener);
    }
    
    /**
     * For debugging: Dump all favorites to logcat
     */
    public void dumpFavoritesToLog() {
        try {
            Log.d(TAG, "=== DUMPING ALL FAVORITES TO LOG FOR DEBUGGING ===");
            String userId = getCurrentUserId();
            if (userId == null) {
                Log.e(TAG, "Cannot dump favorites: No current user ID");
                return;
            }
            
            Log.d(TAG, "Dumping favorites for user: " + userId);
            
            getUserFavoritesCollection()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No favorites found in Firebase for user: " + userId);
                    } else {
                        Log.d(TAG, "Found " + queryDocumentSnapshots.size() + " favorite(s) in Firebase");
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            try {
                                String docId = doc.getId();
                                String title = doc.getString("songTitle");
                                String artist = doc.getString("artistName");
                                boolean isFavorite = Boolean.TRUE.equals(doc.getBoolean("favorite"));
                                
                                Log.d(TAG, String.format("Favorite [%s]: title='%s', artist='%s', favorite=%b", 
                                    docId, title, artist, isFavorite));
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing document: " + doc.getId(), e);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> 
                    Log.e(TAG, "Error dumping favorites", e)
                );
        } catch (Exception e) {
            Log.e(TAG, "Exception in dumpFavoritesToLog", e);
        }
    }
    
    /**
     * Add a test favorite item to Firebase (for verification)
     */
    public void addTestFavorite(final OnFavoriteStatusChangedListener listener) {
        try {
            // Create test songs
            Song[] testSongs = {
                new Song("Test Favorite Song", "Test Artist", ""),
                new Song("Meditation Music", "Zen Master", ""),
                new Song("Rain Sounds", "Nature Vibes", "")
            };
            
            for (Song testSong : testSongs) {
                testSong.setFavorite(true);
                
                // Create a document ID
                String docId = testSong.getSongTitle().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
                
                Log.d(TAG, "Adding TEST song to favorites with ID: " + docId);
                
                getUserFavoritesCollection()
                    .document(docId)
                    .set(testSong.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "SUCCESS: Test song added to Firebase favorites: " + testSong.getSongTitle());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "ERROR: Failed to add test song to Firebase favorites: " + testSong.getSongTitle(), e);
                    });
            }
            
            // Call dumpFavoritesToLog to verify the songs were added
            dumpFavoritesToLog();
            
            // Notify listener about the last song (for simplicity)
            if (listener != null) {
                listener.onFavoriteStatusChanged(testSongs[testSongs.length-1], true, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in addTestFavorite", e);
            if (listener != null) {
                listener.onFavoriteStatusChanged(new Song(), false, e);
            }
        }
    }
} 