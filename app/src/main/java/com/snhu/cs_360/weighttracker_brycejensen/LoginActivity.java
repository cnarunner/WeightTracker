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
 * LoginActivity class is responsible for handling the login functionality of the app.
 * It extends the AppCompatActivity class and implements the necessary methods and event listeners.
 */
public class LoginActivity extends AppCompatActivity {
    private DBHelper dbHelper; // Database helper object
    private EditText etUsername; // EditText for entering username
    private EditText etPassword; // EditText for entering password
    private MaterialButton btn_login; // Button for logging in
    private MaterialButton btn_createAccount; // Button for creating a new account
    private String justCreatedUsername; // String to store the username of a newly created account

    /**
     * This method is called when the activity is first created.
     * It initializes the views, sets up the database helper, and sets up event listeners for the buttons.
     *
     * @param savedInstanceState The saved instance state bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this); // Initialize the database helper

        // Initialize the views
        etUsername = findViewById(R.id.editTextUsername);
        etPassword = findViewById(R.id.editTextPassword);
        btn_login = findViewById(R.id.button_login);

        // Check if a new account was just created and set the username accordingly
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            justCreatedUsername = intent.getStringExtra("username");
        }
        etUsername.setText(justCreatedUsername);

        // Set up the login button click listener
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                // Check if the entered credentials are valid
                boolean isLoggedIn = dbHelper.checkCredentials(etUsername.getText().toString(),
                        etPassword.getText().toString());
                if (isLoggedIn) {
                    // If the credentials are valid, start the MainActivity and pass the username
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    // If the credentials are invalid, show a toast message
                    Toast.makeText(LoginActivity.this, "Login Failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize the create account button
        btn_createAccount = findViewById(R.id.button_createAccountActivity);
        btn_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RegisterActivity when the create account button is clicked
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
