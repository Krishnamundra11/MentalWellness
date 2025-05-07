package com.krishna.navbar.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.krishna.navbar.LoginActivity;
import com.krishna.navbar.R;
import com.krishna.navbar.StatisticsActivity;

import com.krishna.navbar.models.UserProfile;
import com.krishna.navbar.utils.FirestoreHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    // Add a field to hold the currently open edit dialog
    private Dialog editDialog;

    private CircleImageView profileImage;
    private TextView nameText, emailText, genderAgeText, professionText;
    private MaterialCardView therapyCard, analysisCard, musicCard;
    private MaterialCardView academicScoreCard, stressScoreCard, sleepScoreCard;
    private ProgressBar academicProgressBar, stressProgressBar, sleepProgressBar;
    private TextView academicScoreText, stressScoreText, sleepScoreText;
    private TextView academicScoreDescription, stressScoreDescription, sleepScoreDescription;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private Uri imageUri;
    private FirestoreHelper firestoreHelper;
    
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        firestoreHelper = new FirestoreHelper();
        
        // Register activity result launcher for image picking
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    // Update main profile image
                    profileImage.setImageURI(imageUri);
                    // If edit dialog is open, update its image view
                    if (editDialog != null) {
                        CircleImageView dialogImage = editDialog.findViewById(R.id.dialogProfileImage);
                        if (dialogImage != null) {
                            dialogImage.setImageURI(imageUri);
                        }
                    }
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        
        initViews(view);
        loadUserData();
        loadScores();
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        nameText = view.findViewById(R.id.nameText);
        emailText = view.findViewById(R.id.emailText);
        genderAgeText = view.findViewById(R.id.genderAgeText);
        professionText = view.findViewById(R.id.professionText);
        
        therapyCard = view.findViewById(R.id.therapyCard);
        analysisCard = view.findViewById(R.id.analysisCard);
        musicCard = view.findViewById(R.id.musicCard);
        
        academicScoreCard = view.findViewById(R.id.academicScoreCard);
        stressScoreCard = view.findViewById(R.id.stressScoreCard);
        sleepScoreCard = view.findViewById(R.id.sleepScoreCard);
        
        academicProgressBar = view.findViewById(R.id.academicProgressBar);
        stressProgressBar = view.findViewById(R.id.stressProgressBar);
        sleepProgressBar = view.findViewById(R.id.sleepProgressBar);
        
        academicScoreText = view.findViewById(R.id.academicScoreText);
        stressScoreText = view.findViewById(R.id.stressScoreText);
        sleepScoreText = view.findViewById(R.id.sleepScoreText);
        
        academicScoreDescription = view.findViewById(R.id.academicScoreDescription);
        stressScoreDescription = view.findViewById(R.id.stressScoreDescription);
        sleepScoreDescription = view.findViewById(R.id.sleepScoreDescription);

        ImageButton editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> showEditDialog());
        
        // Initialize logout button
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    
    private void loadUserData() {
        if (currentUser == null) return;

        // Load user data from Firestore
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                if (profile != null) {
                    nameText.setText(profile.getName());
                    emailText.setText(profile.getEmail());
                    genderAgeText.setText(String.format("%s, %d years", profile.getGender(), profile.getAge()));
                    professionText.setText(profile.getProfession());

                    // Load profile image using Glide
                    if (profile.getProfileImageURL() != null && !profile.getProfileImageURL().isEmpty()) {
                        Glide.with(this)
                                .load(profile.getProfileImageURL())
                                .placeholder(R.drawable.default_profile)
                                .into(profileImage);
                    }
                }
            }
        }).addOnFailureListener(e -> 
            Toast.makeText(getContext(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void loadScores() {
        // Load all category scores from Firestore
        loadCategoryScore("academic");
        loadCategoryScore("stress");
        loadCategoryScore("sleep");
    }
    
    private void loadCategoryScore(String category) {
        // Get current date
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String documentId = category + "-" + date;
        
        if (currentUser == null) return;

        // Load scores from Firestore
        db.collection("users")
                .document(currentUser.getUid())
                .collection("questionnaires")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get score
                        Long scoreObj = documentSnapshot.getLong("score");
                        int score = scoreObj != null ? scoreObj.intValue() : getDefaultScore(category);
                        
                        // Update UI based on category
                        updateScoreUI(category, score);
                    } else {
                        // Use default values if no scores for today
                        updateScoreUI(category, getDefaultScore(category));
                    }
                })
                .addOnFailureListener(e -> {
                    // Use default values on error
                    updateScoreUI(category, getDefaultScore(category));
                    Toast.makeText(getContext(), 
                            "Error loading " + category + " score: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }
    
    private int getDefaultScore(String category) {
        switch (category) {
            case "academic": return 25;
            case "stress": return 80;
            case "sleep": return 65;
            default: return 50;
        }
    }
    
    private void updateScoreUI(String category, int score) {
        switch (category) {
            case "academic":
                academicProgressBar.setProgress(score);
                academicScoreText.setText(score + "%");
                
                if (score < 30) {
                    academicScoreDescription.setText("Needs improvement");
                } else if (score < 70) {
                    academicScoreDescription.setText("Performing well");
                } else {
                    academicScoreDescription.setText("Excellent work!");
                }
                break;
                
            case "stress":
                stressProgressBar.setProgress(score);
                stressScoreText.setText(score + "%");
                
                if (score < 30) {
                    stressScoreDescription.setText("You're doing great!");
                } else if (score < 70) {
                    stressScoreDescription.setText("Moderate stress");
                } else {
                    stressScoreDescription.setText("High stress! Take care");
                }
                break;
                
            case "sleep":
                sleepProgressBar.setProgress(score);
                sleepScoreText.setText(score + "%");
                
                if (score < 30) {
                    sleepScoreDescription.setText("Poor sleep quality");
                } else if (score < 70) {
                    sleepScoreDescription.setText("Good sleep quality");
                } else {
                    sleepScoreDescription.setText("Excellent sleep!");
                }
                break;
        }
    }

    
private void setupClickListeners() {
    therapyCard.setOnClickListener(v -> {
        // Navigate to Therapy booking screen using FindExpertFragment
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.con, new FindExpertFragment())
                .addToBackStack(null)
                .commit();
        }
    });

    analysisCard.setOnClickListener(v -> {
        // Navigate to Statistics screen - keep using activity for now since we don't have a Stats fragment
        startActivity(new Intent(getActivity(), StatisticsActivity.class));
    });

    musicCard.setOnClickListener(v -> {
        // Navigate to Music Content screen using MusicMainFragment
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.con, new MusicMainFragment())
                .addToBackStack(null)
                .commit();
        }
    });
    
    
        
        // Add click listeners for score cards
        academicScoreCard.setOnClickListener(v -> {
            // Navigate to ScoreRecommendationFragment with academic category
            if (getActivity() != null) {
                Bundle args = new Bundle();
                args.putString("category", "academic");
                ScoreRecommendationFragment fragment = new ScoreRecommendationFragment();
                fragment.setArguments(args);
                
                getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.con, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
        
        stressScoreCard.setOnClickListener(v -> {
            // Navigate to ScoreRecommendationFragment with stress category
            if (getActivity() != null) {
                Bundle args = new Bundle();
                args.putString("category", "stress");
                ScoreRecommendationFragment fragment = new ScoreRecommendationFragment();
                fragment.setArguments(args);
                
                getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.con, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
        
        sleepScoreCard.setOnClickListener(v -> {
            // Navigate to ScoreRecommendationFragment with sleep category
            if (getActivity() != null) {
                Bundle args = new Bundle();
                args.putString("category", "sleep");
                ScoreRecommendationFragment fragment = new ScoreRecommendationFragment();
                fragment.setArguments(args);
                
                getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.con, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
    }

    private void showEditDialog() {
        if (getActivity() == null) return;
        
        final Dialog dialog = new Dialog(getActivity());
        this.editDialog = dialog; // keep reference to edit dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextInputEditText nameEditText = dialog.findViewById(R.id.nameEditText);
        TextInputEditText ageEditText = dialog.findViewById(R.id.ageEditText);
        AutoCompleteTextView genderDropdown = dialog.findViewById(R.id.genderDropdown);
        TextInputEditText emailEditText = dialog.findViewById(R.id.emailEditText);
        TextInputEditText professionEditText = dialog.findViewById(R.id.professionEditText);
        CircleImageView dialogProfileImage = dialog.findViewById(R.id.dialogProfileImage);
        ImageButton changeImageButton = dialog.findViewById(R.id.changeImageButton);
        MaterialButton saveButton = dialog.findViewById(R.id.saveButton);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        
        if (progressBar == null) {
            // Add a progress bar programmatically if it doesn't exist in the layout
            final ProgressBar newProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
            newProgressBar.setId(View.generateViewId());
            newProgressBar.setVisibility(View.GONE);
            ((LinearLayout) saveButton.getParent()).addView(newProgressBar);
        }

        // Setup gender dropdown
        String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        genderDropdown.setAdapter(adapter);

        // Load current values
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                if (profile != null) {
                    nameEditText.setText(profile.getName());
                    ageEditText.setText(String.valueOf(profile.getAge()));
                    genderDropdown.setText(profile.getGender(), false);
                    emailEditText.setText(profile.getEmail());
                    professionEditText.setText(profile.getProfession());
                    
                    // Load profile image
                    if (profile.getProfileImageURL() != null && !profile.getProfileImageURL().isEmpty()) {
                        Glide.with(getContext())
                                .load(profile.getProfileImageURL())
                                .placeholder(R.drawable.default_profile)
                                .into(dialogProfileImage);
                    }
                }
            }
        });

        // Image selection
        changeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        saveButton.setOnClickListener(v -> {
            // Validate fields
            String name = nameEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String gender = genderDropdown.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String profession = professionEditText.getText().toString().trim();

            // Validate input
            if (name.isEmpty()) {
                nameEditText.setError("Name is required");
                nameEditText.requestFocus();
                return;
            }

            if (ageStr.isEmpty()) {
                ageEditText.setError("Age is required");
                ageEditText.requestFocus();
                return;
            }

            if (gender.isEmpty()) {
                genderDropdown.setError("Gender is required");
                genderDropdown.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                emailEditText.setError("Email is required");
                emailEditText.requestFocus();
                return;
            }

            if (profession.isEmpty()) {
                professionEditText.setError("Profession is required");
                professionEditText.requestFocus();
                return;
            }

            // Get the final reference to progressBar for use in lambda
            final ProgressBar finalProgressBar = progressBar != null ? progressBar 
                : dialog.findViewById(R.id.progressBar);

            // Show progress
            if (finalProgressBar != null) finalProgressBar.setVisibility(View.VISIBLE);
            saveButton.setEnabled(false);
            saveButton.setText("Saving...");
            cancelButton.setEnabled(false);
            
            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age <= 0 || age > 120) {
                    ageEditText.setError("Please enter a valid age");
                    ageEditText.requestFocus();
                    if (finalProgressBar != null) finalProgressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    saveButton.setText("Save");
                    cancelButton.setEnabled(true);
                    return;
                }
            } catch (NumberFormatException e) {
                ageEditText.setError("Please enter a valid age");
                ageEditText.requestFocus();
                if (finalProgressBar != null) finalProgressBar.setVisibility(View.GONE);
                saveButton.setEnabled(true);
                saveButton.setText("Save");
                cancelButton.setEnabled(true);
                return;
            }

            if (imageUri != null) {
                // Upload new image and update profile
                uploadImageAndSaveProfile(dialog, name, age, gender, email, profession);
            } else {
                // Update profile without changing image
                updateProfile(dialog, name, age, gender, email, profession, null);
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnDismissListener(d -> {
            // clear reference when dialog is closed
            editDialog = null;
        });
        
        // Animation for dialog appearance
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.show();
    }

    private void uploadImageAndSaveProfile(Dialog dialog, String name, int age, String gender, String email, String profession) {
        StorageReference imageRef = storageRef.child("profileImages/" + currentUser.getUid() + ".jpg");
        
        // Get UI elements once to avoid accessing them in lambda
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final MaterialButton saveButton = dialog.findViewById(R.id.saveButton);
        final MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);
        
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> 
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> 
                        updateProfile(dialog, name, age, gender, email, profession, uri.toString())
                    )
                )
                .addOnFailureListener(e -> {
                    // Reset UI
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (saveButton != null) {
                        saveButton.setEnabled(true);
                        saveButton.setText("Save");
                    }
                    if (cancelButton != null) cancelButton.setEnabled(true);
                    
                    Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile(Dialog dialog, String name, int age, String gender, String email, String profession, String imageUrl) {
        // Get UI elements once to avoid accessing them in lambda
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final MaterialButton saveButton = dialog.findViewById(R.id.saveButton);
        final MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);
        
        UserProfile updatedProfile;
        
        if (imageUrl != null) {
            // New image URL
            updatedProfile = new UserProfile(name, age, gender, email, profession, imageUrl);
            saveProfileToFirestore(dialog, updatedProfile);
        } else {
            // Get existing image URL
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String existingImageUrl = documentSnapshot.getString("profileImageURL");
                    UserProfile profile = new UserProfile(name, age, gender, email, profession, existingImageUrl);
                    saveProfileToFirestore(dialog, profile);
                }
            }).addOnFailureListener(e -> {
                // Reset UI
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (saveButton != null) {
                    saveButton.setEnabled(true);
                    saveButton.setText("Save");
                }
                if (cancelButton != null) cancelButton.setEnabled(true);
                
                Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void saveProfileToFirestore(Dialog dialog, UserProfile profile) {
        // Get UI elements once to avoid accessing them in lambda
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final MaterialButton saveButton = dialog.findViewById(R.id.saveButton);
        final MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);
        
        db.collection("users").document(currentUser.getUid())
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadUserData(); // Reload profile data
                })
                .addOnFailureListener(e -> {
                    // Reset UI
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (saveButton != null) {
                        saveButton.setEnabled(true);
                        saveButton.setText("Save");
                    }
                    if (cancelButton != null) cancelButton.setEnabled(true);
                    
                    Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> performLogout())
            .setNegativeButton("No", null)
            .show();
    }
    
    private void performLogout() {
        if (mAuth != null) {
            mAuth.signOut();
            // Clear any stored user data
            if (getActivity() != null) {
                // Navigate to login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }
} 