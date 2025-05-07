package com.krishna.navbar.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krishna.navbar.R;
import com.krishna.navbar.utils.MusicPreferencesManager;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DepressionHelpActivity extends AppCompatActivity {

    // Session duration in milliseconds (20 minutes)
    private static final long SESSION_DURATION_MS = TimeUnit.MINUTES.toMillis(20);
    private static final String CATEGORY_DEPRESSION_HELP = "depressionHelp";
    
    // Meditation instructions
    private static final String[] MEDITATION_INSTRUCTIONS = {
        "Find a peaceful space where you feel safe",
        "Sit comfortably and close your eyes",
        "Take a few deep, calming breaths",
        "Notice how your body feels in this moment",
        "Acknowledge any difficult emotions without judgment",
        "Remember that this feeling is temporary",
        "Focus on the sensation of your breath",
        "Feel the support beneath you",
        "Notice any positive sensations in your body",
        "Bring to mind a happy memory",
        "Feel the warmth of that memory",
        "Let that positive feeling grow",
        "Know that you are not alone",
        "Feel the strength within you",
        "Gently bring your awareness back",
        "Take a moment to appreciate yourself",
        "Notice any small moments of peace",
        "Feel the healing energy around you",
        "Know that better days are ahead",
        "Slowly return to the present moment"
    };
    
    // UI Components
    private TextView timerText;
    private TextView currentFocusDescription;
    private ImageButton backButton;
    private FloatingActionButton pauseButton, previousButton, nextButton;
    
    // Timer variables
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private long timeLeftInMillis = SESSION_DURATION_MS;
    
    // Media Player variables
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private MusicPreferencesManager preferencesManager;
    private List<String> trackList;
    private int currentTrackIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depression_help);
        
        // Initialize preferences manager
        preferencesManager = new MusicPreferencesManager(this);
        
        // Validate and reset preferences if needed
        preferencesManager.validateAndResetIfNeeded();
        
        // Initialize views
        initViews();
        
        // Set up click listeners
        setupClickListeners();
        
        // Setup media player
        setupMediaPlayer();
        
        // Update the timer text initially
        updateTimerText();
    }
    
    private void initViews() {
        try {
            timerText = findViewById(R.id.timer_text);
            currentFocusDescription = findViewById(R.id.currentFocusDescription);
            
            // Toolbar buttons
            backButton = findViewById(R.id.btn_back);
            
            // Media control buttons
            pauseButton = findViewById(R.id.btn_pause);
            previousButton = findViewById(R.id.btn_previous);
            nextButton = findViewById(R.id.btn_next);
            
            // Validate views
            validateViews();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateViews() {
        boolean allValid = true;
        
        if (timerText == null) {
            Toast.makeText(this, "Timer text view is missing", Toast.LENGTH_SHORT).show();
            allValid = false;
        }
        
        if (currentFocusDescription == null) {
            Toast.makeText(this, "Current focus description view is missing", Toast.LENGTH_SHORT).show();
            allValid = false;
        }
        
        if (backButton == null) {
            Toast.makeText(this, "Back button is missing", Toast.LENGTH_SHORT).show();
            allValid = false;
        }
        
        if (pauseButton == null || previousButton == null || nextButton == null) {
            Toast.makeText(this, "Media control buttons are missing", Toast.LENGTH_SHORT).show();
            allValid = false;
        }
        
        return allValid;
    }
    
    private void setupMediaPlayer() {
        // Get tracks from preferences
        trackList = preferencesManager.getDepressionHelpTracks();
        
        // Check if there was a previously played track
        String lastCategory = preferencesManager.getLastPlayedCategory();
        String lastTrack = preferencesManager.getLastPlayedTrack();
        long lastPosition = preferencesManager.getLastPlaybackPosition();
        
        // If the last category was depression help, try to resume
        if (CATEGORY_DEPRESSION_HELP.equals(lastCategory) && !lastTrack.isEmpty()) {
            // Find the index of the last played track
            for (int i = 0; i < trackList.size(); i++) {
                if (trackList.get(i).equals(lastTrack)) {
                    currentTrackIndex = i;
                    break;
                }
            }
            
            // Initialize media player with the last track
            initializeMediaPlayer(lastTrack);
            
            // Seek to the last position if there was one
            if (lastPosition > 0 && mediaPlayer != null) {
                mediaPlayer.seekTo((int) lastPosition);
            }
        } else {
            // Start with the first track
            if (!trackList.isEmpty()) {
                initializeMediaPlayer(trackList.get(0));
            }
        }
    }
    
    private void initializeMediaPlayer(String trackPath) {
        try {
            // Release any existing media player
            releaseMediaPlayer();
            
            // Create a new media player
            mediaPlayer = new MediaPlayer();
            
            // Set the audio file - in a real app, you would load from assets or resources
            // This is just a placeholder
            int resourceId = 0;
            try {
                resourceId = getResources().getIdentifier(
                    trackPath.replace(".mp3", ""), "raw", getPackageName());
            } catch (Exception e) {
                // Resource not found, handle gracefully
                resourceId = 0;
            }
            
            if (resourceId != 0) {
                try {
                    // Set up the media player from resources
                    mediaPlayer = MediaPlayer.create(this, resourceId);
                    
                    // Set the volume based on saved preferences
                    float volume = preferencesManager.getVolumeLevel();
                    mediaPlayer.setVolume(volume, volume);
                    
                    // Set up completion listener
                    mediaPlayer.setOnCompletionListener(mp -> {
                        // Move to the next track when current one completes
                        playNextTrack();
                    });
                } catch (Exception e) {
                    Toast.makeText(this, "Could not load audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    createDummyMediaPlayer();
                }
            } else {
                // If track not found, use a dummy player
                createDummyMediaPlayer();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing player: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            createDummyMediaPlayer();
        }
    }
    
    private void createDummyMediaPlayer() {
        // Create a dummy media player when resources aren't available
        try {
            // Set up a new media player
            mediaPlayer = new MediaPlayer();
            // Simulate audio playback without actual audio
            mediaPlayer.setOnCompletionListener(mp -> {
                // Simulate track completion after 30 seconds
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (mediaPlayer != null) {
                        playNextTrack();
                    }
                }, 30000); // 30 seconds
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error creating dummy player: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            // Save position before exiting
            saveCurrentPlaybackState();
            finish();
        });

        pauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
                pauseButton.setImageResource(android.R.drawable.ic_media_play);
                Toast.makeText(this, "Music paused", Toast.LENGTH_SHORT).show();
            } else {
                playMusic();
                pauseButton.setImageResource(android.R.drawable.ic_media_pause);
                Toast.makeText(this, "Music playing", Toast.LENGTH_SHORT).show();
            }
            
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        previousButton.setOnClickListener(v -> {
            playPreviousTrack();
        });

        nextButton.setOnClickListener(v -> {
            playNextTrack();
        });
    }
    
    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }
    
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            
            // Save current position when paused
            saveCurrentPlaybackState();
        }
    }
    
    private void playNextTrack() {
        if (trackList.isEmpty()) return;
        
        // Increment track index and wrap around if needed
        currentTrackIndex = (currentTrackIndex + 1) % trackList.size();
        String nextTrack = trackList.get(currentTrackIndex);
        
        // Initialize and play the next track
        initializeMediaPlayer(nextTrack);
        playMusic();
        
        // Reduce timer by 15 seconds
        if (timeLeftInMillis > 15000) {
            timeLeftInMillis -= 15000;
            updateTimerText();
            
            // Calculate and show next instruction
            long elapsedTime = SESSION_DURATION_MS - timeLeftInMillis;
            int instructionIndex = (int) ((elapsedTime / 1000) / 30);
            if (instructionIndex < MEDITATION_INSTRUCTIONS.length) {
                currentFocusDescription.setText(MEDITATION_INSTRUCTIONS[instructionIndex]);
            }
            
            // Restart timer with new time
            startTimer();
        }
    }
    
    private void playPreviousTrack() {
        if (trackList.isEmpty()) return;
        
        // Decrement track index and wrap around if needed
        currentTrackIndex = (currentTrackIndex - 1 + trackList.size()) % trackList.size();
        String prevTrack = trackList.get(currentTrackIndex);
        
        // Initialize and play the previous track
        initializeMediaPlayer(prevTrack);
        playMusic();
        
        // Add 15 seconds to the timer
        timeLeftInMillis += 15000;
        if (timeLeftInMillis > SESSION_DURATION_MS) {
            timeLeftInMillis = SESSION_DURATION_MS;
        }
        updateTimerText();
        
        // Calculate and show previous instruction
        long elapsedTime = SESSION_DURATION_MS - timeLeftInMillis;
        int instructionIndex = (int) ((elapsedTime / 1000) / 30);
        if (instructionIndex < MEDITATION_INSTRUCTIONS.length) {
            currentFocusDescription.setText(MEDITATION_INSTRUCTIONS[instructionIndex]);
        }
        
        // Restart timer with new time
        startTimer();
    }
    
    private void saveCurrentPlaybackState() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentTrack = trackList.isEmpty() ? "" : trackList.get(currentTrackIndex);
            
            // Save to preferences
            preferencesManager.saveLastPlayedInfo(
                CATEGORY_DEPRESSION_HELP, 
                currentTrack, 
                currentPosition
            );
        }
    }
    
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
                updateMeditationInstruction();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                pauseButton.setImageResource(android.R.drawable.ic_media_play);
                Toast.makeText(DepressionHelpActivity.this, "Meditation session complete", Toast.LENGTH_SHORT).show();
            }
        }.start();

        timerRunning = true;
        pauseButton.setImageResource(android.R.drawable.ic_media_pause);
    }
    
    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
    }
    
    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeLeftFormatted);
    }
    
    private void updateMeditationInstruction() {
        if (currentFocusDescription != null) {
            // Calculate which instruction to show based on elapsed time
            long elapsedTime = SESSION_DURATION_MS - timeLeftInMillis;
            int instructionIndex = (int) ((elapsedTime / 1000) / 30); // Change instruction every 30 seconds
            
            if (instructionIndex < MEDITATION_INSTRUCTIONS.length) {
                currentFocusDescription.setText(MEDITATION_INSTRUCTIONS[instructionIndex]);
            }
        }
    }
    
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            playMusic();
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (timerRunning) {
            pauseTimer();
        }
        
        // Save position when stopping
        saveCurrentPlaybackState();
        
        // Pause music when app goes to background
        pauseMusic();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timer resources
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        // Release media player
        releaseMediaPlayer();
    }
} 