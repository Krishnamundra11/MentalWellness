package com.krishna.navbar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishna.navbar.R;
import com.krishna.navbar.models.QuestionnaireResponse;
import com.krishna.navbar.utils.FirestoreHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * QuestionnaireFragment - Displays questionnaire for academic, stress, or sleep assessment
 * Fetches questions from Firestore, shows one question at a time, and calculates final score
 */
public class QuestionnaireFragment extends Fragment {

    // Constants
    private static final int TOTAL_QUESTIONS = 5;
    private static final String ARG_TYPE = "type";
    private static final String FIRESTORE_COLLECTION_PATH = "questionnaires";
    
    // Types of questionnaires
    private static final String TYPE_ACADEMIC = "academic";
    private static final String TYPE_STRESS = "stress";
    private static final String TYPE_SLEEP = "sleep";
    
    // UI Components
    private ImageView ivClose;
    private TextView tvQuestionCount;
    private ImageView ivQuestionImage;
    private TextView tvQuestionText;
    private Button btnNext;
    
    // Data
    private String questionnaireType;
    private List<String> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int[] answers = new int[TOTAL_QUESTIONS];
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirestoreHelper firestoreHelper;
    
    // Images for each question type
    private final int[] academicImages = {
            R.drawable.academic_score,
            R.drawable.academic_score,
            R.drawable.academic_score,
            R.drawable.academic_score,
            R.drawable.academic_score
    };
    
    private final int[] stressImages = {
            R.drawable.stress_level,
            R.drawable.stress_level,
            R.drawable.stress_level,
            R.drawable.stress_level,
            R.drawable.stress_level
    };
    
    private final int[] sleepImages = {
            R.drawable.sleep_score,
            R.drawable.sleep_score,
            R.drawable.sleep_score,
            R.drawable.sleep_score,
            R.drawable.sleep_score
    };
    
    // Hardcoded tips based on score ranges
    private final Map<String, List<String>> academicTips = new HashMap<String, List<String>>() {{
        List<String> lowScoreTips = new ArrayList<String>();
        lowScoreTips.add("Breathe deeply, relax completely.");
        lowScoreTips.add("Move your body, clear your mind.");
        lowScoreTips.add("Smile more, stress less.");
        lowScoreTips.add("Pause, reflect, let go.");
        put("low", lowScoreTips);
        
        List<String> mediumScoreTips = new ArrayList<String>();
        mediumScoreTips.add("Stay focused, learn smarter.");
        mediumScoreTips.add("Plan well, study better.");
        mediumScoreTips.add("Practice daily, master concepts.");
        mediumScoreTips.add("Review often, remember longer.");
        put("medium", mediumScoreTips);
        
        List<String> highScoreTips = new ArrayList<String>();
        highScoreTips.add("Keep up your excellent focus!");
        highScoreTips.add("Continue your consistent study habits.");
        highScoreTips.add("Maintain your positive academic attitude.");
        highScoreTips.add("Share your study techniques with peers.");
        put("high", highScoreTips);
    }};
    
    private final Map<String, List<String>> stressTips = new HashMap<String, List<String>>() {{
        List<String> lowScoreTips = new ArrayList<String>();
        lowScoreTips.add("Breathe deeply, relax completely.");
        lowScoreTips.add("Move your body, clear your mind.");
        lowScoreTips.add("Smile more, stress less.");
        lowScoreTips.add("Pause, reflect, let go.");
        put("low", lowScoreTips);
        
        List<String> mediumScoreTips = new ArrayList<String>();
        mediumScoreTips.add("Take short breaks during work.");
        mediumScoreTips.add("Practice mindfulness for 5 minutes daily.");
        mediumScoreTips.add("Set realistic goals for yourself.");
        mediumScoreTips.add("Connect with friends regularly.");
        put("medium", mediumScoreTips);
        
        List<String> highScoreTips = new ArrayList<String>();
        highScoreTips.add("Prioritize sleep and nutrition.");
        highScoreTips.add("Consider talking to a counselor.");
        highScoreTips.add("Limit caffeine and screen time.");
        highScoreTips.add("Try guided meditation apps.");
        put("high", highScoreTips);
    }};
    
