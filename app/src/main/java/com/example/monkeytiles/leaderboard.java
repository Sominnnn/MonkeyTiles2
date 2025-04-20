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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class leaderboard extends AppCompatActivity {

    private LinearLayout leaderboardContainer;
    private String currentDifficulty = "all"; // Default to show all difficulties
    private static final String TAG = "LeaderboardActivity"; // For logging

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

        // Get the current username for highlighting in the leaderboard
        SharedPreferences userPrefs = getSharedPreferences("MonkeyMindMatchPrefs", MODE_PRIVATE);
        String currentUsername = userPrefs.getString("username", "");
        Log.d(TAG, "Current user is: " + currentUsername);

        // FOR TESTING ONLY: Create test scores if no scores exist
        addTestScoresIfNeeded(currentUsername);

        // Check if a specific difficulty was passed
        if (getIntent().hasExtra("difficulty")) {
            currentDifficulty = getIntent().getStringExtra("difficulty");
            Log.d(TAG, "Showing leaderboard for difficulty: " + currentDifficulty);
        } else {
            Log.d(TAG, "No specific difficulty provided, showing all scores");
        }

        // Add a TextView to show the current filter
        TextView filterInfo = new TextView(this);
        filterInfo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        filterInfo.setPadding(16, 16, 16, 16);
        filterInfo.setTextSize(16);
        filterInfo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        filterInfo.setText("Showing scores for: " +
                (currentDifficulty.equals("all") ? "All Difficulties" :
                        currentDifficulty.substring(0, 1).toUpperCase() + currentDifficulty.substring(1)));

        leaderboardContainer.addView(filterInfo);

        // Set up pause button
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(leaderboard.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Add filter buttons
        setupFilterButtons();

        // Create table header
        createTableHeader();

        // Load and display leaderboard data
        loadLeaderboard(currentUsername);
    }

    /**
     * FOR TESTING ONLY: This method adds test scores for the current user if no scores exist
     * In a real app, you would remove this and rely on scores from actual gameplay
     */
    private void addTestScoresIfNeeded(String currentUsername) {
        if (currentUsername.isEmpty()) {
            Log.d(TAG, "No current username, skipping test score creation");
            return;
        }

        SharedPreferences scorePrefs = getSharedPreferences("MonkeyMindMatchScores", MODE_PRIVATE);
        Map<String, ?> allScores = scorePrefs.getAll();

        // Only add test scores if none exist at all
        if (allScores.isEmpty()) {
            Log.d(TAG, "No scores found, creating test scores for current user");

            // Create test scores with the exact username format
            SharedPreferences.Editor editor = scorePrefs.edit();
            editor.putInt(currentUsername + "_easy", 20);
            editor.putInt(currentUsername + "_normal", 35);
            editor.putInt(currentUsername + "_hard", 50);
            editor.apply();

            // Verify scores were added
            Map<String, ?> updatedScores = scorePrefs.getAll();
            Log.d(TAG, "Added test scores, now have " + updatedScores.size() + " scores");

            Toast.makeText(this, "Created test scores for demonstration", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFilterButtons() {
        LinearLayout filterButtons = new LinearLayout(this);
        filterButtons.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        filterButtons.setOrientation(LinearLayout.HORIZONTAL);
        filterButtons.setPadding(16, 8, 16, 16);

        // All button
        Button allBtn = new Button(this);
        allBtn.setText("All");
        allBtn.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        allBtn.setOnClickListener(v -> {
            Intent intent = new Intent(leaderboard.this, leaderboard.class);
            intent.putExtra("difficulty", "all");
            startActivity(intent);
            finish();
        });

        // Easy button
        Button easyBtn = new Button(this);
        easyBtn.setText("Easy");
        easyBtn.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        easyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(leaderboard.this, leaderboard.class);
            intent.putExtra("difficulty", "easy");
            startActivity(intent);
            finish();
        });

        // Normal button
        Button normalBtn = new Button(this);
        normalBtn.setText("Normal");
        normalBtn.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        normalBtn.setOnClickListener(v -> {
            Intent intent = new Intent(leaderboard.this, leaderboard.class);
            intent.putExtra("difficulty", "normal");
            startActivity(intent);
            finish();
        });

        // Hard button
        Button hardBtn = new Button(this);
        hardBtn.setText("Hard");
        hardBtn.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        hardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(leaderboard.this, leaderboard.class);
            intent.putExtra("difficulty", "hard");
            startActivity(intent);
            finish();
        });

        filterButtons.addView(allBtn);
        filterButtons.addView(easyBtn);
        filterButtons.addView(normalBtn);
        filterButtons.addView(hardBtn);

        leaderboardContainer.addView(filterButtons);
    }

    private void createTableHeader() {
        // Header row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setPadding(16, 24, 16, 8);

        // Rank header
        TextView rankHeader = new TextView(this);
        rankHeader.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
        rankHeader.setText("Rank");
        rankHeader.setTextSize(16);
        rankHeader.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        rankHeader.setTextColor(getResources().getColor(android.R.color.black));

        // Name header
        TextView nameHeader = new TextView(this);
        nameHeader.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
        nameHeader.setText("Player");
        nameHeader.setTextSize(16);
        nameHeader.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        nameHeader.setTextColor(getResources().getColor(android.R.color.black));

        // Difficulty header (only for "all" view)
        TextView diffHeader = null;
        if (currentDifficulty.equals("all")) {
            diffHeader = new TextView(this);
            diffHeader.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            diffHeader.setText("Difficulty");
            diffHeader.setTextSize(16);
            diffHeader.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            diffHeader.setTextColor(getResources().getColor(android.R.color.black));
        }

        // Score header
        TextView scoreHeader = new TextView(this);
        scoreHeader.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
        scoreHeader.setText("Score");
        scoreHeader.setTextSize(16);
        scoreHeader.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        scoreHeader.setTextColor(getResources().getColor(android.R.color.black));

        headerRow.addView(rankHeader);
        headerRow.addView(nameHeader);
        if (diffHeader != null) {
            headerRow.addView(diffHeader);
        }
        headerRow.addView(scoreHeader);

        leaderboardContainer.addView(headerRow);

        // Divider after header
        View headerDivider = new View(this);
        headerDivider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        headerDivider.setBackgroundColor(getResources().getColor(android.R.color.black));
        leaderboardContainer.addView(headerDivider);
    }

    private void loadLeaderboard(String currentUsername) {
        // Get all leaderboard entries from SharedPreferences
        SharedPreferences scoresPrefs = getSharedPreferences("MonkeyMindMatchScores", MODE_PRIVATE);

        // Debug: Print out all entries in SharedPreferences
        Map<String, ?> allScores = scoresPrefs.getAll();
        Log.d(TAG, "Found " + allScores.size() + " entries in SharedPreferences");
        for (Map.Entry<String, ?> entry : allScores.entrySet()) {
            Log.d(TAG, "Key=" + entry.getKey() + ", Value=" + entry.getValue().toString());
        }

        // Debug: Print out the current username for verification
        Log.d(TAG, "Current username that should be highlighted: " + currentUsername);

        List<LeaderboardEntry> entries = new ArrayList<>();

        // Track if current user has any scores
        boolean currentUserHasScore = false;

        for (Map.Entry<String, ?> entry : allScores.entrySet()) {
            String key = entry.getKey();

            // Check if the key has the correct format (username_difficulty)
            if (!key.contains("_")) {
                Log.d(TAG, "Skipping invalid key format: " + key);
                continue;
            }

            // Split the key on the first underscore only
            int underscoreIndex = key.indexOf("_");
            if (underscoreIndex <= 0 || underscoreIndex >= key.length() - 1) {
                Log.d(TAG, "Skipping key with invalid underscore position: " + key);
                continue;
            }

            String username = key.substring(0, underscoreIndex);
            String difficulty = key.substring(underscoreIndex + 1);

            Log.d(TAG, "Processing entry: " + username + " with difficulty " + difficulty);

            // Check if this is the current user's score
            if (username.equalsIgnoreCase(currentUsername)) {
                currentUserHasScore = true;
                Log.d(TAG, "Found a score for current user: " + currentUsername);
            }

            // Filter by difficulty if needed
            if (!currentDifficulty.equals("all") && !difficulty.equals(currentDifficulty)) {
                Log.d(TAG, "Skipping entry because difficulty doesn't match filter");
                continue;
            }

            int flips = 0;
            try {
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    flips = (Integer) value;
                } else if (value instanceof String) {
                    flips = Integer.parseInt((String) value);
                } else if (value instanceof Long) {
                    flips = ((Long) value).intValue();
                } else {
                    Log.e(TAG, "Unexpected value type: " + value.getClass().getName());
                    continue;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing score value: " + e.getMessage());
                continue;
            }

            if (flips > 0) { // Only add valid scores
                entries.add(new LeaderboardEntry(username, difficulty, flips));
                Log.d(TAG, "Added entry to list: " + username + ", " + difficulty + ", " + flips);
            }
        }

        // Add a placeholder entry for the current user if they have no scores yet
        if (!currentUserHasScore && !currentUsername.isEmpty()) {
            if (currentDifficulty.equals("all")) {
                Log.d(TAG, "Adding placeholder entry for current user with no scores");
                entries.add(new LeaderboardEntry(currentUsername, "N/A", 0));
            } else {
                Log.d(TAG, "Adding difficulty-specific placeholder for current user");
                entries.add(new LeaderboardEntry(currentUsername, currentDifficulty, 0));
            }
        }

        // Sort entries by flips (ascending - fewer flips is better)
        Collections.sort(entries, new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry o1, LeaderboardEntry o2) {
                // Put placeholder entries (score 0) at the bottom
                if (o1.flips == 0) return 1;
                if (o2.flips == 0) return -1;
                return Integer.compare(o1.flips, o2.flips);
            }
        });

        Log.d(TAG, "Total valid entries after filtering: " + entries.size());

        // Display entries in the layout
        int count = 0;
        for (LeaderboardEntry entry : entries) {
            // Always include the current user even if we have 10+ entries
            if (count >= 10 && !entry.username.equalsIgnoreCase(currentUsername)) {
                continue;
            }

            // Create a row layout for this entry
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(16, 16, 16, 16);

            // Highlight current user's row - Case-insensitive comparison
            if (entry.username.equalsIgnoreCase(currentUsername)) {
                rowLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                Log.d(TAG, "Highlighting row for current user: " + entry.username);
            }

            // Rank column
            TextView rankView = new TextView(this);
            rankView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            rankView.setText(entry.flips == 0 ? "-" : "#" + (count + 1));
            rankView.setTextSize(16);

            // Name column
            TextView nameView = new TextView(this);
            nameView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            nameView.setText(entry.username);
            nameView.setTextSize(16);

            // Difficulty column (only show when viewing all difficulties)
            TextView difficultyView = null;
            if (currentDifficulty.equals("all")) {
                difficultyView = new TextView(this);
                difficultyView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
                difficultyView.setText(entry.difficulty);
                difficultyView.setTextSize(16);
            }

            // Flips/Score column
            TextView scoreView = new TextView(this);
            scoreView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            scoreView.setText(entry.flips == 0 ? "No score yet" : String.valueOf(entry.flips) + " flips");
            scoreView.setTextSize(16);
            scoreView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            rowLayout.addView(rankView);
            rowLayout.addView(nameView);
            if (difficultyView != null) {
                rowLayout.addView(difficultyView);
            }
            rowLayout.addView(scoreView);

            leaderboardContainer.addView(rowLayout);

            // Add a divider
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            leaderboardContainer.addView(divider);

            if (entry.flips > 0) { // Only count valid scores toward the limit
                count++;
            }
        }

        // Show a message if no entries found
        if (entries.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No scores recorded yet. Play a game first!");
            noDataText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noDataText.setTextSize(18);
            noDataText.setPadding(16, 32, 16, 32);
            leaderboardContainer.addView(noDataText);

            Log.d(TAG, "No entries displayed - showing empty state message");
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
        if (currentDifficulty != null && !currentDifficulty.equals("all")) {
            intent.putExtra("difficulty", currentDifficulty);
        }
        intent.putExtra("destination", "leaderboard");
        startActivity(intent);
        finish();
    }
}