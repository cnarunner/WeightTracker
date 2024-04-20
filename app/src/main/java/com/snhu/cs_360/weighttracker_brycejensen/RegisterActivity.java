package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {
    EditText etUsername;
    EditText etPassword;
    //EditText etPhoneNumber;
    EditText etRetypePassword;
    MaterialButton btnCreateAccount;
    MaterialButton btnGoToLogin;
    DBHelper dbHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.editTextCreateUsername);
        etPassword = findViewById(R.id.editTextCreatePassword);
        //etPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        etRetypePassword = findViewById(R.id.editTextRetypePassword);

        btnGoToLogin = findViewById(R.id.button_alreadyHaveAccount);
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnCreateAccount = findViewById(R.id.button_createAccount);
        dbHelper = new DBHelper(this);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username;
                String password;
                //String phoneNumber;
                String retypePassword;

                username = etUsername.getText().toString();
                password = etPassword.getText().toString();

                retypePassword = etRetypePassword.getText().toString();

                if (username.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Username is required!", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty() || retypePassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password is required!", Toast.LENGTH_SHORT).show();
                } else {
                    if (retypePassword.equals(password)) {
                        if (dbHelper.checkUsername(username)) {
                            Toast.makeText(RegisterActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                        }
                        // Proceed with account creation
                        boolean successCreation = dbHelper.insertData(username, password);
                        if (successCreation) {
                            Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);

                            // Get the phone number from the EditText (if provided)
                            //phoneNumber = etPhoneNumber.getText().toString();

                            // Store the phone number in the database
                            //dbHelper.updatePhoneNumber(username, phoneNumber);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
