package com.example.monkeytiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class choosedifficulty extends AppCompatActivity {

    private String username;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choosedifficulty);

        // Fix the view ID to match your layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.easy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get username from intent or shared preferences
        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        } else {
            // Retrieve from SharedPreferences if not passed through intent
            SharedPreferences sharedPreferences = getSharedPreferences("MonkeyMindMatchPrefs", MODE_PRIVATE);
            username = sharedPreferences.getString("username", "Player");
        }

        // Initialize existing buttons
        Button pauseButton = findViewById(R.id.pausebtn_choosediff);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to pause activity
                Intent intent = new Intent(choosedifficulty.this, pause.class);
                intent.putExtra("username", username); // Pass username to maintain state
                startActivity(intent);
            }
        });

        Button homeButton = findViewById(R.id.homebtn_choosedif);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(choosedifficulty.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close this activity when going back to home
            }
        });

        Button easyButton = findViewById(R.id.easybtn_choosediff);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save difficulty preference
                saveGamePreference("easy");

                // Navigate to easy game activity
                Intent intent = new Intent(choosedifficulty.this, easynew.class);
                intent.putExtra("username", username); // Pass username to game activity
                startActivity(intent);
            }
        });

        Button mediumButton = findViewById(R.id.moderatebtn_choosediff);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save difficulty preference
                saveGamePreference("medium");

                // Navigate to medium game activity
                Intent intent = new Intent(choosedifficulty.this, mediumeasy.class);
                intent.putExtra("username", username); // Pass username to game activity
                startActivity(intent);
            }
        });

        Button hardButton = findViewById(R.id.hardbtn_choosediff);
        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save difficulty preference
                saveGamePreference("hard");

                // Navigate to hard game activity
                Intent intent = new Intent(choosedifficulty.this, hardnew.class);
                intent.putExtra("username", username); // Pass username to game activity
                startActivity(intent);
            }
        });
    }

    /**
     * Saves the game difficulty preference for future reference
     * @param difficulty The selected difficulty level
     */
    private void saveGamePreference(String difficulty) {
        SharedPreferences sharedPreferences = getSharedPreferences("MonkeyMindMatchPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("difficulty", difficulty);
        editor.putString("username", username); // Ensure username is always saved
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        // Navigate back to MainActivity when back button is pressed
        Intent intent = new Intent(choosedifficulty.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}