    private final Map<String, List<String>> sleepTips = new HashMap<String, List<String>>() {{
        List<String> lowScoreTips = new ArrayList<String>();
        lowScoreTips.add("Maintain consistent sleep/wake times.");
        lowScoreTips.add("Create a restful bedroom environment.");
        lowScoreTips.add("Avoid screens 1 hour before bed.");
        lowScoreTips.add("Try relaxation techniques before sleeping.");
        put("low", lowScoreTips);
        
        List<String> mediumScoreTips = new ArrayList<String>();
        mediumScoreTips.add("Limit daytime naps to 20 minutes.");
        mediumScoreTips.add("Exercise regularly but not before bed.");
        mediumScoreTips.add("Avoid large meals and caffeine before sleep.");
        mediumScoreTips.add("Create a bedtime routine.");
        put("medium", mediumScoreTips);
        
        List<String> highScoreTips = new ArrayList<String>();
        highScoreTips.add("Keep your quality sleep routine.");
        highScoreTips.add("Continue limiting screen time before bed.");
        highScoreTips.add("Maintain your consistent sleep schedule.");
        highScoreTips.add("Share your healthy sleep habits.");
        put("high", highScoreTips);
    }};
    
    // Track currently selected option
    private int currentSelectedOption = 0;
    private View previouslySelectedOption = null;
    
