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
import com.krishna.navbar.ui.meditation.MeditationSession;
import com.krishna.navbar.ui.meditation.MeditationSessionDetailFragment;
import com.krishna.navbar.utils.UserUtils;

import java.util.Calendar;

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

    private TextView tvGreeting;
    private TextView tvSubGreeting;
    private UserUtils userUtils;
    
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userUtils = UserUtils.getInstance();
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
        
        // Update greeting with user's name
        updateGreeting();
        
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
        
        // TextViews
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvSubGreeting = view.findViewById(R.id.tv_sub_greeting);
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
        

    }
    
    /**
     * Start a meditation session for sessions that don't have specific activities
     * @param sessionName Name of the session
     * @param duration Duration in minutes
     */
    private void startSession(String sessionName, int duration) {
        // Create a new MeditationSession object
        MeditationSession session = 
            new MeditationSession(
                "1", // dummy id
                sessionName,
                "Description for " + sessionName + " meditation session. Practice mindfulness and relaxation.",
                duration,
                "placeholder_meditation", // Use placeholder image
                "sample_meditation_audio"  // Use sample audio
            );
            
        // Create the fragment
        MeditationSessionDetailFragment fragment = 
            MeditationSessionDetailFragment.newInstance(session.getId());
        
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

    private void updateGreeting() {
        userUtils.getUserName(requireContext(), new UserUtils.OnNameFetched() {
            @Override
            public void onNameFetched(String name) {
                String timeOfDay = getTimeOfDay();
                tvGreeting.setText(String.format("%s, %s", timeOfDay, name));
            }
            
            @Override
            public void onError(String error) {
                // Fallback to generic greeting
                String timeOfDay = getTimeOfDay();
                tvGreeting.setText(String.format("%s!", timeOfDay));
            }
        });
    }
    
    private String getTimeOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hourOfDay < 12) {
            return "Good morning";
        } else if (hourOfDay < 17) {
            return "Good afternoon";
        } else {
            return "Good evening";
        }
    }
} 