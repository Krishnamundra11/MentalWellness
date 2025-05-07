package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishna.navbar.R;
import com.krishna.navbar.models.PredictionRequest;
import com.krishna.navbar.models.PredictionResponse;
import com.krishna.navbar.utils.FirestoreHelper;
import com.krishna.navbar.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Button btnRefreshResult;
    private ProgressBar progressLoading;
    private View scoreCardBackground;

    // Data
    private String type;
    private int score;
    private ArrayList<String> tips;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirestoreHelper firestoreHelper;

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
        
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firestoreHelper = new FirestoreHelper();
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
        
        // Fetch prediction from API
        fetchPredictionFromApi();

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
        btnRefreshResult = view.findViewById(R.id.btn_refresh_result);
        progressLoading = view.findViewById(R.id.progress_loading);
        
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
        
        // Set tips initially (these will be replaced by API recommendation)
        if (tips != null && tips.size() >= 4) {
            tvTip1.setText(tips.get(0));
            tvTip2.setText(tips.get(1));
            tvTip3.setText(tips.get(2));
            tvTip4.setText(tips.get(3));
        }
    }
    
    /**
     * Fetch prediction and recommendation from API
     */
    private void fetchPredictionFromApi() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading indicator
        progressLoading.setVisibility(View.VISIBLE);
        
        // Get answers from Firestore
        fetchAnswersAndMakeApiCall();
    }
    
    /**
     * Fetch answers from Firestore and make API call
     */
    private void fetchAnswersAndMakeApiCall() {
        // Get reference to today's questionnaire document
        DocumentReference docRef = firestoreHelper.getTodayQuestionnaireReference(type);
        
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get answers map from document
                Map<String, String> answers = (Map<String, String>) documentSnapshot.get("answers");
                
                if (answers != null) {
                    // Create request model
                    PredictionRequest request = new PredictionRequest(answers);
                    
                    // Make API call
                    makeApiCall(request);
                } else {
                    // No answers found
                    progressLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: No answers found", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Document doesn't exist
                progressLoading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: No data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            // Error getting document
            progressLoading.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Error fetching answers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    
    /**
     * Make API call to get prediction and recommendation
     */
    private void makeApiCall(PredictionRequest request) {
        // Set a timeout of 15 seconds for the operation
        RetrofitClient.getApiService().getPrediction(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                progressLoading.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    PredictionResponse predictionResponse = response.body();
                    updateUIWithPrediction(predictionResponse);
                } else {
                    handleApiError(null);
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                progressLoading.setVisibility(View.GONE);
                handleApiError(t);
            }
        });
    }
    
    /**
     * Handle API errors with appropriate messaging and fallback
     */
    private void handleApiError(Throwable t) {
        String errorMessage;
        
        if (t != null) {
            // Determine specific error message based on exception type
            if (t instanceof java.net.SocketTimeoutException) {
                errorMessage = "Connection timed out. Server might be starting up.";
            } else if (t instanceof java.net.UnknownHostException) {
                errorMessage = "Network error: Cannot reach server.";
            } else if (t instanceof java.io.IOException) {
                errorMessage = "Network error: " + t.getMessage();
            } else {
                errorMessage = "Error connecting to API: " + t.getMessage();
            }
            android.util.Log.e("ScoreRecommendation", "API error: " + t.getMessage(), t);
        } else {
            errorMessage = "Error getting prediction from server.";
        }
        
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        
        // Show tips anyway from local data
        tvScoreLevel.setText(getDefaultLevelBasedOnScore());
        tvScoreMessage.setText(getDefaultMessageBasedOnScore());
        
        // Enable retry button
        btnRefreshResult.setEnabled(true);
        btnRefreshResult.setVisibility(View.VISIBLE);
    }
    
    /**
     * Get default level classification based on score
     */
    private String getDefaultLevelBasedOnScore() {
        if (score < 40) {
            return type.equals("stress") ? "High Stress" : "Poor";
        } else if (score < 75) {
            return type.equals("stress") ? "Moderate Stress" : "Average";
        } else {
            return type.equals("stress") ? "Low Stress" : "Excellent";
        }
    }
    
    /**
     * Get default message based on score
     */
    private String getDefaultMessageBasedOnScore() {
        if (score < 40) {
            if (type.equals("stress")) {
                return "Your stress levels are high. Consider practicing relaxation techniques and consulting a professional.";
            } else if (type.equals("academic")) {
                return "Your academic performance needs improvement. Consider seeking academic support.";
            } else {
                return "Your sleep quality is poor. Try to establish a better sleep routine.";
            }
        } else if (score < 75) {
            if (type.equals("stress")) {
                return "You have moderate stress levels. Regular self-care may help reduce your stress.";
            } else if (type.equals("academic")) {
                return "Your academic performance is average. With some improvements, you can excel further.";
            } else {
                return "Your sleep quality is average. Small adjustments to your routine might help improve it.";
            }
        } else {
            if (type.equals("stress")) {
                return "You have healthy stress levels. Keep maintaining your good habits!";
            } else if (type.equals("academic")) {
                return "Your academic performance is excellent. Keep up the good work!";
            } else {
                return "Your sleep quality is excellent. Continue with your healthy sleep routine.";
            }
        }
    }
    
    /**
     * Update UI with prediction response
     */
    private void updateUIWithPrediction(PredictionResponse response) {
        // Update the stress level with prediction
        if (response.getPrediction() != null) {
            tvScoreLevel.setText(response.getPrediction());
        }
        
        // Update the message with recommendation
        if (response.getRecommendation() != null) {
            tvScoreMessage.setText(response.getRecommendation());
            
            // Set first tip to recommendation and keep others (can be custom)
            tvTip1.setText(response.getRecommendation());
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
     * Set up click listeners
     */
    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        
        // Do questionnaire again button
        btnDoAgain.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Create a new instance of QuestionnaireFragment with the same type
                QuestionnaireFragment questionnaireFragment = new QuestionnaireFragment();
                Bundle args = new Bundle();
                args.putString("type", type);
                questionnaireFragment.setArguments(args);
                
                // Replace the current fragment with the questionnaire fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.con, questionnaireFragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
        
        // Refresh result button
        btnRefreshResult.setOnClickListener(v -> {
            // Fetch prediction from API again
            fetchPredictionFromApi();
        });
    }
} 