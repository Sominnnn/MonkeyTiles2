package com.example.monkeytiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signup extends AppCompatActivity {

    private EditText usernameEditText;
    private Button homeButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.easy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username_input);
        homeButton = findViewById(R.id.homebtn_signup);
        signUpButton = findViewById(R.id.signupbtn_signup);

        // Set up home button click listener
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main activity
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up sign up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the username from the input field
                String username = usernameEditText.getText().toString().trim();

                // Validate the username
                if (username.isEmpty()) {
                    Toast.makeText(signup.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the username to SharedPreferences for later use in leaderboards
                saveUsername(username);

                // Navigate to the game activity or back to main menu
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(signup.this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Saves the username to SharedPreferences for future use
     * @param username The username to save
     */
    private void saveUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("MonkeyMindMatchPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }
}