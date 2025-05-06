package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Rect;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.krishna.navbar.R;
import com.krishna.navbar.adapters.TrackAdapter;
import com.krishna.navbar.models.Song;
import com.krishna.navbar.models.Track;
import com.krishna.navbar.utils.FirebaseFavoritesManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private static final String TAG = "FavoritesFragment";

    private RecyclerView rvFavoriteTracks;
    private TextView tvEmptyState;
    private FirebaseFavoritesManager favoritesManager;
    private TrackAdapter adapter;
    private List<Track> favoriteTracks = new ArrayList<>();
    private boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating FavoritesFragment view");
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        setupUI(view);
        return view;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing FavoritesFragment");
        favoritesManager = FirebaseFavoritesManager.getInstance();
    }
    
    @Override 
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: FavoritesFragment view created");
        
        // Immediate refresh after view is created
        refreshFavorites();
        
        // Debug message for user
        Toast.makeText(getContext(), "Loading your favorites...", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: FavoritesFragment resumed");
        
        // Always refresh favorites when fragment is resumed
        refreshFavorites();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: FavoritesFragment started");
        
        // Set up real-time listener for favorites changes
        setupFavoritesListener();
    }

    /**
     * Public method to refresh favorites from outside the fragment
     */
    public void refreshFavorites() {
        Log.d(TAG, "refreshFavorites: Refreshing favorites for display");
        try {
            if (!isAdded()) {
                Log.e(TAG, "refreshFavorites: Fragment not attached to activity, cannot refresh");
                return;
            }
            
            // Dump favorites to log for debugging
            favoritesManager.dumpFavoritesToLog();
            
            // Clear previous tracks before loading
            favoriteTracks.clear();
            
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                Log.d(TAG, "refreshFavorites: Notified adapter after clearing tracks");
            } else {
                Log.e(TAG, "refreshFavorites: Adapter is null, cannot notify");
            }
            
            // Load favorites from Firebase
            loadFavoritesFromFirebase();
        } catch (Exception e) {
            Log.e(TAG, "refreshFavorites: Error refreshing favorites", e);
            if (getContext() != null && isAdded()) {
                Toast.makeText(getContext(), "Error refreshing favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupUI(View view) {
        Log.d(TAG, "setupUI: Setting up FavoritesFragment UI");
        rvFavoriteTracks = view.findViewById(R.id.rvFavoriteTracks);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        if (rvFavoriteTracks == null) {
            Log.e(TAG, "setupUI: rvFavoriteTracks is null! Check fragment_favorites.xml");
            return;
        }
        
        if (tvEmptyState == null) {
            Log.e(TAG, "setupUI: tvEmptyState is null! Check fragment_favorites.xml");
            return;
        }

        // Initialize RecyclerView with a proper layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvFavoriteTracks.setLayoutManager(layoutManager);
        rvFavoriteTracks.setHasFixedSize(true);
        
        // Add some item decoration for better spacing
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_small);
        if (spacingInPixels == 0) spacingInPixels = 8; // Fallback if dimension not found
        
        // Remove any existing item decorations to avoid duplicates
        for (int i = 0; i < rvFavoriteTracks.getItemDecorationCount(); i++) {
            rvFavoriteTracks.removeItemDecorationAt(i);
        }
        rvFavoriteTracks.addItemDecoration(new SpacingItemDecoration(spacingInPixels));
        
        // Initialize adapter with empty list
        adapter = new TrackAdapter(favoriteTracks, 
            // Play track when clicked
            position -> {
                if (position >= 0 && position < favoriteTracks.size()) {
                    Track track = favoriteTracks.get(position);
                    playTrack(track.getTitle(), track.getArtist());
                }
            },
            // Favorite clicked - update UI immediately
            (position, isFavorite) -> {
                if (position >= 0 && position < favoriteTracks.size()) {
                    Track track = favoriteTracks.get(position);
                    
                    // Show toast message
                    String message = isFavorite ? 
                        "Added to favorites" : 
                        "Removed from favorites";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    
                    // If unfavorited, remove from the list immediately
                    if (!isFavorite) {
                        favoriteTracks.remove(position);
                        adapter.notifyItemRemoved(position);
                        updateEmptyStateVisibility();
                    }
                }
            }
        );
        
        // Set adapter to RecyclerView
        rvFavoriteTracks.setAdapter(adapter);
        Log.d(TAG, "setupUI: Adapter set on RecyclerView with " + favoriteTracks.size() + " items");
        
        // Make sure the empty state is initially visible since the list is empty
        updateEmptyStateVisibility();
    }
    
    private void loadFavoritesFromFirebase() {
        try {
            // Log attempt to load
            Log.d(TAG, "loadFavoritesFromFirebase: Attempting to load favorites from Firebase...");
            
            // Check if user is authenticated
            String userId = favoritesManager.getCurrentUserId();
            if (userId == null) {
                Log.e(TAG, "loadFavoritesFromFirebase: User not authenticated, cannot load favorites");
                if (getContext() != null && isAdded()) {
                    Toast.makeText(getContext(), "Please sign in to see your favorites", Toast.LENGTH_SHORT).show();
                }
                updateEmptyStateVisibility();
                return;
            }
            
            Log.d(TAG, "loadFavoritesFromFirebase: Fetching favorites for user: " + userId);
            
            // For development testing - add test favorite on first load
            if (isFirstLoad) {
                Log.d(TAG, "loadFavoritesFromFirebase: First load, adding test favorite");
                favoritesManager.addTestFavorite(null);
                isFirstLoad = false;
            }
            
            // Get all favorites from Firebase
            favoritesManager.getAllFavorites((favorites, exception) -> {
                if (!isAdded()) {
                    Log.e(TAG, "loadFavoritesFromFirebase: Fragment not attached to activity, cannot update UI");
                    return;
                }
                
                if (exception != null) {
                    Log.e(TAG, "loadFavoritesFromFirebase: Error loading favorites", exception);
                    if (getContext() != null && isAdded()) {
                        Toast.makeText(getContext(), "Error loading favorites", Toast.LENGTH_SHORT).show();
                    }
                    updateEmptyStateVisibility();
                    return;
                }
                
                // Display count in log for debugging
                Log.d(TAG, "loadFavoritesFromFirebase: Retrieved " + favorites.size() + " favorites from Firebase");
                
                // Clear previous data to avoid duplicates
                favoriteTracks.clear();
                
                if (favorites.isEmpty()) {
                    Log.d(TAG, "loadFavoritesFromFirebase: No favorites found in Firebase");
                    // Update UI on empty results
                    if (getActivity() != null && isAdded()) {
                        getActivity().runOnUiThread(() -> {
                            updateEmptyStateVisibility();
                            tvEmptyState.setText("No favorites found. Star some songs to add them here!");
                        });
                    }
                    return;
                }
                
                // For each song in favorites, create a Track and add to the list
                for (Song song : favorites) {
                    // Log details for debugging
                    Log.d(TAG, "loadFavoritesFromFirebase: Adding favorite: " + 
                          "Title = " + song.getSongTitle() + 
                          ", Artist = " + song.getArtistName() +
                          ", ID = " + song.getId());
                    
                    // Create track with favorite status and add to list
                    Track track = new Track(song.getSongTitle(), song.getArtistName(), "3:30", true);
                    favoriteTracks.add(track);
                }
                
                // Update adapter on UI thread
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        if (adapter != null) {
                            // Check if RecyclerView is visible
                            if (rvFavoriteTracks.getVisibility() != View.VISIBLE) {
                                rvFavoriteTracks.setVisibility(View.VISIBLE);
                            }
                            
                            // Update adapter
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "loadFavoritesFromFirebase: Adapter notified with " + favoriteTracks.size() + " tracks");
                            
                            // Show success message
                            if (favoriteTracks.size() > 0) {
                                Toast.makeText(getContext(), 
                                    favoriteTracks.size() + " favorite songs loaded", 
                                    Toast.LENGTH_SHORT).show();
                            }
                            
                            // Force update to RecyclerView
                            rvFavoriteTracks.post(() -> {
                                Log.d(TAG, "loadFavoritesFromFirebase: Forcing RecyclerView layout refresh");
                                adapter.notifyDataSetChanged();
                                rvFavoriteTracks.scrollToPosition(0);
                            });
                        } else {
                            Log.e(TAG, "loadFavoritesFromFirebase: Adapter is null, cannot notify");
                        }
                        
                        // Update visibility of empty state vs. list
                        updateEmptyStateVisibility();
                    });
                } else {
                    Log.e(TAG, "loadFavoritesFromFirebase: Activity is null or fragment not attached, cannot update UI");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "loadFavoritesFromFirebase: Error loading favorites", e);
            if (getContext() != null && isAdded()) {
                Toast.makeText(getContext(), "Error loading favorites", Toast.LENGTH_SHORT).show();
            }
            updateEmptyStateVisibility();
        }
    }
    
    private void setupFavoritesListener() {
        try {
            Log.d(TAG, "setupFavoritesListener: Setting up real-time listener for favorites");
            favoritesManager.listenForFavoritesUpdates(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, "setupFavoritesListener: Error listening for favorites updates", error);
                        return;
                    }
                    
                    Log.d(TAG, "setupFavoritesListener: Received update from Firebase");
                    
                    // Reload favorites when changes occur
                    if (isAdded()) {  // Check if fragment is still attached
                        Log.d(TAG, "setupFavoritesListener: Fragment is attached, reloading favorites");
                        loadFavoritesFromFirebase();
                    } else {
                        Log.e(TAG, "setupFavoritesListener: Fragment not attached, cannot reload favorites");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "setupFavoritesListener: Error setting up favorites listener", e);
        }
    }
    
    private void updateEmptyStateVisibility() {
        try {
            if (tvEmptyState == null || rvFavoriteTracks == null) {
                Log.e(TAG, "updateEmptyStateVisibility: Views are null");
                return;
            }
            
            if (favoriteTracks.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvFavoriteTracks.setVisibility(View.GONE);
                Log.d(TAG, "updateEmptyStateVisibility: Empty state shown (list is empty)");
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvFavoriteTracks.setVisibility(View.VISIBLE);
                Log.d(TAG, "updateEmptyStateVisibility: RecyclerView shown with " + favoriteTracks.size() + " items");
            }
        } catch (Exception e) {
            Log.e(TAG, "updateEmptyStateVisibility: Error updating UI visibility", e);
        }
    }
    
    private void playTrack(String trackName, String artistName) {
        // Navigate to player fragment
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("trackName", trackName);
        args.putString("artistName", artistName);
        playerFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.con, playerFragment)
                .addToBackStack(null)
                .commit();
    }

    // Simple item decoration for spacing between items
    private class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spacing;

        public SpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, 
                                  @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = spacing;
        }
    }
} 