    public QuestionnaireFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the questionnaire type from arguments
        if (getArguments() != null) {
            questionnaireType = getArguments().getString(ARG_TYPE, TYPE_ACADEMIC);
        } else {
            questionnaireType = TYPE_ACADEMIC;
        }
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestoreHelper = new FirestoreHelper();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);
        
        // Initialize UI components
        initViews(view);
        
        // Set up click listeners
        setupClickListeners();
        
        // Check if user has already completed this questionnaire today
        checkTodaySubmission();
        
        return view;
    }
    
    /**
     * Initialize all view references
     */
    private void initViews(View view) {
        // Old references
        // ivBack = view.findViewById(R.id.iv_back);
        // tvProgressText = view.findViewById(R.id.tv_progress_text);
        // progressBar = view.findViewById(R.id.progress_bar);
        // tvQuestionnaireTitle = view.findViewById(R.id.tv_questionnaire_title);
        // ivQuestionImage = view.findViewById(R.id.iv_question_image);
        // tvQuestionText = view.findViewById(R.id.tv_question_text);
        // radioGroupAnswers = view.findViewById(R.id.radio_group_answers);
        // rbExcellent = view.findViewById(R.id.rb_excellent);
        // rbGood = view.findViewById(R.id.rb_good);
        // rbOkay = view.findViewById(R.id.rb_okay);
        // rbPoor = view.findViewById(R.id.rb_poor);
        // rbVeryPoor = view.findViewById(R.id.rb_very_poor);

        // New UI references
        ivClose = view.findViewById(R.id.iv_close);
        tvQuestionCount = view.findViewById(R.id.tv_question_count);
        ivQuestionImage = view.findViewById(R.id.iv_question_image);
        tvQuestionText = view.findViewById(R.id.tv_question_text);
        btnNext = view.findViewById(R.id.btn_next);
        
        // References for option views
        View optionExcellent = view.findViewById(R.id.option_excellent);
        View optionGood = view.findViewById(R.id.option_good);
        View optionOkay = view.findViewById(R.id.option_okay);
        View optionPoor = view.findViewById(R.id.option_poor);
        View optionVeryPoor = view.findViewById(R.id.option_very_poor);
        
        // Set checkmark icons
        ImageView checkExcellent = view.findViewById(R.id.check_excellent);
        ImageView checkGood = view.findViewById(R.id.check_good);
        ImageView checkOkay = view.findViewById(R.id.check_okay);
        ImageView checkPoor = view.findViewById(R.id.check_poor);
        ImageView checkVeryPoor = view.findViewById(R.id.check_very_poor);
        
        checkExcellent.setImageResource(R.drawable.ic_check);
        checkGood.setImageResource(R.drawable.ic_check);
        checkOkay.setImageResource(R.drawable.ic_check);
        checkPoor.setImageResource(R.drawable.ic_check);
        checkVeryPoor.setImageResource(R.drawable.ic_check);

        // Set up back button click listener
        ivClose.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        
        // Set initial question count
        tvQuestionCount.setText("Question 01 of 05");
        
        // Initially disable the next button until an option is selected
        btnNext.setEnabled(false);
        
        // Set up click listeners for options
        optionExcellent.setOnClickListener(v -> selectOption(5));
        optionGood.setOnClickListener(v -> selectOption(4));
        optionOkay.setOnClickListener(v -> selectOption(3));
        optionPoor.setOnClickListener(v -> selectOption(2));
        optionVeryPoor.setOnClickListener(v -> selectOption(1));
        
        // Set initial image
        updateQuestionImage();
    }
    
    /**
     * Set up click listeners for interactive elements
     */
    private void setupClickListeners() {
        // Next button
        btnNext.setOnClickListener(v -> {
            // Check if an answer is selected
            if (currentSelectedOption == 0) {
                Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save answer
            answers[currentQuestionIndex] = currentSelectedOption;
            
            // If this is the last question, submit the questionnaire
            if (currentQuestionIndex == TOTAL_QUESTIONS - 1) {
                submitQuestionnaire();
            } else {
                // Otherwise, move to the next question
                currentQuestionIndex++;
                updateUI();
            }
        });
    }
    
    /**
     * Load questions from Firestore
     */
    private void loadQuestionsFromFirestore() {
        db.collection(FIRESTORE_COLLECTION_PATH)
                .document(questionnaireType)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get questions array from Firestore
                            List<Map<String, Object>> questionsFromDb = (List<Map<String, Object>>) documentSnapshot.get("questions");
                            
                            // Clear existing questions and add new ones
                            questions.clear();
                            if (questionsFromDb != null) {
                                for (Map<String, Object> questionMap : questionsFromDb) {
                                    String question = (String) questionMap.get("question");
                                    questions.add(question);
                                }
                            }
                            
                            // If we have less than 5 questions, add placeholder questions
                            while (questions.size() < TOTAL_QUESTIONS) {
                                questions.add("Default question " + (questions.size() + 1));
                            }
                            
                            // Update UI with the first question
                            updateUI();
                        } else {
                            // Document doesn't exist
                            Toast.makeText(getContext(), "Questionnaire not found", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error loading questions
                        Toast.makeText(getContext(), "Error loading questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });
    }
    
    /**
     * Update UI with current question
     */
    private void updateUI() {
        // Check if view is available
        if (getView() == null) {
            return;  // View is not attached, cannot update UI
        }
        
        // Update question count text
        TextView tvQuestionCount = getView().findViewById(R.id.tv_question_count);
        String questionCountText = String.format("Question %02d of %02d", 
                                         (currentQuestionIndex + 1), TOTAL_QUESTIONS);
        tvQuestionCount.setText(questionCountText);
        
        // Update question text
        if (questions.size() > currentQuestionIndex) {
            tvQuestionText.setText(questions.get(currentQuestionIndex));
        }
        
        // Update button text for last question
        if (currentQuestionIndex == TOTAL_QUESTIONS - 1) {
            btnNext.setText("Submit");
        } else {
            btnNext.setText("Next Question");
        }
        
        // Update question image
        updateQuestionImage();
        
        // Reset option selection and disable next button until an option is selected
        resetOptionSelection();
        currentSelectedOption = 0;
        previouslySelectedOption = null;
        btnNext.setEnabled(false);
    }
    
    /**
     * Update the question image based on type and current question index
     */
    private void updateQuestionImage() {
        // Check if view or image view is null
        if (ivQuestionImage == null) {
            return;
        }
        
        int imageResource;
        
        switch (questionnaireType) {
            case TYPE_ACADEMIC:
                imageResource = academicImages[currentQuestionIndex % academicImages.length];
                break;
            case TYPE_STRESS:
                imageResource = stressImages[currentQuestionIndex % stressImages.length];
                break;
            case TYPE_SLEEP:
                imageResource = sleepImages[currentQuestionIndex % sleepImages.length];
                break;
            default:
                imageResource = R.drawable.academic_score;
        }
        
        ivQuestionImage.setImageResource(imageResource);
    }
    
    /**
     * Select an answer option based on its value
     */
    private void selectOption(int optionValue) {
        // Check if view is available
        if (getView() == null) {
            return;  // View is not attached, cannot select option
        }
        
        // Save current answer
        answers[currentQuestionIndex] = optionValue;
        
        // Reset previous selection
        resetOptionSelection();
        
        // Get views
        View optionExcellent = getView().findViewById(R.id.option_excellent);
        View optionGood = getView().findViewById(R.id.option_good);
        View optionOkay = getView().findViewById(R.id.option_okay);
        View optionPoor = getView().findViewById(R.id.option_poor);
        View optionVeryPoor = getView().findViewById(R.id.option_very_poor);
        
        // Get checkmark ImageViews
        ImageView checkExcellent = getView().findViewById(R.id.check_excellent);
        ImageView checkGood = getView().findViewById(R.id.check_good);
        ImageView checkOkay = getView().findViewById(R.id.check_okay);
        ImageView checkPoor = getView().findViewById(R.id.check_poor);
        ImageView checkVeryPoor = getView().findViewById(R.id.check_very_poor);
        
        // Update all checkmarks to use the proper check icon
        checkExcellent.setImageResource(R.drawable.ic_check);
        checkGood.setImageResource(R.drawable.ic_check);
        checkOkay.setImageResource(R.drawable.ic_check);
        checkPoor.setImageResource(R.drawable.ic_check);
        checkVeryPoor.setImageResource(R.drawable.ic_check);
        
        // Mark new selection
        View selectedOption = null;
        ImageView selectedCheck = null;
        
        switch (optionValue) {
            case 5: // Excellent
                selectedOption = optionExcellent;
                selectedCheck = checkExcellent;
                break;
            case 4: // Good
                selectedOption = optionGood;
                selectedCheck = checkGood;
                break;
            case 3: // Okay
                selectedOption = optionOkay;
                selectedCheck = checkOkay;
                break;
            case 2: // Poor
                selectedOption = optionPoor;
                selectedCheck = checkPoor;
                break;
            case 1: // Very Poor
                selectedOption = optionVeryPoor;
                selectedCheck = checkVeryPoor;
                break;
        }
        
        if (selectedOption != null) {
            selectedOption.setSelected(true);
            previouslySelectedOption = selectedOption;
            currentSelectedOption = optionValue;
            
            // Show checkmark for selected option
            if (selectedCheck != null) {
                selectedCheck.setVisibility(View.VISIBLE);
            }
            
            // Enable next button
            btnNext.setEnabled(true);
        }
    }
    
    /**
     * Reset the currently selected option
     */
    private void resetOptionSelection() {
        // Reset previous selection if exists
        if (previouslySelectedOption != null) {
            previouslySelectedOption.setSelected(false);
        }
        
        // Hide all checkmarks
        if (getView() != null) {
            ImageView checkExcellent = getView().findViewById(R.id.check_excellent);
            ImageView checkGood = getView().findViewById(R.id.check_good);
            ImageView checkOkay = getView().findViewById(R.id.check_okay);
            ImageView checkPoor = getView().findViewById(R.id.check_poor);
            ImageView checkVeryPoor = getView().findViewById(R.id.check_very_poor);
            
            checkExcellent.setVisibility(View.INVISIBLE);
            checkGood.setVisibility(View.INVISIBLE);
            checkOkay.setVisibility(View.INVISIBLE);
            checkPoor.setVisibility(View.INVISIBLE);
            checkVeryPoor.setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * Submit the questionnaire and calculate score
     */
    private void submitQuestionnaire() {
        // Calculate total score
        int totalScore = 0;
        for (int answer : answers) {
            totalScore += answer;
        }
        
        // Calculate percentage (0-100)
        int percentageScore = totalScore * 100 / (TOTAL_QUESTIONS * 5);
        
        // Get appropriate tips based on score
        List<String> tips;
        if (percentageScore < 40) {
            tips = getTipsByRange("low");
        } else if (percentageScore < 75) {
            tips = getTipsByRange("medium");
        } else {
            tips = getTipsByRange("high");
        }
        
        // Save response to Firebase
        saveResponseToFirebase(questionnaireType, percentageScore);
        
        // Create and show the ScoreRecommendationFragment
        ScoreRecommendationFragment scoreFragment = ScoreRecommendationFragment.newInstance(
                questionnaireType, percentageScore, new ArrayList<>(tips));
        
        getParentFragmentManager()
            .beginTransaction()
            .replace(R.id.con, scoreFragment)
            .addToBackStack(null)
            .commit();
    }
    
    /**
     * Save questionnaire response to Firestore
     */
    private void saveResponseToFirebase(String category, int score) {
        // Check if user is logged in
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        // Convert answers array to map
        Map<String, String> answersMap = QuestionnaireResponse.convertAnswersToMap(answers);
        
        // Get appropriate tips based on score
        List<String> tips;
        if (score < 40) {
            tips = getTipsByRange("low");
        } else if (score < 75) {
            tips = getTipsByRange("medium");
        } else {
            tips = getTipsByRange("high");
        }
        
        // Create response as a map (following the requested structure)
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("category", category);
        responseData.put("date", currentDate);
        responseData.put("score", score);
        responseData.put("answers", answersMap);
        responseData.put("recommendations", tips);
        
        // Use FirestoreHelper to get reference to today's questionnaire document and save data
        firestoreHelper.getTodayQuestionnaireReference(category)
                .set(responseData)
                .addOnSuccessListener(aVoid -> {
                    // Response saved successfully
                    Toast.makeText(getContext(), "Response saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to save response
                    Toast.makeText(getContext(), "Error saving response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    /**
     * Get tips based on score range and questionnaire type
     */
    private List<String> getTipsByRange(String range) {
        switch (questionnaireType) {
            case TYPE_ACADEMIC:
                return academicTips.get(range);
            case TYPE_STRESS:
                return stressTips.get(range);
            case TYPE_SLEEP:
                return sleepTips.get(range);
            default:
                return academicTips.get(range);
        }
    }
    
    /**
     * Check if user has already completed this questionnaire today
     */
    private void checkTodaySubmission() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }
        
        // Use FirestoreHelper to check if questionnaire was completed today
        firestoreHelper.hasCompletedQuestionnaireToday(questionnaireType)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User has already completed this questionnaire today
                        Toast.makeText(getContext(), 
                                "You've already completed the " + questionnaireType + 
                                " questionnaire today. Please try again tomorrow.", 
                                Toast.LENGTH_LONG).show();
                                
                        // Go back to previous screen
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        // User hasn't completed this questionnaire today, load questions
                        loadQuestionsFromFirestore();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error checking submission - proceed with loading questions
                    Toast.makeText(getContext(), "Error checking previous submissions: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    loadQuestionsFromFirestore();
                });
    }
} 