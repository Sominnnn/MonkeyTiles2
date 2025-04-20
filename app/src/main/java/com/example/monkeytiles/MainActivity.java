package com.example.monkeytiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.monkeytiles.ui.instruction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.easy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button startButton = findViewById(R.id.startbtn_home);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(MainActivity.this, choosedifficulty.class);
                startActivity(intent);
            }
        });

        Button catalogButton = findViewById(R.id.signupbtn_home);
        catalogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(MainActivity.this, category1.class);
                startActivity(intent);
            }
        });

        Button howtoplayButton = findViewById(R.id.catalogbtn_home);
        howtoplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(MainActivity.this, instruction.class);
                startActivity(intent);
            }
        });

        Button leaderboardButton = findViewById(R.id.leaderboardbtn_home);
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(MainActivity.this, leaderboard.class);
                startActivity(intent);
            }
        });
    }
}