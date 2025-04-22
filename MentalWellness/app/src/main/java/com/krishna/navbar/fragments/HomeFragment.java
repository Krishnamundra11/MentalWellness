package com.krishna.navbar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
 * HomeFragment - Landing page for the Mental Wellness app
 * Displays recommended meditation sessions and quick start options
 */
public class HomeFragment extends Fragment {
    
    // UI Components
    private CardView cardMorningClarity;
    private CardView cardOceanBreath;
    private CardView cardSleep;
    private CardView cardStress;
    private CardView cardFocus;
    private CardView cardNature;
    private ImageView btnNotification;
    private ImageView btnProfile;
    
    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize UI components
        initViews(view);
        
        // Set up click listeners for session cards and buttons
        setupClickListeners();
        
        return view;
    }
    
    /**
     * Initialize all view references
     */
    private void initViews(View view) {
        // Cards
        cardMorningClarity = view.findViewById(R.id.card_morning_clarity);
        cardOceanBreath = view.findViewById(R.id.card_ocean_breath);
        cardSleep = view.findViewById(R.id.card_sleep);
        cardStress = view.findViewById(R.id.card_stress);
        cardFocus = view.findViewById(R.id.card_focus);
        cardNature = view.findViewById(R.id.card_nature);
        
        // Buttons
        btnNotification = view.findViewById(R.id.btn_notification);
        btnProfile = view.findViewById(R.id.btn_profile);
    }
    
    /**
     * Set up click listeners for all interactive elements
     */
    private void setupClickListeners() {
        // Recommended Sessions - still go to MeditationGuideFragment
        cardMorningClarity.setOnClickListener(v -> navigateToMeditationGuide());
        cardOceanBreath.setOnClickListener(v -> navigateToMeditationGuide());
        
        // Quick Start Cards - now navigate directly to their respective activities
        cardSleep.setOnClickListener(v -> startActivity(new Intent(getActivity(), SleepAidActivity.class)));
        cardStress.setOnClickListener(v -> startActivity(new Intent(getActivity(), StressReliefActivity.class)));
        cardFocus.setOnClickListener(v -> startActivity(new Intent(getActivity(), DepressionHelpActivity.class)));
        cardNature.setOnClickListener(v -> navigateToMeditationGuide());
        
        // Top Buttons
        btnNotification.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show());
            
        btnProfile.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Profile", Toast.LENGTH_SHORT).show());
    }
    
    /**
     * Start a meditation session for sessions that don't have specific activities
     * @param sessionName Name of the session
     * @param duration Duration in minutes
     */
    private void startSession(String sessionName, int duration) {
        // Create a new MeditationSession object
        com.example.mentalwellness.ui.meditation.MeditationSession session = 
            new com.example.mentalwellness.ui.meditation.MeditationSession(
                1, // dummy id
                sessionName,
                "Description for " + sessionName + " meditation session. Practice mindfulness and relaxation.",
                duration + " minutes",
                R.drawable.placeholder_meditation, // Use placeholder image
                R.raw.sample_meditation_audio  // Use sample audio
            );
            
        // Create the fragment
        com.example.mentalwellness.ui.meditation.MeditationSessionDetailFragment fragment = 
            new com.example.mentalwellness.ui.meditation.MeditationSessionDetailFragment();
            
        // Pass the session as an argument
        Bundle args = new Bundle();
        args.putSerializable("meditation_session", session);
        fragment.setArguments(args);
        
        // Navigate to the fragment
        getParentFragmentManager().beginTransaction()
            .replace(R.id.con, fragment)
            .addToBackStack(null)
            .commit();
    }
    
    /**
     * Navigate to the Meditation Guide screen
     */
    private void navigateToMeditationGuide() {
        MeditationGuideFragment fragment = new MeditationGuideFragment();
        
        // Navigate to the fragment
        getParentFragmentManager().beginTransaction()
            .replace(R.id.con, fragment)
            .addToBackStack(null)
            .commit();
    }
} 