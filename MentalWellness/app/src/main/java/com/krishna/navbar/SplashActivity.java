package com.krishna.navbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check authentication state after splash duration
        new Handler().postDelayed(() -> {
            checkUserAndRedirect();
        }, SPLASH_DURATION);
    }
    
    private void checkUserAndRedirect() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            // User is signed in, check if they have completed their profile
            db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Intent intent;
                    if (documentSnapshot.exists()) {
                        // User profile exists, go to MainActivity
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        // User is signed in but profile is not completed
                        intent = new Intent(SplashActivity.this, UserInfoActivity.class);
                        Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Error checking profile, default to login
                    Toast.makeText(this, "Error checking profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
        } else {
            // No user is signed in, go to LoginActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
} 