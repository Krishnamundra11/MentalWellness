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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.krishna.navbar.utils.LoadingDialog;

public class SignupActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView loginText;
    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_AUTH_STATE = "auth_state";
    private static final String STATE_SIGNUP = "signup";
    private static final String STATE_COMPLETED = "completed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);
        
        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Set auth state to signup
        prefs.edit().putString(KEY_AUTH_STATE, STATE_SIGNUP).apply();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginText = findViewById(R.id.loginText);

        // Set click listeners
        signupButton.setOnClickListener(v -> {
            if (!loadingDialog.isShowing()) {
                registerUser();
            }
        });
        
        loginText.setOnClickListener(v -> {
            if (!loadingDialog.isShowing()) {
                // Clear auth state when returning to login
                prefs.edit().remove(KEY_AUTH_STATE).apply();
                finish();
            }
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return;
        }

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

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Please confirm your password");
            confirmPasswordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        // Show loading dialog
        loadingDialog.show();

        // Disable signup button
        signupButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success
                        // Update auth state to profile completion
                        prefs.edit().putString(KEY_AUTH_STATE, "profile_completion").apply();
                        startActivity(new Intent(SignupActivity.this, UserInfoActivity.class));
                        finishAffinity(); // Close all activities in the stack
                    } else {
                        // Sign up failed
                        String errorMessage = "Registration failed";
                        if (task.getException() != null) {
                            if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                errorMessage = "Password should be at least 6 characters";
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                errorMessage = "Email is already registered";
                            }
                        }
                        Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        
                        // Re-enable signup button
                        signupButton.setEnabled(true);
                    }
                    
                    // Dismiss loading dialog
                    loadingDialog.dismiss();
                });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Do not clear the auth state on destroy as we want to remember
        // where the user was in the flow if they close the app
    }
} 