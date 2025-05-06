package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.krishna.navbar.R;
import com.krishna.navbar.models.Song;
import com.krishna.navbar.models.UserPlaylist;
import com.krishna.navbar.utils.FirebasePlaylistManager;
import com.krishna.navbar.utils.MediaPlayerManager;

import java.util.List;

/**
 * Fragment for playing music from Firebase Storage
 */
public class FirebasePlayerFragment extends Fragment {
    private static final String TAG = "FirebasePlayerFragment";
    
    private String playlistId;
    private int trackIndex;
    private UserPlaylist currentPlaylist;
    private List<Song> songs;
    
    // Firebase components
    private FirebasePlaylistManager playlistManager;
    
    // Media player components
    private MediaPlayerManager mediaPlayerManager;
    
    // UI components
    private TextView tvSongTitle;
    private TextView tvArtistName;
    private ImageView ivPlaylistArt;
    private ImageButton btnPlay;
    private ImageButton btnPrevious;
    private ImageButton btnNext;
    private ImageButton btnBack;
    private SeekBar seekBar;
    private TextView tvCurrentPosition;
    private TextView tvDuration;
    
    // State variables
    private boolean isPlaying = false;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get arguments
        if (getArguments() != null) {
            playlistId = getArguments().getString("playlistId");
            trackIndex = getArguments().getInt("trackIndex", 0);
        }
        
        // Initialize managers
        playlistManager = FirebasePlaylistManager.getInstance();
        
        // Initialize MediaPlayerManager if not already done
        if (getActivity() != null) {
            MediaPlayerManager.initialize(getActivity());
            mediaPlayerManager = MediaPlayerManager.getInstance();
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firebase_player, container, false);
        
        // Initialize UI components
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvArtistName = view.findViewById(R.id.tvArtistName);
        ivPlaylistArt = view.findViewById(R.id.ivPlaylistArt);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        btnBack = view.findViewById(R.id.btnBack);
        seekBar = view.findViewById(R.id.seekBar);
        tvCurrentPosition = view.findViewById(R.id.tvCurrentPosition);
        tvDuration = view.findViewById(R.id.tvDuration);
        
        // Set up click listeners
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnPlay.setOnClickListener(v -> togglePlayback());
        btnPrevious.setOnClickListener(v -> playPreviousTrack());
        btnNext.setOnClickListener(v -> playNextTrack());
        
        // Connect seekbar to media player
        mediaPlayerManager.setSeekBar(seekBar);
        
        // Set up completion listener
        mediaPlayerManager.setOnCompletionListener(new MediaPlayerManager.OnCompletionListener() {
            @Override
            public void onCompletion() {
                isPlaying = false;
                updatePlayButton();
                
                // Auto-play next track
                playNextTrack();
            }
        });
        
        // Load playlist and start playback
        loadPlaylistAndStartPlayback();
        
        return view;
    }
    
    private void loadPlaylistAndStartPlayback() {
        if (playlistId == null || playlistId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid playlist ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get specific playlist document by ID
        playlistManager.getUserPlaylistsCollection()
            .document(playlistId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    // Convert document to playlist
                    DocumentSnapshot document = task.getResult();
                    currentPlaylist = playlistManager.convertDocumentToPlaylist(document);
                    
                    if (currentPlaylist != null) {
                        songs = currentPlaylist.getSongs();
                        
                        if (songs != null && !songs.isEmpty()) {
                            // Set up the player UI with the playlist info
                            updatePlayerUI();
                            
                            // Start playing the selected track
                            playSongAtIndex(trackIndex);
                        } else {
                            Toast.makeText(getContext(), "No songs in this playlist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error loading playlist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Playlist not found", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading playlist", task.getException());
                }
            });
    }
    
    private void updatePlayerUI() {
        // Set the playlist title and other UI elements based on playlist theme
        if (currentPlaylist != null) {
            ivPlaylistArt.setBackgroundResource(currentPlaylist.getBackgroundResourceId());
            
            // Update the song info for the current track
            updateTrackInfo();
        }
    }
    
    private void updateTrackInfo() {
        if (songs != null && trackIndex >= 0 && trackIndex < songs.size()) {
            Song currentSong = songs.get(trackIndex);
            
            tvSongTitle.setText(currentSong.getSongTitle());
            tvArtistName.setText(currentSong.getArtistName());
            
            // Update play/pause button state
            updatePlayButton();
            
            // Update navigation button states
            btnPrevious.setEnabled(trackIndex > 0);
            btnPrevious.setAlpha(trackIndex > 0 ? 1.0f : 0.5f);
            
            btnNext.setEnabled(trackIndex < songs.size() - 1);
            btnNext.setAlpha(trackIndex < songs.size() - 1 ? 1.0f : 0.5f);
        }
    }
    
    private void playSongAtIndex(int index) {
        if (songs == null || songs.isEmpty() || index < 0 || index >= songs.size()) {
            return;
        }
        
        // Update track index
        trackIndex = index;
        
        // Get the current song
        Song currentSong = songs.get(trackIndex);
        String songUrl = currentSong.getSongUrl();
        
        if (songUrl == null || songUrl.isEmpty()) {
            Toast.makeText(getContext(), "Song URL is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get the download URL from Firebase Storage
        playlistManager.getSongDownloadUrl(songUrl, new FirebasePlaylistManager.OnUrlLoadedListener() {
            @Override
            public void onUrlLoaded(String url, Exception exception) {
                if (exception == null && url != null) {
                    // Play the song with the download URL
                    mediaPlayerManager.playUrl(url, 
                        new MediaPlayerManager.OnPreparedListener() {
                            @Override
                            public void onPrepared() {
                                // Start playback when prepared
                                mediaPlayerManager.playPause();
                                isPlaying = true;
                                updatePlayButton();
                                
                                // Update the UI with song information
                                updateTrackInfo();
                            }
                        }, 
                        new MediaPlayerManager.OnErrorListener() {
                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(getContext(), "Error playing song: " + errorMessage, Toast.LENGTH_SHORT).show();
                                isPlaying = false;
                                updatePlayButton();
                            }
                        }
                    );
                } else {
                    Toast.makeText(getContext(), "Error loading song URL", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error getting download URL", exception);
                }
            }
        });
    }
    
    private void togglePlayback() {
        if (mediaPlayerManager.isPrepared()) {
            mediaPlayerManager.playPause();
            isPlaying = mediaPlayerManager.isPlaying();
            updatePlayButton();
        } else if (songs != null && !songs.isEmpty()) {
            // If media player isn't prepared, try to play the current track
            playSongAtIndex(trackIndex);
        }
    }
    
    private void updatePlayButton() {
        btnPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }
    
    private void playPreviousTrack() {
        if (trackIndex > 0) {
            playSongAtIndex(trackIndex - 1);
        }
    }
    
    private void playNextTrack() {
        if (songs != null && trackIndex < songs.size() - 1) {
            playSongAtIndex(trackIndex + 1);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        // Pause playback when fragment is paused
        if (mediaPlayerManager != null && mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.playPause();
            isPlaying = false;
            updatePlayButton();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Release media player resources when fragment is destroyed
        if (mediaPlayerManager != null) {
            mediaPlayerManager.releaseMediaPlayer();
        }
    }
} 