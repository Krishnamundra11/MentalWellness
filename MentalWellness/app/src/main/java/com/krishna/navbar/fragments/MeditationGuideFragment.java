package com.krishna.navbar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.R;
import com.krishna.navbar.activities.DepressionHelpActivity;
import com.krishna.navbar.activities.SleepAidActivity;
import com.krishna.navbar.activities.StressReliefActivity;

/**
 * MeditationGuideFragment - Displays a collection of meditation sessions with 
 * instructions for the Mental Wellness app
 */
public class MeditationGuideFragment extends Fragment {

    private ImageButton btnBack, btnMenu;
    private ImageButton btnPlayStress, btnPauseStress;
    private ImageButton btnPlayDepression, btnPauseDepression;
    private ImageButton btnPlaySleep, btnPauseSleep;
    
    // Card views for session selection
    private CardView cardStressRelief;
    private CardView cardDepressionHelp;
    private CardView cardSleepAid;

    public MeditationGuideFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meditation_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        initViews(view);
        
        // Set click listeners
        setupClickListeners();
    }
    
    private void initViews(View view) {
        // Top app bar buttons
        btnBack = view.findViewById(R.id.btn_back);
        btnMenu = view.findViewById(R.id.btn_menu);
        
        // Stress relief controls
        btnPlayStress = view.findViewById(R.id.btn_play_stress);
        btnPauseStress = view.findViewById(R.id.btn_pause_stress);
        
        // Depression help controls
        btnPlayDepression = view.findViewById(R.id.btn_play_depression);
        btnPauseDepression = view.findViewById(R.id.btn_pause_depression);
        
        // Sleep aid controls
        btnPlaySleep = view.findViewById(R.id.btn_play_sleep);
        btnPauseSleep = view.findViewById(R.id.btn_pause_sleep);
        
        // Find the card views - get the parent card view of each session
        cardStressRelief = (CardView) btnPlayStress.getParent().getParent().getParent().getParent();
        cardDepressionHelp = (CardView) btnPlayDepression.getParent().getParent().getParent().getParent();
        cardSleepAid = (CardView) btnPlaySleep.getParent().getParent().getParent().getParent();
    }
    
    private void setupClickListeners() {
        // Top app bar
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        btnMenu.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Options menu clicked", Toast.LENGTH_SHORT).show());
        
        // Session card clicks - launch appropriate activities
        cardStressRelief.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), StressReliefActivity.class)));
            
        cardDepressionHelp.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), DepressionHelpActivity.class)));
            
        cardSleepAid.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), SleepAidActivity.class)));
        
        // Session playback controls - also launch the appropriate activities
        btnPlayStress.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), StressReliefActivity.class)));
            
        btnPauseStress.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Paused Stress Relief session", Toast.LENGTH_SHORT).show());
        
        btnPlayDepression.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), DepressionHelpActivity.class)));
            
        btnPauseDepression.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Paused Depression Help session", Toast.LENGTH_SHORT).show());
        
        btnPlaySleep.setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), SleepAidActivity.class)));
            
        btnPauseSleep.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Paused Sleep Aid session", Toast.LENGTH_SHORT).show());
    }
} 