package com.example.monkeytiles;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;

public class pause extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pause);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.easy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button returnbutton = findViewById(R.id.startbtn_pause);
        returnbutton.setOnClickListener(v -> {
            Intent intent = new Intent(pause.this, easynew.class);
            startActivity(intent);
        });

        // In your pause activity (pause.java)
        Button restartButton = findViewById(R.id.restartbtn_pause);
        restartButton.setOnClickListener(v -> {
            // Create an intent to return to the game activity
            Intent intent = new Intent(pause.this, easynew.class);

            // Add extra data to signal that this is a restart
            intent.putExtra("RESTART_GAME", true);

            startActivity(intent);
            finish(); // Close the pause activity
        });


        Button homebutton = findViewById(R.id.homebtn_pause);
        homebutton.setOnClickListener(v -> {
            Intent intent = new Intent(pause.this, MainActivity.class);
            startActivity(intent);
        });
    }
}