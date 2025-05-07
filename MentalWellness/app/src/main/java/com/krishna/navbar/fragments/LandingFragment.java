package com.krishna.navbar.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.krishna.navbar.utils.UserUtils;

import java.util.ArrayList;
import java.util.Calendar;
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
    private LinearLayout scoreCardsContainer;
    private MaterialCardView cardScoreResult;
    private TextView tvScoreTitle;
    private TextView tvScoreValue;
    private TextView tvScoreMessage;
    private TextView tvTipsHeader;
    private TextView tvTip1;
    private TextView tvTip2;
    private TextView tvTip3;
    private TextView tvTip4;
    private Button btnRetakeQuiz;

    // Data
    private List<CarouselItem> carouselItems;
    private CarouselAdapter carouselAdapter;
    private UserUtils userUtils;
    
    public LandingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        
        // Initialize UI components
        initViews(view);
        
        // Set up carousel
        setupCarousel();
        
        // Set up click listeners for all interactive elements
        setupClickListeners();
        
        // Set up fragment result listener for questionnaire results
        setupFragmentResultListener();
        
        // Update greeting with user's name
        updateGreeting();
        
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

        // Score Card
        scoreCardsContainer = view.findViewById(R.id.score_cards_container);
        cardScoreResult = view.findViewById(R.id.card_score_result);
        tvScoreTitle = view.findViewById(R.id.tv_score_title);
        tvScoreValue = view.findViewById(R.id.tv_score_value);
        tvScoreMessage = view.findViewById(R.id.tv_score_message);
        tvTipsHeader = view.findViewById(R.id.tv_tips_header);
        tvTip1 = view.findViewById(R.id.tv_tip_1);
        tvTip2 = view.findViewById(R.id.tv_tip_2);
        tvTip3 = view.findViewById(R.id.tv_tip_3);
        tvTip4 = view.findViewById(R.id.tv_tip_4);
        btnRetakeQuiz = view.findViewById(R.id.btn_retake_quiz);
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
            
        ivUserAvatar.setOnClickListener(v -> {
            // Navigate to ProfileFragment
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new ProfileFragment())
                .addToBackStack(null)
                .commit();
        });
        
        // Mood Check
        btnMoodAction.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Checking your mood", Toast.LENGTH_SHORT).show());
            
        // Carousel items
        carouselAdapter.setOnItemClickListener(position -> {
            CarouselItem item = carouselItems.get(position);
            String type;
            switch (position) {
                case 0:
                    type = "academic";
                    break;
                case 1:
                    type = "stress";
                    break;
                case 2:
                    type = "sleep";
                    break;
                default:
                    type = "academic";
            }
            
            // Start the QuestionnaireFragment with the appropriate type
            Bundle args = new Bundle();
            args.putString("type", type);
            QuestionnaireFragment questionnaireFragment = new QuestionnaireFragment();
            questionnaireFragment.setArguments(args);
            
            getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.con, questionnaireFragment)
                .addToBackStack(null)
                .commit();
        });
        
        // Service cards
        cardTherapy.setOnClickListener(v -> {
            // Navigate to FindExpertFragment
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new FindExpertFragment())
                .addToBackStack(null)
                .commit();
        });
        
        cardPlaylists.setOnClickListener(v -> {
            // Navigate to MusicMainFragment
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new MusicMainFragment())
                .addToBackStack(null)
                .commit();
        });
        
        cardLibrary.setOnClickListener(v -> {
            // Navigate to LibraryFragment
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new LibraryFragment())
                .addToBackStack(null)
                .commit();
        });
        
        cardInspiration.setOnClickListener(v -> {
            // Navigate to a future InspirationFragment or show toast if not implemented
            Toast.makeText(getContext(), "Daily inspiration coming soon", Toast.LENGTH_SHORT).show();
        });
        
        // Action buttons
        tvTherapyStart.setOnClickListener(v -> {
            // Navigate to FindExpertFragment
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new FindExpertFragment())
                .addToBackStack(null)
                .commit();
        });
        
        tvPlaylistsStart.setOnClickListener(v -> {
            // Navigate to MusicMainFragment
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new MusicMainFragment())
                .addToBackStack(null)
                .commit();
        });
        
        tvLibraryStart.setOnClickListener(v -> {
            // Navigate to LibraryFragment
            getParentFragmentManager().beginTransaction()
                .replace(R.id.con, new LibraryFragment())
                .addToBackStack(null)
                .commit();
        });
        
        // Retake quiz button
        if (btnRetakeQuiz != null) {
            btnRetakeQuiz.setOnClickListener(v -> {
                // Get the category from the current score card
                String category = tvScoreTitle.getText().toString().toLowerCase();
                if (category.contains("academic")) {
                    category = "academic";
                } else if (category.contains("stress")) {
                    category = "stress";
                } else if (category.contains("sleep")) {
                    category = "sleep";
                } else {
                    category = "academic"; // default
                }
                
                // Start the questionnaire with the same category
                Bundle args = new Bundle();
                args.putString("type", category);
                QuestionnaireFragment questionnaireFragment = new QuestionnaireFragment();
                questionnaireFragment.setArguments(args);
                
                getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.con, questionnaireFragment)
                    .addToBackStack(null)
                    .commit();
            });
        }
    }

    /**
     * Set up fragment result listener to receive questionnaire results
     */
    private void setupFragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener("questionnaire_result", this, (requestKey, result) -> {
            // Extract results from the bundle
            String type = result.getString("type", "academic");
            int score = result.getInt("score", 0);
            ArrayList<String> tips = result.getStringArrayList("tips");
            
            // Display result based on type
            displayQuestionnaireResult(type, score, tips);
        });
    }

    /**
     * Display questionnaire result with score and tips
     */
    private void displayQuestionnaireResult(String type, int score, ArrayList<String> tips) {
        // Configure the card title based on type
        String titleText = "Your ";
        int cardColor = 0;
        switch (type) {
            case "academic":
                titleText += "Academic Score";
                cardColor = Color.parseColor("#E8F5E9");
                break;
            case "stress":
                titleText += "Stress Score";
                cardColor = Color.parseColor("#E3F2FD");
                break;
            case "sleep":
                titleText += "Sleep Score";
                cardColor = Color.parseColor("#FFF3E0");
                break;
        }
        tvScoreTitle.setText(titleText);
        cardScoreResult.setCardBackgroundColor(cardColor);
        
        // Set the score value
        tvScoreValue.setText(String.valueOf(score));
        
        // Set appropriate message based on score
        if (score < 40) {
            if (type.equals("stress")) {
                tvScoreMessage.setText("You are feeling pretty stressed today...");
            } else if (type.equals("sleep")) {
                tvScoreMessage.setText("You have slept less today...");
            } else {
                tvScoreMessage.setText("You are having difficulty focusing...");
            }
        } else if (score < 75) {
            if (type.equals("stress")) {
                tvScoreMessage.setText("Your stress level is manageable...");
            } else if (type.equals("sleep")) {
                tvScoreMessage.setText("Your sleep quality is average...");
            } else {
                tvScoreMessage.setText("You are maintaining acceptable focus...");
            }
        } else {
            if (type.equals("stress")) {
                tvScoreMessage.setText("You are feeling calm today...");
            } else if (type.equals("sleep")) {
                tvScoreMessage.setText("You have slept well today...");
            } else {
                tvScoreMessage.setText("You are performing better today...");
            }
        }
        
        // Set tips
        if (tips != null && tips.size() >= 4) {
            tvTip1.setText(tips.get(0));
            tvTip2.setText(tips.get(1));
            tvTip3.setText(tips.get(2));
            tvTip4.setText(tips.get(3));
        }
        
        // Show the score container
        scoreCardsContainer.setVisibility(View.VISIBLE);
    }

    private void updateGreeting() {
        userUtils.getUserName(requireContext(), new UserUtils.OnNameFetched() {
            @Override
            public void onNameFetched(String name) {
                String timeOfDay = getTimeOfDay();
                tvGreeting.setText(String.format("%s, %s!", timeOfDay, name));
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