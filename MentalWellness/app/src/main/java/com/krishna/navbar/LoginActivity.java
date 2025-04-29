package com.krishna.navbar;

import android.content.Intent;
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
import com.krishna.navbar.utils.LoadingDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordText, signupText;
    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
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
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
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
                    }
                    
                    // Dismiss loading dialog
                    loadingDialog.dismiss();
                });
    }
} 