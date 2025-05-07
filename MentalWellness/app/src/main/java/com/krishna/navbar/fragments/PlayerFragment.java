package com.krishna.navbar.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import com.krishna.navbar.R;
import com.krishna.navbar.models.Track;
import com.krishna.navbar.utils.NavigationHelper;

import java.util.ArrayList;
import java.util.List;

public class PlayerFragment extends Fragment {

    private String trackName;
    private String artistName;
    private String playlistName;
    private int trackIndex;
    
    private TextView tvPlayerTitle;
    private TextView tvPlayerSubtitle;
    private TextView tvPlayerCategory;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private SeekBar seekBar;
    private ImageButton btnPlayPause;
    private ImageButton btnPrevious;
    private ImageButton btnNext;
    private ImageButton btnHeart;
    private ImageButton btnBack;
    
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    
    // Playlist of songs in res/raw
    private final int[] playlist = {
        R.raw.meditation_example,
        // Add more songs here as they are added to res/raw
    };
    
    // Add the progress update runnable
    private final Runnable progressUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int totalDuration = mediaPlayer.getDuration();
                
                // Update seekbar
                if (totalDuration > 0) {
                    int progress = (currentPosition * 100) / totalDuration;
                    seekBar.setProgress(progress);
                }
                
                // Update current time text
                updateTimeDisplay(currentPosition, tvCurrentTime);
                
                // Schedule next update
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        
        // Get track and playlist information from arguments
        if (getArguments() != null) {
            trackName = getArguments().getString("trackName", "Unknown Track");
            artistName = getArguments().getString("artistName", "Unknown Artist");
            playlistName = getArguments().getString("playlistName", "Playlist");
            trackIndex = getArguments().getInt("trackIndex", 0);
        }
        
