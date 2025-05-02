package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krishna.navbar.R;

import java.util.ArrayList;

/**
 * ScoreRecommendationFragment - Shows score results and recommendations based on questionnaire type
 */
public class ScoreRecommendationFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private static final String ARG_SCORE = "score";
    private static final String ARG_TIPS = "tips";

    // UI Components
    private ImageView ivBack;
    private TextView tvScoreTitle;
    private TextView tvScoreValue;
    private TextView tvScoreLevel;
    private TextView tvScoreMessage;
    private TextView tvTipsHeader;
    private TextView tvTip1;
    private TextView tvTip2;
    private TextView tvTip3;
    private TextView tvTip4;
    private TextView tvSavedNotice;
    private Button btnDoAgain;
    private View scoreCardBackground;

    // Data
    private String type;
    private int score;
    private ArrayList<String> tips;

    public ScoreRecommendationFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of the fragment with score data
     */
    public static ScoreRecommendationFragment newInstance(String type, int score, ArrayList<String> tips) {
        ScoreRecommendationFragment fragment = new ScoreRecommendationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putInt(ARG_SCORE, score);
        args.putStringArrayList(ARG_TIPS, tips);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE, "stress");
            score = getArguments().getInt(ARG_SCORE, 0);
            tips = getArguments().getStringArrayList(ARG_TIPS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score_recommendation, container, false);

        // Initialize UI components
        initViews(view);

        // Update UI with data
        updateUI();

        // Set up click listeners
        setupClickListeners();

        return view;
    }

    /**
     * Initialize all view references
     */
    private void initViews(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        tvScoreTitle = view.findViewById(R.id.tv_score_title);
        tvScoreValue = view.findViewById(R.id.tv_score_value);
        tvScoreLevel = view.findViewById(R.id.tv_score_level);
        tvScoreMessage = view.findViewById(R.id.tv_score_message);
        tvTipsHeader = view.findViewById(R.id.tv_tips_header);
        tvTip1 = view.findViewById(R.id.tv_tip_1);
        tvTip2 = view.findViewById(R.id.tv_tip_2);
        tvTip3 = view.findViewById(R.id.tv_tip_3);
        tvTip4 = view.findViewById(R.id.tv_tip_4);
        tvSavedNotice = view.findViewById(R.id.tv_saved_notice);
        btnDoAgain = view.findViewById(R.id.btn_do_stress_again);
        
        // Get reference to the constraint layout that contains the score card content
        scoreCardBackground = view.findViewById(R.id.score_background);
        
        // Show saved notice
        if (tvSavedNotice != null) {
            tvSavedNotice.setVisibility(View.VISIBLE);
            tvSavedNotice.setText("Results have been saved to your profile");
        }
    }

    /**
     * Update UI with the score data
     */
    private void updateUI() {
        // Set score value
        tvScoreValue.setText(String.valueOf(score));

        // Configure UI based on score type
        String titleText = "Your ";
        String buttonText = "Do ";
        int cardBackgroundColor = 0;
        
        switch (type) {
            case "academic":
                titleText += "Academic Score";
                buttonText += "academic quiz again";
                configureAcademicUI();
                break;
            case "stress":
                titleText += "Stress Score";
                buttonText += "stress quiz again";
                configureStressUI();
                break;
            case "sleep":
                titleText += "Sleep Score";
                buttonText += "sleep quiz again";
                configureSleepUI();
                break;
        }
        
        tvScoreTitle.setText(titleText);
        btnDoAgain.setText(buttonText);
        
        // Set tips
        if (tips != null && tips.size() >= 4) {
            tvTip1.setText(tips.get(0));
            tvTip2.setText(tips.get(1));
            tvTip3.setText(tips.get(2));
            tvTip4.setText(tips.get(3));
        }
    }
    
    /**
     * Configure UI for Academic score
     */
    private void configureAcademicUI() {
        // Set background color based on range
        if (score < 40) {
            // Low score - soft green background
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("Low Academic Pressure");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.dark_peach));
            tvScoreMessage.setText("You are performing better today...");
        } else if (score < 75) {
            // Medium score - soft blue background  
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("Medium Academic Pressure");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.medium_peach));
            tvScoreMessage.setText("You are maintaining acceptable focus...");
        } else {
            // High score - soft red background
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("High Academic Pressure");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.dark_peach));
            tvScoreMessage.setText("You are having difficulty focusing...");
        }
    }
    
    /**
     * Configure UI for Stress score
     */
    private void configureStressUI() {
        // Set background color based on range
        if (score < 40) {
            // Low score
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("Low Stress Level");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.dark_peach));
            tvScoreMessage.setText("You are feeling calm today...");
        } else if (score < 75) {
            // Medium score
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("Medium Stress Level");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.medium_peach));
            tvScoreMessage.setText("Your stress level is manageable...");
        } else {
            // High score
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("High Stress Level");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.dark_peach));
            tvScoreMessage.setText("You are feeling pretty stressed today...");
        }
    }
    
    /**
     * Configure UI for Sleep score
     */
    private void configureSleepUI() {
        // Set background color based on range
        if (score < 40) {
            // Low score
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("Poor Sleep Quality");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.dark_peach));
            tvScoreMessage.setText("You have slept less today...");
        } else if (score < 75) {
            // Medium score
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("Moderate Sleep Quality");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.medium_peach));
            tvScoreMessage.setText("Your sleep quality is average...");
        } else {
            // High score
            scoreCardBackground.setBackgroundColor(getResources().getColor(R.color.light_peach));
            tvScoreLevel.setText("Good Sleep Quality");
            tvScoreLevel.setBackgroundResource(R.drawable.level_badge_background);
            tvScoreLevel.getBackground().setTint(getResources().getColor(R.color.dark_peach));
            tvScoreMessage.setText("You have slept well today...");
        }
    }

    /**
     * Set up click listeners for interactive elements
     */
    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        
        // Do quiz again button
        btnDoAgain.setOnClickListener(v -> {
            // Start the QuestionnaireFragment again with the same type
            Bundle args = new Bundle();
            args.putString("type", type);
            QuestionnaireFragment questionnaireFragment = new QuestionnaireFragment();
            questionnaireFragment.setArguments(args);
            
            getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.con, questionnaireFragment, null)
                .addToBackStack(null)
                .commit();
        });
    }
} 