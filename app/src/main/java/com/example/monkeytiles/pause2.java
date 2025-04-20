package com.example.monkeytiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class pause2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pause2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up continue button
        Button continueButton = findViewById(R.id.returnbtn_pause2);
        if (continueButton != null) {
            continueButton.setOnClickListener(v -> {
                // Simply finish this activity to return to the game
                // Add an animation to make the transition smoother
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Set up restart button
        Button restartButton = findViewById(R.id.restartbtn_pause2);
        if (restartButton != null) {
            restartButton.setOnClickListener(v -> {
                // Create a new intent for hardnew activity with a restart flag
                Intent intent = new Intent(pause2.this, hardnew.class);
                intent.putExtra("RESTART_GAME", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                finish(); // Close the pause activity
            });
        }

        // Set up home button (optional)
        Button homeButton = findViewById(R.id.homebtn_pause2);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(pause2.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                finish(); // Close the pause activity
            });
        }
    }

    @Override
    public void onBackPressed() {
        // Override back button to continue the game
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}