package com.example.monkeytiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class youwin2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_youwin2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the flip count from the intent
        int flipCount = getIntent().getIntExtra("FLIP_COUNT", 0);

        // Find the TextView for flips in your layout
        TextView flipsTextView = findViewById(R.id.flipHard);

        // Set the text to display the flip count
        if (flipsTextView != null) {
            flipsTextView.setText("" + flipCount);
        }

        // Optional: Set up the Play Again button
        Button playAgainButton = findViewById(R.id.playagainbtn_youwin2);
        if (playAgainButton != null) {
            playAgainButton.setOnClickListener(v -> {
                Intent intent = new Intent(youwin2.this, hardnew.class);
                intent.putExtra("RESTART_GAME", true);
                startActivity(intent);
                finish();
            });
        }

        // Optional: Set up the Exit button
        Button exitButton = findViewById(R.id.exitbtn_youwin2);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> {
                Intent intent = new Intent(youwin2.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}