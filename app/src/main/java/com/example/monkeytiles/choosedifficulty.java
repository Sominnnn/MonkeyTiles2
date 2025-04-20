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

public class choosedifficulty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choosedifficulty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.easy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button pauseButton = findViewById(R.id.pausebtn_choosediff);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(choosedifficulty.this, pause.class);
                startActivity(intent);
            }
        });

        Button homeButton = findViewById(R.id.homebtn_choosedif);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(choosedifficulty.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button easyButton = findViewById(R.id.easybtn_choosediff);
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(choosedifficulty.this, easynew.class);
                startActivity(intent);
            }
        });

        Button mediumButton = findViewById(R.id.moderatebtn_choosediff);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(choosedifficulty.this, mediumeasy.class);
                startActivity(intent);
            }
        });

        Button hardButton = findViewById(R.id.hardbtn_choosediff);
        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CardCatalogActivity
                Intent intent = new Intent(choosedifficulty.this, hardnew.class);
                startActivity(intent);
            }
        });
    }
}