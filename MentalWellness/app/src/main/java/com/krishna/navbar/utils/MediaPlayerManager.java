package com.krishna.navbar.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;

/**
 * Manager class for handling MediaPlayer operations
 */
public class MediaPlayerManager {
    private static final String TAG = "MediaPlayerManager";
    
    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private Context context;
    private boolean isPrepared = false;
    private boolean isPlaying = false;
    private String currentUrl = "";
    
    // For tracking playback position
    private Handler progressHandler;
    private Runnable progressRunnable;
    private SeekBar seekBar;
    
    // Callback interfaces
    public interface OnPreparedListener {
        void onPrepared();
    }
    
    public interface OnErrorListener {
        void onError(String errorMessage);
    }
    
    public interface OnCompletionListener {
        void onCompletion();
    }
    
    private OnPreparedListener onPreparedListener;
    private OnErrorListener onErrorListener;
    private OnCompletionListener onCompletionListener;
    
    // Private constructor for singleton pattern
    private MediaPlayerManager(Context context) {
        this.context = context.getApplicationContext();
        createMediaPlayer();
        progressHandler = new Handler();
    }
    
    /**
     * Initialize the MediaPlayerManager singleton
     */
    public static void initialize(Context context) {
        if (instance == null) {
            instance = new MediaPlayerManager(context);
        }
    }
    
    /**
     * Get the MediaPlayerManager singleton instance
     */
    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MediaPlayerManager must be initialized with context first");
        }
        return instance;
    }
    
    /**
     * Create a new MediaPlayer instance
     */
    private void createMediaPlayer() {
        releaseMediaPlayer();
        
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
            new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        );
        
        mediaPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            if (onPreparedListener != null) {
                onPreparedListener.onPrepared();
            }
        });
        
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            String errorMsg = "MediaPlayer error: " + what + ", " + extra;
            Log.e(TAG, errorMsg);
            isPrepared = false;
            
            if (onErrorListener != null) {
                onErrorListener.onError(errorMsg);
            }
            return true;
        });
        
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            if (onCompletionListener != null) {
                onCompletionListener.onCompletion();
            }
        });
    }
    
    /**
     * Stream audio from a URL
     */
    public void playUrl(String url, OnPreparedListener preparedListener, OnErrorListener errorListener) {
        // If it's the same URL and already prepared, just play it
        if (url.equals(currentUrl) && isPrepared) {
            playPause();
            return;
        }
        
        // Save callbacks
        this.onPreparedListener = preparedListener;
        this.onErrorListener = errorListener;
        
        // Reset and prepare for a new URL
        isPrepared = false;
        isPlaying = false;
        currentUrl = url;
        
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source", e);
            currentUrl = "";
            
            if (errorListener != null) {
                errorListener.onError("Error loading audio: " + e.getMessage());
            }
            
            Toast.makeText(context, "Error loading audio", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Play or pause the current audio
     */
    public void playPause() {
        if (!isPrepared) {
            Log.e(TAG, "MediaPlayer not prepared");
            return;
        }
        
        try {
            if (isPlaying) {
                mediaPlayer.pause();
                stopProgressUpdate();
            } else {
                mediaPlayer.start();
                startProgressUpdate();
            }
            isPlaying = !isPlaying;
        } catch (Exception e) {
            Log.e(TAG, "Error playing/pausing", e);
        }
    }
    
    /**
     * Stop playback
     */
    public void stop() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                stopProgressUpdate();
                isPlaying = false;
            } catch (Exception e) {
                Log.e(TAG, "Error stopping", e);
            }
        }
    }
    
    /**
     * Set a SeekBar to control and show progress
     */
    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && isPrepared) {
                        mediaPlayer.seekTo(progress);
                    }
                }
                
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Nothing needed here
                }
                
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Nothing needed here
                }
            });
            
            // Update seekbar max to song duration when prepared
            if (isPrepared) {
                seekBar.setMax(mediaPlayer.getDuration());
            }
        }
    }
    
    /**
     * Start updating the progress
     */
    private void startProgressUpdate() {
        stopProgressUpdate(); // Stop any existing updates first
        
        if (seekBar != null && mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());
            
            progressRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && isPlaying) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        progressHandler.postDelayed(this, 100);
                    }
                }
            };
            
            progressHandler.post(progressRunnable);
        }
    }
    
    /**
     * Stop updating the progress
     */
    private void stopProgressUpdate() {
        if (progressHandler != null && progressRunnable != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }
    
    /**
     * Set the completion listener
     */
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }
    
    /**
     * Get total duration of the current audio
     */
    public int getDuration() {
        if (isPrepared) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }
    
    /**
     * Get current position in the audio
     */
    public int getCurrentPosition() {
        if (isPrepared) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    /**
     * Check if MediaPlayer is currently playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }
    
    /**
     * Check if MediaPlayer is prepared and ready
     */
    public boolean isPrepared() {
        return isPrepared;
    }
    
    /**
     * Release MediaPlayer resources
     */
    public void releaseMediaPlayer() {
        stopProgressUpdate();
        
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                // Ignore, may not be prepared
            }
            
            mediaPlayer.release();
            mediaPlayer = null;
            isPrepared = false;
            isPlaying = false;
            currentUrl = "";
        }
    }
} 