package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {
    DBHelper dbHelper;
    EditText etUsername;
    EditText etPassword;
    MaterialButton btn_login;
    MaterialButton btn_createAccount;
    String justCreatedUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        etUsername = findViewById(R.id.editTextUsername);
        etPassword = findViewById(R.id.editTextPassword);
        btn_login = findViewById(R.id.button_login);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            justCreatedUsername = intent.getStringExtra("username");
        }
        etUsername.setText(justCreatedUsername);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                boolean isLoggedIn = dbHelper.checkCredentials(etUsername.getText().toString(), 
                        etPassword.getText().toString());
                if (isLoggedIn) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_createAccount = findViewById(R.id.button_createAccountActivity);
        btn_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
