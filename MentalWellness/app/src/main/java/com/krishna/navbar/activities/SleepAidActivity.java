package com.krishna.navbar.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krishna.navbar.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SleepAidActivity extends AppCompatActivity {

    // Session duration in milliseconds (30 minutes)
    private static final long SESSION_DURATION_MS = TimeUnit.MINUTES.toMillis(30);
    
    // UI Components
    private TextView timerText;
    private FloatingActionButton btnPause;
    private FloatingActionButton btnPrevious;
    private FloatingActionButton btnNext;
    private ImageButton btnBack;
    private ImageButton btnMenu;
    private ImageButton btnVolume;
    private ImageButton btnMusic;
    private ImageButton btnNotification;
    
    // Timer variables
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private long timeLeftInMillis = SESSION_DURATION_MS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_aid);
        
        // Initialize views
        initViews();
        
        // Set up click listeners
        setupClickListeners();
        
        // Update the timer text initially
        updateTimerText();
    }
    
    private void initViews() {
        timerText = findViewById(R.id.timer_text);
        btnPause = findViewById(R.id.btn_pause);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);
        btnVolume = findViewById(R.id.btn_volume);
        btnMusic = findViewById(R.id.btn_music);
        btnNotification = findViewById(R.id.btn_notification);
    }
    
    private void setupClickListeners() {
        // Navigation controls
        btnBack.setOnClickListener(v -> finish());
        
        btnMenu.setOnClickListener(v -> 
            Toast.makeText(this, "Options menu", Toast.LENGTH_SHORT).show());
        
        // Playback controls
        btnPause.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
                btnPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                startTimer();
                btnPause.setImageResource(android.R.drawable.ic_media_pause);
            }
        });
        
        btnPrevious.setOnClickListener(v -> 
            Toast.makeText(this, "Previous meditation step", Toast.LENGTH_SHORT).show());
            
        btnNext.setOnClickListener(v -> 
            Toast.makeText(this, "Next meditation step", Toast.LENGTH_SHORT).show());
            
        // Audio controls
        btnVolume.setOnClickListener(v -> 
            Toast.makeText(this, "Volume control", Toast.LENGTH_SHORT).show());
            
        btnMusic.setOnClickListener(v -> 
            Toast.makeText(this, "Music selection", Toast.LENGTH_SHORT).show());
            
        btnNotification.setOnClickListener(v -> 
            Toast.makeText(this, "Notification settings", Toast.LENGTH_SHORT).show());
    }
    
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                btnPause.setImageResource(android.R.drawable.ic_media_play);
                Toast.makeText(SleepAidActivity.this, "Session Complete!", Toast.LENGTH_LONG).show();
                // Reset timer for future use
                timeLeftInMillis = SESSION_DURATION_MS;
                updateTimerText();
            }
        }.start();

        timerRunning = true;
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timer resources
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
} 