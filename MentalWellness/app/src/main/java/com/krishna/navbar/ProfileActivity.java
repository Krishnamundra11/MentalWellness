package com.krishna.navbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView nameText, emailText, genderAgeText, professionText;
    private MaterialCardView therapyCard, analysisCard, musicCard;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        genderAgeText = findViewById(R.id.genderAgeText);
        professionText = findViewById(R.id.professionText);
        
        therapyCard = findViewById(R.id.therapyCard);
        analysisCard = findViewById(R.id.analysisCard);
        musicCard = findViewById(R.id.musicCard);

        ImageButton editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> showEditDialog());
    }

    private void loadUserData() {
        if (currentUser == null) return;

        // Load profile image using Glide
        String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.default_profile)
                    .into(profileImage);
        }

        // Set email
        emailText.setText(currentUser.getEmail());

        // Load user data from Firestore
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String gender = documentSnapshot.getString("gender");
                Long age = documentSnapshot.getLong("age");
                String profession = documentSnapshot.getString("profession");

                nameText.setText(name);
                genderAgeText.setText(String.format("%s, %d years", gender, age));
                professionText.setText(profession);
            }
        }).addOnFailureListener(e -> 
            Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupClickListeners() {
        therapyCard.setOnClickListener(v -> {
            // Navigate to Therapy booking screen
            startActivity(new Intent(ProfileActivity.this, TherapyActivity.class));
        });

        analysisCard.setOnClickListener(v -> {
            // Navigate to Mood Tracking screen
            startActivity(new Intent(ProfileActivity.this, MoodTrackingActivity.class));
        });

        musicCard.setOnClickListener(v -> {
            // Navigate to Wellness Library screen
            startActivity(new Intent(ProfileActivity.this, WellnessLibraryActivity.class));
        });
    }

    private void showEditDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_profile);

        TextInputEditText nameEditText = dialog.findViewById(R.id.nameEditText);
        TextInputEditText ageEditText = dialog.findViewById(R.id.ageEditText);
        AutoCompleteTextView genderDropdown = dialog.findViewById(R.id.genderDropdown);
        TextInputEditText professionEditText = dialog.findViewById(R.id.professionEditText);
        MaterialButton saveButton = dialog.findViewById(R.id.saveButton);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancelButton);

        // Setup gender dropdown
        String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        genderDropdown.setAdapter(adapter);

        // Load current values
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                nameEditText.setText(documentSnapshot.getString("name"));
                Long age = documentSnapshot.getLong("age");
                if (age != null) {
                    ageEditText.setText(String.valueOf(age));
                }
                genderDropdown.setText(documentSnapshot.getString("gender"), false);
                professionEditText.setText(documentSnapshot.getString("profession"));
            }
        });

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            String gender = genderDropdown.getText().toString().trim();
            String profession = professionEditText.getText().toString().trim();

            if (name.isEmpty() || ageStr.isEmpty() || gender.isEmpty() || profession.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("age", Integer.parseInt(ageStr));
            updates.put("gender", gender);
            updates.put("profession", profession);

            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        loadUserData();
                        dialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show()
                    );
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
} 