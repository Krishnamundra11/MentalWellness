package com.krishna.navbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.krishna.navbar.utils.LoadingDialog;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetButton;
    private TextView loginText;
    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);
        loginText = findViewById(R.id.loginText);

        // Set click listeners
        resetButton.setOnClickListener(v -> {
            if (!loadingDialog.isShowing()) {
                resetPassword();
            }
        });
        
        loginText.setOnClickListener(v -> {
            if (!loadingDialog.isShowing()) {
                finish();
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        // Show loading dialog
        loadingDialog.show();

        // Disable reset button
        resetButton.setEnabled(false);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, 
                            "Password reset email sent. Please check your inbox.", 
                            Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String errorMessage = "Failed to send reset email";
                        if (task.getException() != null) {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                errorMessage = "No account found with this email";
                            }
                        }
                        Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        
                        // Re-enable reset button
                        resetButton.setEnabled(true);
                    }
                    
                    // Dismiss loading dialog
                    loadingDialog.dismiss();
                });
    }
} 