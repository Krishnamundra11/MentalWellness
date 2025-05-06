package com.krishna.navbar.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krishna.navbar.R;
import com.krishna.navbar.models.Song;
import com.krishna.navbar.models.Track;
import com.krishna.navbar.utils.FirebaseFavoritesManager;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private static final String TAG = "TrackAdapter";

    public interface OnTrackClickListener {
        void onTrackClick(int position);
    }
    
    public interface OnFavoriteClickListener {
        void onFavoriteClick(int position, boolean isFavorite);
    }

    private final List<Track> tracks;
    private final OnTrackClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    private final FirebaseFavoritesManager favoritesManager;

    public TrackAdapter(List<Track> tracks, OnTrackClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
        this.favoritesManager = FirebaseFavoritesManager.getInstance();
    }
    
    public TrackAdapter(List<Track> tracks, OnTrackClickListener listener, OnFavoriteClickListener favoriteListener) {
        this.tracks = tracks;
        this.listener = listener;
        this.favoriteListener = favoriteListener;
        this.favoritesManager = FirebaseFavoritesManager.getInstance();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.tvTrackTitle.setText(track.getTitle());
        holder.tvTrackArtist.setText(track.getArtist());
        holder.tvTrackDuration.setText(track.getDuration());
        
        // Initialize favorite button state from track model
        updateFavoriteButtonState(holder.btnFavorite, track.isFavorite());
        
        // Also check Firebase for current status
        checkFavoriteStatus(holder, track);
        
        holder.btnFavorite.setOnClickListener(v -> {
            // Create a Song object from track for Firebase
            Song song = new Song(track.getTitle(), track.getArtist(), "");
            
            // Get current state
            boolean currentlyFavorite = holder.btnFavorite.getTag() != null && 
                                       (boolean) holder.btnFavorite.getTag();
            
            Log.d(TAG, "Favorite button clicked for: " + track.getTitle() + ", current state: " + currentlyFavorite);
            
            // Toggle the favorite state in the track model
            track.setFavorite(!currentlyFavorite);
            
            // Immediately update UI for better responsiveness
            updateFavoriteButtonState(holder.btnFavorite, !currentlyFavorite);
            
            // Show immediate feedback via toast
            String message = !currentlyFavorite ? "Added to favorites" : "Removed from favorites";
            Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
            
            if (currentlyFavorite) {
                // If already favorite, remove from favorites
                Log.d(TAG, "Removing from favorites: " + track.getTitle());
                favoritesManager.removeFromFavorites(song, (updatedSong, isFavorite, exception) -> {
                    if (exception != null) {
                        // If operation failed, revert UI and track state
                        track.setFavorite(true);
                        Log.e(TAG, "Failed to remove from favorites: " + track.getTitle(), exception);
                        updateFavoriteButtonState(holder.btnFavorite, true);
                        
                        // Show error message
                        Toast.makeText(holder.itemView.getContext(), 
                            "Error removing from favorites", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Successfully removed from favorites: " + track.getTitle());
                        
                        // Notify listener
                        if (favoriteListener != null) {
                            favoriteListener.onFavoriteClick(position, false);
                        }
                    }
                });
            } else {
                // If not favorite, add to favorites
                Log.d(TAG, "Adding to favorites: " + track.getTitle());
                favoritesManager.addToFavorites(song, (updatedSong, isFavorite, exception) -> {
                    if (exception != null) {
                        // If operation failed, revert UI and track state
                        track.setFavorite(false);
                        Log.e(TAG, "Failed to add to favorites: " + track.getTitle(), exception);
                        updateFavoriteButtonState(holder.btnFavorite, false);
                        
                        // Show error message
                        Toast.makeText(holder.itemView.getContext(), 
                            "Error adding to favorites", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Successfully added to favorites: " + track.getTitle());
                        
                        // Notify listener
                        if (favoriteListener != null) {
                            favoriteListener.onFavoriteClick(position, true);
                        }
                    }
                });
            }
        });
        
        holder.fabPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(position);
            }
        });
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrackClick(position);
            }
        });
    }
    
    private void checkFavoriteStatus(TrackViewHolder holder, Track track) {
        // Create a Song object from track for Firebase
        Song song = new Song(track.getTitle(), track.getArtist(), "");
        
        // Check if this track is a favorite in Firebase
        favoritesManager.isFavorite(song, (checkedSong, isFavorite, exception) -> {
            if (exception == null) {
                Log.d(TAG, "Checked favorite status for: " + track.getTitle() + " = " + isFavorite);
                
                // Update Track model with the Firebase status
                track.setFavorite(isFavorite);
                
                // Update UI based on favorite status
                updateFavoriteButtonState(holder.btnFavorite, isFavorite);
            } else {
                Log.e(TAG, "Error checking favorite status for: " + track.getTitle(), exception);
            }
        });
    }
    
    private void updateFavoriteButtonState(ImageButton button, boolean isFavorite) {
        if (button != null && button.getContext() != null) {
            if (isFavorite) {
                button.setImageResource(android.R.drawable.btn_star_big_on);
                button.setColorFilter(button.getContext().getResources().getColor(android.R.color.holo_orange_light));
            } else {
                button.setImageResource(android.R.drawable.btn_star_big_off);
                button.setColorFilter(button.getContext().getResources().getColor(android.R.color.darker_gray));
            }
            // Store the current state in the button's tag
            button.setTag(isFavorite);
        }
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrackTitle;
        TextView tvTrackArtist;
        TextView tvTrackDuration;
        FloatingActionButton fabPlay;
        ImageButton btnFavorite;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrackTitle = itemView.findViewById(R.id.tvTrackTitle);
            tvTrackArtist = itemView.findViewById(R.id.tvTrackArtist);
            tvTrackDuration = itemView.findViewById(R.id.tvTrackDuration);
            fabPlay = itemView.findViewById(R.id.fabTrackPlay);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
} 