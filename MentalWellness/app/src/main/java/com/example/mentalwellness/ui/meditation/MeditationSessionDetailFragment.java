package com.example.mentalwellness.ui.meditation;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MeditationSessionDetailFragment extends Fragment {

    private FloatingActionButton fabPlay;
    private TextView tvTitle, tvDuration, tvProgressPercent, tvDescription;
    private ImageView ivMeditationImage;
    private ProgressBar progressBar;
    private ImageButton btnBack, btnFavorite;
    
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable progressRunnable;
    
    // Session data
    private MeditationSession meditationSession;
    
    // Default sample data (will be overridden if arguments are provided)
    private String meditationTitle = "Calm Morning Meditation";
    private String meditationDuration = "10 minutes";
    private String meditationDescription = "Start your day with this calm morning meditation. This session will help you become more present and set a positive intention for the day ahead. The practice includes gentle breathing exercises and mindfulness techniques to center your awareness.";
    private int progressPercent = 65;
    private boolean isFavorite = false;
    
    public MeditationSessionDetailFragment() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meditation_session_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get meditation session from arguments if available
        if (getArguments() != null && getArguments().containsKey("meditation_session")) {
            meditationSession = (MeditationSession) getArguments().getSerializable("meditation_session");
            if (meditationSession != null) {
                meditationTitle = meditationSession.getTitle();
                meditationDuration = meditationSession.getDuration();
                meditationDescription = meditationSession.getDescription();
                // Keep default progress
            }
        }
        
        // Initialize views
        initViews(view);
        
        // Set data to views
        setData();
        
        // Set click listeners
        setListeners();
        
        // Setup media player
        setupMediaPlayer();
    }
    
    private void initViews(View view) {
        fabPlay = view.findViewById(R.id.fab_play);
        tvTitle = view.findViewById(R.id.tv_meditation_title);
        tvDuration = view.findViewById(R.id.tv_duration);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        tvDescription = view.findViewById(R.id.tv_description);
        ivMeditationImage = view.findViewById(R.id.iv_meditation_image);
        progressBar = view.findViewById(R.id.progress_bar);
        btnBack = view.findViewById(R.id.btn_back);
        btnFavorite = view.findViewById(R.id.btn_favorite);
    }
    
    private void setData() {
        tvTitle.setText(meditationTitle);
        tvDuration.setText(meditationDuration);
        tvProgressPercent.setText(progressPercent + "%");
        tvDescription.setText(meditationDescription);
        progressBar.setProgress(progressPercent);
        
        // In a real app, you would load the image from a URL or resource
        // using a library like Glide or Picasso
        int imageResourceId = R.drawable.placeholder_meditation;
        if (meditationSession != null) {
            imageResourceId = meditationSession.getImageResourceId();
        }
        ivMeditationImage.setImageResource(imageResourceId);
        
        updateFavoriteIcon();
    }
    
    private void setListeners() {
        fabPlay.setOnClickListener(v -> togglePlayPause());
        
        btnBack.setOnClickListener(v -> {
            // Navigate back
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        btnFavorite.setOnClickListener(v -> {
            // Toggle favorite status
            isFavorite = !isFavorite;
            updateFavoriteIcon();
            
            // In a real app, you would update this in your database
        });
    }
    
    private void updateFavoriteIcon() {
        if (isFavorite) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star);
        }
    }
    
    private void setupMediaPlayer() {
        // In a real app, you would use the actual audio file
        // This is just for demonstration
        try {
            int audioResourceId = R.raw.sample_meditation_audio;
            if (meditationSession != null) {
                audioResourceId = meditationSession.getAudioResourceId();
            }
            mediaPlayer = MediaPlayer.create(getContext(), audioResourceId);
            handler = new Handler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                // Pause playback
                mediaPlayer.pause();
                fabPlay.setImageResource(android.R.drawable.ic_media_play);
                if (handler != null && progressRunnable != null) {
                    handler.removeCallbacks(progressRunnable);
                }
            } else {
                // Start playback
                mediaPlayer.start();
                fabPlay.setImageResource(android.R.drawable.ic_media_pause);
                updateProgressBar();
            }
            isPlaying = !isPlaying;
        }
    }
    
    private void updateProgressBar() {
        if (mediaPlayer != null && handler != null) {
            progressRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer.isPlaying()) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int total = mediaPlayer.getDuration();
                        int progress = (int) (((float) currentPosition / total) * 100);
                        
                        // Update progress bar and text
                        progressBar.setProgress(progress);
                        tvProgressPercent.setText(progress + "%");
                        
                        // Run again after 1 second
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.post(progressRunnable);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Pause playback when fragment is paused
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            fabPlay.setImageResource(android.R.drawable.ic_media_play);
            if (handler != null && progressRunnable != null) {
                handler.removeCallbacks(progressRunnable);
            }
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (handler != null && progressRunnable != null) {
            handler.removeCallbacks(progressRunnable);
        }
    }
} 