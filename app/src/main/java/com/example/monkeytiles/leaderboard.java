package com.example.monkeytiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class leaderboard extends AppCompatActivity {
    TextView leaderboardText;
    String currentUsername;
    String[] difficulties = {"easy", "medium", "hard"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardText = findViewById(R.id.leaderboardText);
        currentUsername = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", "Guest");

        showLeaderboard();
    }

    private void showLeaderboard() {
        SharedPreferences preferences = getSharedPreferences("FlipCounts", MODE_PRIVATE);
        StringBuilder leaderboardData = new StringBuilder();

        for (String diff : difficulties) {
            String key = currentUsername + "_" + diff;
            int flips = preferences.getInt(key, -1);

            if (flips != -1) {
                leaderboardData.append(diff.toUpperCase())
                        .append(" - Flips: ").append(flips).append("\n");
            } else {
                leaderboardData.append(diff.toUpperCase())
                        .append(" - No score recorded\n");
            }
        }

        leaderboardText.setText(leaderboardData.toString());
    }
}

