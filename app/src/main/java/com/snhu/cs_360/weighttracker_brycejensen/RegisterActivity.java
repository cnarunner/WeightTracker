package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * RegisterActivity is an Android Activity class that handles user registration.
 * It provides a user interface for creating a new account by entering a username and password.
 * The activity also includes functionality to navigate to the LoginActivity and validate user input.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etRetypePassword;
    private MaterialButton btnCreateAccount;
    private MaterialButton btnGoToLogin;
    private DBHelper dbHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        etUsername = findViewById(R.id.editTextCreateUsername);
        etPassword = findViewById(R.id.editTextCreatePassword);
        etRetypePassword = findViewById(R.id.editTextRetypePassword);
        btnGoToLogin = findViewById(R.id.button_alreadyHaveAccount);
        btnCreateAccount = findViewById(R.id.button_createAccount);

        // Initialize database helper
        dbHelper = new DBHelper(this);

        // Set click listener for "Already Have an Account" button
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for "Create Account" button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    /**
     * Handles user registration process.
     * Validates user input, checks for existing username, and creates a new account if all conditions are met.
     */
    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String retypePassword = etRetypePassword.getText().toString().trim();

        // Validate user input
        if (username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Username is required!", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty() || retypePassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password is required!", Toast.LENGTH_SHORT).show();
        } else {
            // Check if passwords match
            if (retypePassword.equals(password)) {
                // Check if username already exists
                if (dbHelper.checkUsername(username)) {
                    Toast.makeText(RegisterActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with account creation
                    boolean successCreation = dbHelper.insertData(username, password);
                    if (successCreation) {
                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
