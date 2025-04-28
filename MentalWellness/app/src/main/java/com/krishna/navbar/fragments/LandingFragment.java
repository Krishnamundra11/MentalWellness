package com.krishna.navbar.fragments;

import android.graphics.Color;
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
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.krishna.navbar.R;
import com.krishna.navbar.adapters.CarouselAdapter;
import com.krishna.navbar.models.CarouselItem;

import java.util.ArrayList;
import java.util.List;

/**
 * LandingFragment - Main landing screen for the Mental Wellness app
 * Displays user greeting, mood check, wellbeing categories, and quick actions
 */
public class LandingFragment extends Fragment {

    // UI Components
    private ImageView ivUserAvatar;
    private ImageView ivNotification;
    private TextView tvGreeting;
    private MaterialCardView cardMoodCheck;
    private TextView btnMoodAction;
    private ViewPager2 carouselWellbeing;
    private TabLayout carouselIndicator;
    private MaterialCardView cardTherapy;
    private MaterialCardView cardPlaylists;
    private MaterialCardView cardLibrary;
    private MaterialCardView cardInspiration;
    private TextView tvTherapyStart;
    private TextView tvPlaylistsStart;
    private TextView tvLibraryStart;

    // Data
    private List<CarouselItem> carouselItems;
    private CarouselAdapter carouselAdapter;
    
    public LandingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        
        // Initialize UI components
        initViews(view);
        
        // Set up carousel
        setupCarousel();
        
        // Set up click listeners for all interactive elements
        setupClickListeners();
        
        return view;
    }
    
    /**
     * Initialize all view references
     */
    private void initViews(View view) {
        // Top Bar
        ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        ivNotification = view.findViewById(R.id.iv_notification);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        
        // Mood Check
        cardMoodCheck = view.findViewById(R.id.card_mood_check);
        btnMoodAction = view.findViewById(R.id.btn_mood_action);
        
        // Wellbeing Carousel
        carouselWellbeing = view.findViewById(R.id.carousel_wellbeing);
        carouselIndicator = view.findViewById(R.id.carousel_indicator);

        // Service Cards
        cardTherapy = view.findViewById(R.id.card_therapy);
        cardPlaylists = view.findViewById(R.id.card_playlists);
        cardLibrary = view.findViewById(R.id.card_library);
        cardInspiration = view.findViewById(R.id.card_inspiration);
        
        // Action Buttons
        tvTherapyStart = view.findViewById(R.id.tv_therapy_start);
        tvPlaylistsStart = view.findViewById(R.id.tv_playlists_start);
        tvLibraryStart = view.findViewById(R.id.tv_library_start);
    }
    
    /**
     * Set up the wellbeing categories carousel
     */
    private void setupCarousel() {
        // Create carousel items
        carouselItems = new ArrayList<>();
        carouselItems.add(new CarouselItem(
                "Check Your Academic Score",
                "Find your inner peace",
                R.drawable.academic_score,
                Color.parseColor("#E8F5E9")
        ));
        carouselItems.add(new CarouselItem(
                "Check Your Stress Level",
                "Balance mind and body",
                R.drawable.stress_level,
                Color.parseColor("#E3F2FD")
        ));
        carouselItems.add(new CarouselItem(
                "Track Your Sleep",
                "Find peace in nature",
                R.drawable.sleep_score,
                Color.parseColor("#FFF3E0")
        ));
        
        // Create and set adapter
        carouselAdapter = new CarouselAdapter(getContext(), carouselItems);
        carouselWellbeing.setAdapter(carouselAdapter);
        
        // Apply page transformer for scaling and alpha effects
        carouselWellbeing.setPageTransformer(new ViewPager2.PageTransformer() {
            private static final float MIN_SCALE = 0.8f;
            private static final float MIN_ALPHA = 0.5f;
            
            @Override
            public void transformPage(@NonNull View page, float position) {
                int pageWidth = page.getWidth();
                
                if (position < -1) { // Page is far off-screen to the left
                    page.setAlpha(0f);
                    page.setScaleX(MIN_SCALE);
                    page.setScaleY(MIN_SCALE);
                } else if (position <= 1) { // Page is visible or partially visible
                    // Calculate scale factor
                    float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                    
                    // Calculate alpha
                    float alphaFactor = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - Math.abs(position));
                    page.setAlpha(alphaFactor);
                    
                    // Apply translation for a smoother effect
                    float yPosition = position * -pageWidth / 8f;
                    page.setTranslationX(yPosition);
                } else { // Page is far off-screen to the right
                    page.setAlpha(0f);
                    page.setScaleX(MIN_SCALE);
                    page.setScaleY(MIN_SCALE);
                }
            }
        });
        
        // Set up tab layout with viewpager2 and enhanced indicator
        new TabLayoutMediator(carouselIndicator, carouselWellbeing,
                (tab, position) -> {
                    // No text for tabs, using custom indicator
                }).attach();
                
        // Set indicator interpolation
        carouselIndicator.setTabIndicatorAnimationMode(TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC);
        
        // Set offscreen page limit
        carouselWellbeing.setOffscreenPageLimit(3);
    }
    
    /**
     * Set up click listeners for all interactive elements
     */
    private void setupClickListeners() {
        // Top Bar
        ivNotification.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show());
            
        ivUserAvatar.setOnClickListener(v -> 
            Toast.makeText(getContext(), "User Profile", Toast.LENGTH_SHORT).show());
        
        // Mood Check
        btnMoodAction.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Checking your mood", Toast.LENGTH_SHORT).show());
            
        // Carousel items
        carouselAdapter.setOnItemClickListener(position -> {
            CarouselItem item = carouselItems.get(position);
            Toast.makeText(getContext(), "Selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
        
        // Service Cards
        tvTherapyStart.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Book a Therapy", Toast.LENGTH_SHORT).show());
            
        tvPlaylistsStart.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Calming Playlists", Toast.LENGTH_SHORT).show());
            
        tvLibraryStart.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Wellness Library", Toast.LENGTH_SHORT).show());
            
        // Make whole cards clickable
        cardTherapy.setOnClickListener(v -> tvTherapyStart.performClick());
        cardPlaylists.setOnClickListener(v -> tvPlaylistsStart.performClick());
        cardLibrary.setOnClickListener(v -> tvLibraryStart.performClick());
    }
} 