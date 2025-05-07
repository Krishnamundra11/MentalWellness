package com.krishna.navbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.krishna.navbar.models.UserProfile;
import com.krishna.navbar.utils.UserUtils;

import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UserInfoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "UserInfoActivity"; // Add tag for consistent logging

    private CircleImageView profileImageView;
    private TextInputEditText nameEdit, ageEdit, emailEdit, professionEdit;
    private AutoCompleteTextView genderEdit;
    private MaterialButton saveButton;
    private ProgressBar progressBar;
    private TextView welcomeText;
    private Uri imageUri;
    private Bitmap selectedBitmap = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String currentUserId;
    private boolean isProfileComplete = false; // Flag to track profile completion
    
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_AUTH_STATE = "auth_state";
    private static final String STATE_PROFILE_COMPLETION = "profile_completion";
    private static final String STATE_COMPLETED = "completed";

    private UserUtils userUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        
        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Set auth state to profile completion
        prefs.edit().putString(KEY_AUTH_STATE, STATE_PROFILE_COMPLETION).apply();
        
        // Initialize Firebase and UserUtils
        initializeFirebase();
        userUtils = UserUtils.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to login
            prefs.edit().remove(KEY_AUTH_STATE).apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = currentUser.getUid();
        Log.d(TAG, "Current user ID: " + currentUserId);

        // Initialize views
        initializeViews();
        
        // Set welcome message using UserUtils
        userUtils.getUserName(this, new UserUtils.OnNameFetched() {
            @Override
            public void onNameFetched(String name) {
                welcomeText.setText("Welcome, " + name + "!");
            }
            
            @Override
            public void onError(String error) {
                // Fallback to email username
                String email = currentUser.getEmail();
                if (email != null) {
                    String[] parts = email.split("@");
                    String username = parts.length > 0 ? parts[0] : "";
                    if (!username.isEmpty()) {
                        username = username.substring(0, 1).toUpperCase() + username.substring(1);
                        welcomeText.setText("Welcome, " + username + "!");
                    } else {
                        welcomeText.setText("Welcome!");
                    }
                } else {
                    welcomeText.setText("Welcome!");
                }
            }
        });
        
        // Pre-fill email
        emailEdit.setText(currentUser.getEmail());
        // Disable email editing since it comes from authentication
        emailEdit.setEnabled(false);

        // Setup gender dropdown
        setupGenderDropdown();
        
        // Set click listeners
        setClickListeners();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        
        // Make sure to get the storage reference properly
        storageRef = storage.getReference();
        
        // Log the storage bucket for debugging
        Log.d(TAG, "Firebase Storage Bucket: " + storage.getReference().getBucket());
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        nameEdit = findViewById(R.id.editTextName);
        ageEdit = findViewById(R.id.editTextAge);
        genderEdit = findViewById(R.id.editTextGender);
        emailEdit = findViewById(R.id.editTextEmail);
        professionEdit = findViewById(R.id.editTextProfession);
        saveButton = findViewById(R.id.btnSaveProfile);
        progressBar = findViewById(R.id.progressBar);
        welcomeText = findViewById(R.id.welcomeText);
    }
    
    private void setupGenderDropdown() {
        String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        genderEdit.setAdapter(adapter);
    }

    private void setClickListeners() {
        findViewById(R.id.btnChangeImage).setOnClickListener(v -> openImagePicker());
        
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                showProgress(true);
                saveProfile();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        String name = nameEdit.getText().toString().trim();
        String ageStr = ageEdit.getText().toString().trim();
        String gender = genderEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String profession = professionEdit.getText().toString().trim();
        
        if (TextUtils.isEmpty(name)) {
            nameEdit.setError("Name is required");
            nameEdit.requestFocus();
            isValid = false;
        }
        
        if (TextUtils.isEmpty(ageStr)) {
            ageEdit.setError("Age is required");
            ageEdit.requestFocus();
            isValid = false;
        } else {
            try {
                int age = Integer.parseInt(ageStr);
                if (age <= 0 || age > 120) {
                    ageEdit.setError("Please enter a valid age");
                    ageEdit.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                ageEdit.setError("Please enter a valid age");
                ageEdit.requestFocus();
                isValid = false;
            }
        }
        
        if (TextUtils.isEmpty(gender)) {
            genderEdit.setError("Gender is required");
            genderEdit.requestFocus();
            isValid = false;
        }
        
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Email is required");
            emailEdit.requestFocus();
            isValid = false;
        }
        
        if (TextUtils.isEmpty(profession)) {
            professionEdit.setError("Profession is required");
            professionEdit.requestFocus();
            isValid = false;
        }
        
        // We no longer require an image since we handle this in saveProfile
        // with the option to use a default image
        
        return isValid;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri selectedImageUri = data.getData();
                // Try to access the image content and load bitmap immediately
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                if (inputStream == null) {
                    throw new Exception("Could not open image stream");
                }
                
                // Decode and store the b   itmap
                selectedBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                
                if (selectedBitmap == null) {
                    throw new Exception("Could not decode image");
                }
                
                // Store the URI for later use
                imageUri = selectedImageUri;
                
                // Display the selected image
                profileImageView.setImageBitmap(selectedBitmap);
                Log.d(TAG, "Image selected and decoded successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error accessing or decoding selected image: " + e.getMessage(), e);
                Toast.makeText(this, "Unable to process the selected image. Please try another.", Toast.LENGTH_LONG).show();
                imageUri = null;
                selectedBitmap = null;
            }
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!show);
        saveButton.setText(show ? "Creating Profile..." : "Continue");
        
        // Disable all input fields during processing
        nameEdit.setEnabled(!show);
        ageEdit.setEnabled(!show);
        genderEdit.setEnabled(!show);
        emailEdit.setEnabled(!show);
        professionEdit.setEnabled(!show);
        findViewById(R.id.btnChangeImage).setEnabled(!show);
    }

    private void saveProfile() {
        String name = nameEdit.getText().toString().trim();
        String ageStr = ageEdit.getText().toString().trim();
        String gender = genderEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String profession = professionEdit.getText().toString().trim();

        int age = Integer.parseInt(ageStr);
        
        // Check if imageUri is null and show an option to proceed without image
        if (imageUri == null && selectedBitmap == null) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("No Profile Image")
                    .setMessage("You haven't selected a profile image. Would you like to select one now or continue without an image?")
                    .setPositiveButton("Select Image", (dialog, which) -> {
                        dialog.dismiss();
                        showProgress(false);
                        openImagePicker();
                    })
                    .setNegativeButton("Continue Without Image", (dialog, which) -> {
                        dialog.dismiss();
                        // Proceed with a default image URL
                        saveProfileWithDefaultImage(name, age, gender, email, profession);
                    })
                    .setCancelable(true)
                    .show();
        } else {
            uploadImageAndSaveProfile(name, age, gender, email, profession);
        }
    }

    private void saveProfileWithDefaultImage(String name, int age, String gender, String email, String profession) {
        // Use a publicly accessible default profile image URL
        String defaultImageUrl = "https://ui-avatars.com/api/?name=" + Uri.encode(name) + "&background=random";
        
        // Log the action
        Log.d(TAG, "Using default profile image from UI Avatars: " + defaultImageUrl);
        
        // Save profile with default image
        saveProfileData(name, age, gender, email, profession, defaultImageUrl);
    }

    private void uploadImageAndSaveProfile(String name, int age, String gender, String email, String profession) {
        if (imageUri == null && selectedBitmap == null) {
            showProgress(false);
            Toast.makeText(this, "Please select a profile image before saving.", Toast.LENGTH_SHORT).show();
            return; // Exit if no image is selected
        }

        try {
            // Instead of using the URI directly, we'll load the image as a bitmap and upload the bitmap
            uploadBitmapAndSaveProfile(name, age, gender, email, profession);
        } catch (Exception e) {
            Log.e(TAG, "Error preparing image for upload: " + e.getMessage(), e);
            showProgress(false);
            Toast.makeText(this, "Error preparing image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadBitmapAndSaveProfile(String name, int age, String gender, String email, String profession) {
        try {
            Bitmap bitmapToUpload;
            
            // Use the stored bitmap if available, otherwise load from URI
            if (selectedBitmap != null) {
                bitmapToUpload = selectedBitmap;
                Log.d(TAG, "Using pre-loaded bitmap for upload");
            } else if (imageUri != null) {
                // Get input stream from URI
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    throw new Exception("Could not open image stream");
                }
                
                // Decode the image to a bitmap
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                
                if (originalBitmap == null) {
                    throw new Exception("Could not decode image");
                }
                
                bitmapToUpload = originalBitmap;
                Log.d(TAG, "Loaded bitmap from URI for upload");
            } else {
                throw new Exception("No image available to upload");
            }
            
            // Compress the bitmap
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapToUpload, 500, 
                    (int) (500 * ((float) bitmapToUpload.getHeight() / bitmapToUpload.getWidth())), true);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageData = baos.toByteArray();
            
            // Create storage reference with timestamp to ensure uniqueness
            String timestamp = String.valueOf(System.currentTimeMillis());
            StorageReference imageRef = storageRef.child("profileImages")
                                             .child(currentUserId)
                                             .child(timestamp + ".jpg");
            
            Log.d(TAG, "Uploading to path: " + imageRef.getPath());
            Log.d(TAG, "Uploading compressed image: " + imageData.length + " bytes");
            
            // Upload the byte array directly
            imageRef.putBytes(imageData)
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    Log.d(TAG, "Upload progress: " + progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Upload successful, getting download URL");
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d(TAG, "Download URL obtained: " + uri.toString());
                        saveProfileData(name, age, gender, email, profession, uri.toString());
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                        showProgress(false);
                        Toast.makeText(UserInfoActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload failed: " + e.getMessage(), e);
                    showProgress(false);
                    Toast.makeText(UserInfoActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    
                    // If upload fails, offer to use default image
                    offerDefaultImageOption(name, age, gender, email, profession);
                });
        } catch (Exception e) {
            Log.e(TAG, "Error processing image: " + e.getMessage(), e);
            showProgress(false);
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // If processing fails, offer to use default image
            offerDefaultImageOption(name, age, gender, email, profession);
        }
    }

    private void offerDefaultImageOption(String name, int age, String gender, String email, String profession) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Upload Failed")
                .setMessage("Failed to upload your image. Would you like to try again or continue with a default profile image?")
                .setPositiveButton("Try Again", (dialog, which) -> {
                    dialog.dismiss();
                    showProgress(false);
                    openImagePicker();
                })
                .setNegativeButton("Use Default Image", (dialog, which) -> {
                    dialog.dismiss();
                    saveProfileWithDefaultImage(name, age, gender, email, profession);
                })
                .setCancelable(false)
                .show();
    }

    private void saveProfileData(String name, int age, String gender, String email, String profession, String imageUrl) {
        UserProfile profile = new UserProfile(name, age, gender, email, profession, imageUrl);
        
        db.collection("users").document(currentUserId)
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserInfoActivity.this, "Profile created successfully", Toast.LENGTH_SHORT).show();
                    isProfileComplete = true;
                    
                    // Update auth state to completed
                    prefs.edit().putString(KEY_AUTH_STATE, STATE_COMPLETED).apply();
                    
                    // Navigate to main app screen
                    Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(UserInfoActivity.this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        if (!isProfileComplete) {
            // Show a message to prevent going back
            Toast.makeText(this, "Please complete your profile before leaving.", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed(); // Allow back navigation if profile is complete
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Do not clear the auth state on destroy as we want to remember
        // where the user was in the flow if they close the app
    }
}