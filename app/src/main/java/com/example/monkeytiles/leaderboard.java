package com.example.monkeytiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class leaderboard extends AppCompatActivity {

    private LinearLayout leaderboardContainer;
    private String currentDifficulty = "all"; // Default to show all difficulties

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        leaderboardContainer = findViewById(R.id.leaderboard_container);
        Button pauseButton = findViewById(R.id.pausebtn_leaderboard);

        // Check if a specific difficulty was passed
        if (getIntent().hasExtra("difficulty")) {
            currentDifficulty = getIntent().getStringExtra("difficulty");
        }

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(leaderboard.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Load and display leaderboard data
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        // Clear existing entries
        leaderboardContainer.removeAllViews();

        // Get all leaderboard entries from SharedPreferences
        SharedPreferences scoresPrefs = getSharedPreferences("MonkeyMindMatchScores", MODE_PRIVATE);

        List<LeaderboardEntry> entries = new ArrayList<>();

        // Get all score keys
        Map<String, ?> allScores = scoresPrefs.getAll();

        for (Map.Entry<String, ?> entry : allScores.entrySet()) {
            String key = entry.getKey();
            // Format expected: username_difficulty
            String[] parts = key.split("_");

            if (parts.length >= 2) {
                String username = parts[0];
                String difficulty = parts[1];

                // Filter by difficulty if needed
                if (!currentDifficulty.equals("all") && !difficulty.equals(currentDifficulty)) {
                    continue;
                }

                int flips = scoresPrefs.getInt(key, 0);
                if (flips > 0) { // Only add valid scores
                    entries.add(new LeaderboardEntry(username, difficulty, flips));
                }
            }
        }

        // Sort entries by flips (ascending - fewer flips is better)
        Collections.sort(entries, new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry o1, LeaderboardEntry o2) {
                return Integer.compare(o1.flips, o2.flips);
            }
        });

        // Display entries in the layout
        int count = 0;
        for (LeaderboardEntry entry : entries) {
            if (count >= 10) break; // Show only top 10

            // For simplicity, creating text views directly
            // You can replace this with inflating a custom layout if desired
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(16, 16, 16, 16);

            // Name column
            TextView nameView = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            nameView.setLayoutParams(nameParams);
            nameView.setText(entry.username);
            nameView.setTextSize(16);

            // Flips/Time column
            TextView scoreView = new TextView(this);
            LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            scoreView.setLayoutParams(scoreParams);
            scoreView.setText(String.valueOf(entry.flips) + " flips");
            scoreView.setTextSize(16);
            scoreView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            rowLayout.addView(nameView);
            rowLayout.addView(scoreView);

            leaderboardContainer.addView(rowLayout);

            // Add a divider
            View divider = new View(this);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2);
            dividerParams.setMargins(0, 8, 0, 8);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

            leaderboardContainer.addView(divider);

            count++;
        }

        // Show a message if no entries found
        if (entries.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No scores recorded yet. Play a game first!");
            noDataText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noDataText.setTextSize(18);
            noDataText.setPadding(16, 32, 16, 32);
            leaderboardContainer.addView(noDataText);
        }
    }

    // Helper class to store leaderboard data
    private static class LeaderboardEntry {
        String username;
        String difficulty;
        int flips;

        LeaderboardEntry(String username, String difficulty, int flips) {
            this.username = username;
            this.difficulty = difficulty;
            this.flips = flips;
        }
    }

    @Override
    public void onBackPressed() {
        // Navigate back to difficulty selection or main activity
        Intent intent = new Intent(leaderboard.this, choosedifficulty.class);
        startActivity(intent);
        finish();
    }
}