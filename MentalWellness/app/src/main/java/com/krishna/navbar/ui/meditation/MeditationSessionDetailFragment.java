package com.krishna.navbar.ui.meditation;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishna.navbar.R;

public class MeditationSessionDetailFragment extends Fragment {
    private static final String ARG_SESSION_ID = "session_id";
    private String sessionId;
    private MeditationSession session;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private ImageView ivMeditationImage;
    private TextView tvMeditationTitle;
    private TextView tvDescription;
    private TextView tvDuration;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabPlay;

    public static MeditationSessionDetailFragment newInstance(String sessionId) {
        MeditationSessionDetailFragment fragment = new MeditationSessionDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionId = getArguments().getString(ARG_SESSION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meditation_session_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        ivMeditationImage = view.findViewById(R.id.iv_meditation_image);
        tvMeditationTitle = view.findViewById(R.id.tv_meditation_title);
        tvDescription = view.findViewById(R.id.tv_description);
        tvDuration = view.findViewById(R.id.tv_duration);
        fabPlay = view.findViewById(R.id.fab_play);

        // Load session data
        loadSessionData();

        // Set click listeners
        setupClickListeners();
    }

    private void loadSessionData() {
        FirebaseFirestore.getInstance()
                .collection("meditation_sessions")
                .document(sessionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    session = documentSnapshot.toObject(MeditationSession.class);
                    if (session != null) {
                        updateUI();
                    } else {
                        Toast.makeText(getContext(), "Session not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Error loading session: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void updateUI() {
        if (session == null) return;

        tvMeditationTitle.setText(session.getTitle());
        tvDescription.setText(session.getDescription());
        tvDuration.setText(session.getDurationMinutes() + " minutes");

        if (session.getImageUrl() != null && !session.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(session.getImageUrl())
                    .into(ivMeditationImage);
        }
    }

    private void setupClickListeners() {
        fabPlay.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMeditation();
            } else {
                playMeditation();
            }
        });
    }

    private void playMeditation() {
        if (session == null || session.getAudioUrl() == null) {
            Toast.makeText(getContext(), "Audio not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(session.getAudioUrl());
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    updatePlayPauseButton();
                });
            }
            mediaPlayer.start();
            isPlaying = true;
            updatePlayPauseButton();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error playing audio: " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMeditation() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            updatePlayPauseButton();
        }
    }

    private void updatePlayPauseButton() {
        fabPlay.setImageResource(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}