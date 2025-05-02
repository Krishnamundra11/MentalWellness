package com.krishna.navbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishna.navbar.utils.LoadingDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordText, signupText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LoadingDialog loadingDialog;
    
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_AUTH_STATE = "auth_state";
    private static final String STATE_LOGIN = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadingDialog = new LoadingDialog(this);
        
        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Set auth state to login
        prefs.edit().putString(KEY_AUTH_STATE, STATE_LOGIN).apply();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        signupText = findViewById(R.id.signupText);

        // Set click listeners
        loginButton.setOnClickListener(v -> {
            if (!loadingDialog.isShowing()) {
                loginUser();
            }
        });
        
        forgotPasswordText.setOnClickListener(v -> {
            if (!loadingDialog.isShowing()) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        
        signupText.setOnClickListener(v -> {
            if (!loadingDialog.isShowing()) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
        
        // Check if we need to restore a previous state
        checkSavedState();
    }
    
    private void checkSavedState() {
        String savedState = prefs.getString(KEY_AUTH_STATE, "");
        
        if (!savedState.isEmpty() && !savedState.equals(STATE_LOGIN)) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is signed in, check what state they were in
                if (savedState.equals("profile_completion")) {
                    // They were in the middle of completing their profile
                    startActivity(new Intent(LoginActivity.this, UserInfoActivity.class));
                    finish();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserProfileAndNavigate(currentUser);
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        // Show loading dialog
        loadingDialog.show();

        // Disable login button
        loginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserProfileAndNavigate(user);
                    } else {
                        // Sign in failed
                        String errorMessage = "Authentication failed";
                        if (task.getException() != null) {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                errorMessage = "No account found with this email";
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                errorMessage = "Invalid email or password";
                            }
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        
                        // Re-enable login button
                        loginButton.setEnabled(true);
                        
                        // Dismiss loading dialog
                        loadingDialog.dismiss();
                    }
                });
    }
    
    private void checkUserProfileAndNavigate(FirebaseUser user) {
        if (user != null) {
            // Check if user profile exists in Firestore
            db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    loadingDialog.dismiss();
                    
                    if (documentSnapshot.exists()) {
                        // User profile exists, go to main activity
                        prefs.edit().putString(KEY_AUTH_STATE, "completed").apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // User profile doesn't exist, go to user info activity to complete profile
                        prefs.edit().putString(KEY_AUTH_STATE, "profile_completion").apply();
                        Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Error checking profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        } else {
            loadingDialog.dismiss();
            loginButton.setEnabled(true);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Do not clear the auth state on destroy as we want to remember
        // where the user was in the flow if they close the app
    }
} 