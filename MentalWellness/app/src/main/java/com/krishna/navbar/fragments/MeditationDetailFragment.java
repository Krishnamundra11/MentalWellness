package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.R;

/**
 * Fragment for displaying meditation details
 */
public class MeditationDetailFragment extends Fragment {

    private ImageButton btnBack, btnMenu;
    private ImageButton btnPlayStress, btnPauseStress;
    private ImageButton btnPlayDepression, btnPauseDepression;
    private ImageButton btnPlaySleep, btnPauseSleep;

    public MeditationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meditation_detail, container, false);
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
        
        // Stress relief session controls
        btnPlayStress.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Playing Stress Relief session", Toast.LENGTH_SHORT).show());
            
        btnPauseStress.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Paused Stress Relief session", Toast.LENGTH_SHORT).show());
        
        // Depression help session controls
        btnPlayDepression.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Playing Depression Help session", Toast.LENGTH_SHORT).show());
            
        btnPauseDepression.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Paused Depression Help session", Toast.LENGTH_SHORT).show());
        
        // Sleep aid session controls
        btnPlaySleep.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Playing Sleep Aid session", Toast.LENGTH_SHORT).show());
            
        btnPauseSleep.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Paused Sleep Aid session", Toast.LENGTH_SHORT).show());
    }
} 