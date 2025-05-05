package com.krishna.navbar.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krishna.navbar.R;

public class DepressionHelpActivity extends AppCompatActivity {

    private TextView timerText;
    private ImageButton backButton, menuButton;
    private FloatingActionButton pauseButton, previousButton, nextButton;
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private long timeLeftInMillis = 20 * 60 * 1000; // 20 minutes in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depression_help);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        timerText = findViewById(R.id.timer_text);
        
        // Toolbar buttons
        backButton = findViewById(R.id.btn_back);
        menuButton = findViewById(R.id.btn_menu);
        
        // Media control buttons
        pauseButton = findViewById(R.id.btn_pause);
        previousButton = findViewById(R.id.btn_previous);
        nextButton = findViewById(R.id.btn_next);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
        });

        menuButton.setOnClickListener(v -> {
            Toast.makeText(this, "Menu options", Toast.LENGTH_SHORT).show();
        });

        pauseButton.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
                pauseButton.setImageResource(android.R.drawable.ic_media_play);
                Toast.makeText(this, "Session paused", Toast.LENGTH_SHORT).show();
            } else {
                startTimer();
                pauseButton.setImageResource(android.R.drawable.ic_media_pause);
                Toast.makeText(this, "Session resumed", Toast.LENGTH_SHORT).show();
            }
        });

        previousButton.setOnClickListener(v -> {
            Toast.makeText(this, "Previous exercise", Toast.LENGTH_SHORT).show();
        });

        nextButton.setOnClickListener(v -> {
            Toast.makeText(this, "Next exercise", Toast.LENGTH_SHORT).show();
        });
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
                pauseButton.setImageResource(android.R.drawable.ic_media_play);
                Toast.makeText(DepressionHelpActivity.this, "Session completed!", Toast.LENGTH_LONG).show();
            }
        }.start();

        timerRunning = true;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeLeftFormatted);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerRunning) {
            pauseTimer();
        }
    }
} 