        // Hide bottom navigation when this fragment is shown
        if (getActivity() != null) {
            View bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.GONE);
            }
        }
        
        setupUI(view);
        setupMediaPlayer();
        setupListeners();
        
        return view;
    }

    private void setupUI(View view) {
        // Setup views
        tvPlayerTitle = view.findViewById(R.id.tvPlayerTitle);
        tvPlayerSubtitle = view.findViewById(R.id.tvPlayerSubtitle);
        tvPlayerCategory = view.findViewById(R.id.tvPlayerCategory);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        seekBar = view.findViewById(R.id.seekBar);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        btnHeart = view.findViewById(R.id.btnHeart);
        btnBack = view.findViewById(R.id.btnBack);
        
        // Get the album art
        ImageView ivAlbumArt = view.findViewById(R.id.ivAlbumArt);
        
        // Set track information
        tvPlayerTitle.setText(trackName);
        getSubtitle();
        tvPlayerCategory.setText(playlistName);
        
        // Apply color theme based on playlist
        int tintColor = getResources().getColor(R.color.colorOrange); // default
        switch (playlistName) {
            case "Finding Calm":
                tintColor = getResources().getColor(R.color.colorBlue);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_blue_background);
                break;
            case "Spiritual":
                tintColor = getResources().getColor(R.color.colorYellow);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_yellow_background);
                break;
            case "Motivation":
                tintColor = getResources().getColor(R.color.colorMint);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_mint_background);
                break;
            case "Breathe":
                tintColor = getResources().getColor(R.color.colorPink);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_pink_background);
                break;
            case "Mindfulness":
                tintColor = getResources().getColor(R.color.colorPurple);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_purple_background);
                break;
            case "Sleep Well":
                tintColor = getResources().getColor(R.color.colorBlueDark);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_blue_dark_background);
                break;
            case "Healing":
                tintColor = getResources().getColor(R.color.colorOrange);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_orange_background);
                break;
            case "Anxiety Relief":
                tintColor = getResources().getColor(R.color.colorGreen);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_green_background);
                break;
            case "Positive Energy":
                tintColor = getResources().getColor(R.color.colorRed);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_red_background);
                break;
            case "Stress Relief":
                tintColor = getResources().getColor(R.color.colorTeal);
                ivAlbumArt.setColorFilter(tintColor);
                btnPlayPause.setBackgroundResource(R.drawable.player_play_button_teal_background);
                break;
            default:
                tintColor = getResources().getColor(R.color.colorOrange);
                ivAlbumArt.setColorFilter(tintColor);
                break;
        }
        
        // Set the color for the category text
        tvPlayerCategory.setTextColor(tintColor);
        
        // Set total time from track
        Track currentTrack = getTrackFromPlaylist();
        if (currentTrack != null) {
            tvTotalTime.setText(currentTrack.getDuration());
        }
        
        // Setup seekbar
        seekBar.setMax(100); // Use 0-100 for simplicity
        seekBar.setProgress(0);
        
        // Setup button listeners
        btnPlayPause.setOnClickListener(v -> togglePlayback());
        btnPrevious.setOnClickListener(v -> playPreviousTrack());
        btnNext.setOnClickListener(v -> playNextTrack());
        btnHeart.setOnClickListener(v -> toggleFavorite());
        btnBack.setOnClickListener(v -> navigateBack());
    }

    private Track getTrackFromPlaylist() {
        List<Track> tracks = getTrackListForPlaylist();
        if (trackIndex >= 0 && trackIndex < tracks.size()) {
            return tracks.get(trackIndex);
        }
        return null;
    }
    
    private void getSubtitle() {
        // Set a motivational subtitle based on the track name
        String subtitle;
        
        if (trackName.contains("Rise") || trackName.contains("Conquer")) {
            subtitle = "Embrace your journey—each step forward brings new possibilities";
        } else if (trackName.contains("Power") || trackName.contains("Fearless")) {
            subtitle = "Your strength is within—unleash your true potential";
        } else if (trackName.contains("Calm") || trackName.contains("Peace")) {
            subtitle = "Find serenity in stillness—peace begins within";
        } else if (trackName.contains("Breath") || trackName.contains("Breathing")) {
            subtitle = "With each breath, find your center and restore balance";
        } else {
            subtitle = "Let the music guide you to a place of mental wellness";
        }
        
        tvPlayerSubtitle.setText(subtitle);
    }
    
    private List<Track> getTrackListForPlaylist() {
        // Create a list of tracks based on the playlist name (same logic as in PlaylistFragment)
        List<Track> tracks = new ArrayList<>();
        
        switch (playlistName) {
            case "Motivation":
                tracks.add(new Track("Rise and Conquer", "InspiroTunes", "3:45"));
                tracks.add(new Track("Limitless Energy", "MindBoost", "4:02"));
                tracks.add(new Track("Fearless Spirit", "Peak Soundscapes", "3:30"));
                tracks.add(new Track("Positive Mindset", "InspiroTunes", "4:15"));
                tracks.add(new Track("Breakthrough", "MindBoost", "3:22"));
                // Add more tracks...
                break;
                
            case "Finding Calm":
                tracks.add(new Track("Ocean Waves", "Nature Sounds", "5:10"));
                tracks.add(new Track("Gentle Rain", "Ambient Dreams", "4:45"));
                tracks.add(new Track("Mountain Stream", "Nature Sounds", "4:20"));
                // Add more tracks...
                break;
                
            // Add cases for other playlists...
                
            default:
                // Default track if not in a recognized playlist
                tracks.add(new Track(trackName, artistName, "3:30"));
                break;
        }
        
        return tracks;
    }
    
    private void setupMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(getContext(), playlist[trackIndex]);
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                updatePlayPauseButton();
                playNextTrack();
            });
            
            // Set total duration
            int totalDuration = mediaPlayer.getDuration();
            updateTimeDisplay(totalDuration, tvTotalTime);
            
            // Start playing automatically
            startPlayback();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error initializing audio player", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            NavigationHelper.handleBackNavigation(this);
        });

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                pausePlayback();
            } else {
                startPlayback();
            }
        });

        btnNext.setOnClickListener(v -> {
            playNextTrack();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    int totalDuration = mediaPlayer.getDuration();
                    int newPosition = (progress * totalDuration) / 100;
                    mediaPlayer.seekTo(newPosition);
                    updateTimeDisplay(newPosition, tvCurrentTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause progress updates while seeking
                handler.removeCallbacks(progressUpdateRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume progress updates after seeking
                if (isPlaying) {
                    handler.post(progressUpdateRunnable);
                }
            }
        });
    }

    private void togglePlayback() {
        if (isPlaying) {
            pausePlayback();
        } else {
            startPlayback();
        }
    }

    private void startPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            updatePlayPauseButton();
            startProgressUpdate();
        }
    }

    private void pausePlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            updatePlayPauseButton();
            handler.removeCallbacks(progressUpdateRunnable);
        }
    }

    private void startProgressUpdate() {
        handler.removeCallbacks(progressUpdateRunnable);
        progressUpdateRunnable.run();
    }

    private void updateTimeDisplay(int milliseconds, TextView textView) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        textView.setText(String.format("%d:%02d", minutes, seconds));
    }

    private void updatePlayPauseButton() {
        btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }
    
    private void playPreviousTrack() {
        // Stop current playback timer
        handler.removeCallbacks(progressUpdateRunnable);
        
        // Move to previous track
        if (trackIndex > 0) {
            trackIndex--;
            updateTrackInfo();
        } else {
            // Wrap around to the last track
            List<Track> tracks = getTrackListForPlaylist();
            trackIndex = tracks.size() - 1;
            updateTrackInfo();
        }
    }
    
    private void playNextTrack() {
        // Stop current playback timer
        handler.removeCallbacks(progressUpdateRunnable);
        
        // Move to next track
        List<Track> tracks = getTrackListForPlaylist();
        if (trackIndex < tracks.size() - 1) {
            trackIndex++;
            updateTrackInfo();
        } else {
            // Wrap around to the first track
            trackIndex = 0;
            updateTrackInfo();
        }
    }
    
    private void updateTrackInfo() {
        // Get new track info
        Track track = getTrackFromPlaylist();
        if (track != null) {
            trackName = track.getTitle();
            artistName = track.getArtist();
            
            // Update UI
            tvPlayerTitle.setText(trackName);
            getSubtitle();
            tvTotalTime.setText(track.getDuration());
            
            // Reset progress
            seekBar.setProgress(0);
            tvCurrentTime.setText("0:00");
            
            // Start playback of new track
            startPlayback();
        }
    }
    
    private void toggleFavorite() {
        // In a real app, this would add/remove the track from favorites
        // For this demo, we'll just toggle the button appearance
        boolean isFavorite = btnHeart.getTag() != null && (boolean) btnHeart.getTag();
        isFavorite = !isFavorite;
        
        btnHeart.setTag(isFavorite);
        btnHeart.setColorFilter(isFavorite ? 
                getResources().getColor(android.R.color.holo_red_light) : 
                getResources().getColor(R.color.black));
    }
    
    private void navigateBack() {
        // Stop playback and clean up
        isPlaying = false;
        handler.removeCallbacks(progressUpdateRunnable);
        
        // Go back to previous fragment
        getParentFragmentManager().popBackStack();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Pause playback when fragment is paused
        if (isPlaying) {
            pausePlayback();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up MediaPlayer resources
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        // Remove any pending callbacks
        handler.removeCallbacks(progressUpdateRunnable);
        
        // Restore bottom navigation visibility
        if (getActivity() != null && !isRemoving()) {
            View bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.VISIBLE);
            }
        }
    }
} 