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

    private LinearLayout leaderboardContainer;
    private String currentDifficulty = "all";
    private static final String TAG = "LeaderboardActivity";

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

        leaderboardContainer = findViewById(R.id.leaderboard_container);
        Button pauseButton = findViewById(R.id.pausebtn_leaderboard);

        SharedPreferences userPrefs = getSharedPreferences("MonkeyMindMatchPrefs", MODE_PRIVATE);
        String currentUsername = userPrefs.getString("username", "");
        Log.d(TAG, "Current user is: " + currentUsername);

        addTestScoresIfNeeded(currentUsername);

        if (getIntent().hasExtra("difficulty")) {
            currentDifficulty = getIntent().getStringExtra("difficulty");
            Log.d(TAG, "Showing leaderboard for difficulty: " + currentDifficulty);
        } else {
            Log.d(TAG, "No specific difficulty provided, showing all scores");
        }

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

        pauseButton.setOnClickListener(v -> {
            Intent intent = new Intent(leaderboard.this, MainActivity.class);
            startActivity(intent);
        });

        setupFilterButtons();
        createTableHeader();
        loadLeaderboard(currentUsername);
    }

    private void addTestScoresIfNeeded(String currentUsername) {
        if (currentUsername.isEmpty()) {
            Log.d(TAG, "No current username, skipping test score creation");
            return;
        }

        SharedPreferences scorePrefs = getSharedPreferences("MonkeyMindMatchScores", MODE_PRIVATE);
        SharedPreferences.Editor editor = scorePrefs.edit();

        String[] difficulties = {"easy", "normal", "hard"};
        boolean added = false;

        for (String diff : difficulties) {
            String key = currentUsername + "_" + diff;
            if (!scorePrefs.contains(key)) {
                editor.putInt(key, 0);  // Use 0 to indicate "no score yet"
                added = true;
            }
        }

        if (added) {
            editor.apply();
            Toast.makeText(this, "Test scores added for current user", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFilterButtons() {
        LinearLayout filterButtons = new LinearLayout(this);
        filterButtons.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        filterButtons.setOrientation(LinearLayout.HORIZONTAL);
        filterButtons.setPadding(16, 8, 16, 16);

        String[] difficulties = {"all", "easy", "normal", "hard"};

        for (String diff : difficulties) {
            Button button = new Button(this);
            button.setText(diff.substring(0, 1).toUpperCase() + diff.substring(1));
            button.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            button.setOnClickListener(v -> {
                if (!diff.equals(currentDifficulty)) {
                    Intent intent = new Intent(leaderboard.this, leaderboard.class);
                    intent.putExtra("difficulty", diff);
                    startActivity(intent);
                    finish();
                }
            });
            filterButtons.addView(button);
        }

        leaderboardContainer.addView(filterButtons);
    }

    private void createTableHeader() {
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setPadding(16, 24, 16, 8);

        String[] headers = {"Rank", "Player", "Difficulty", "Flips"};
        float[] weights = {0.2f, 0.6f, 0.5f, 0.5f};

        for (int i = 0; i < headers.length; i++) {
            TextView header = new TextView(this);
            header.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]));
            header.setText(headers[i]);
            header.setTextSize(16);
            header.setTextColor(getResources().getColor(android.R.color.black));
            header.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            headerRow.addView(header);
        }

        leaderboardContainer.addView(headerRow);

        View headerDivider = new View(this);
        headerDivider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        headerDivider.setBackgroundColor(getResources().getColor(android.R.color.black));
        leaderboardContainer.addView(headerDivider);
    }

    private void loadLeaderboard(String currentUsername) {
        SharedPreferences scoresPrefs = getSharedPreferences("MonkeyMindMatchScores", MODE_PRIVATE);
        Map<String, ?> allScores = scoresPrefs.getAll();

        List<LeaderboardEntry> entries = new ArrayList<>();
        boolean currentUserHasScore = false;

        for (Map.Entry<String, ?> entry : allScores.entrySet()) {
            String key = entry.getKey();
            if (!key.contains("_")) continue;

            String[] parts = key.split("_");
            if (parts.length != 2) continue;
            String username = parts[0];
            String difficulty = parts[1];

            if (!currentDifficulty.equals("all") && !difficulty.equals(currentDifficulty)) continue;

            int flips;
            try {
                flips = Integer.parseInt(entry.getValue().toString());
            } catch (Exception e) {
                continue;
            }

            entries.add(new LeaderboardEntry(username, difficulty, flips));
            if (username.equalsIgnoreCase(currentUsername)) currentUserHasScore = true;
        }

        if (!currentUserHasScore && !currentUsername.isEmpty()) {
            String fallbackDifficulty = currentDifficulty.equals("all") ? "easy" : currentDifficulty;
            entries.add(new LeaderboardEntry(currentUsername, fallbackDifficulty, 0));
        }

        entries.sort((o1, o2) -> {
            if (o1.flips == 0) return 1;
            if (o2.flips == 0) return -1;
            return Integer.compare(o1.flips, o2.flips);
        });

        int count = 0;
        for (LeaderboardEntry entry : entries) {
            if (count >= 10 && !entry.username.equalsIgnoreCase(currentUsername)) {
                continue;
            }

            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(16, 16, 16, 16);

            if (entry.username.equalsIgnoreCase(currentUsername)) {
                rowLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            }

            TextView rankView = new TextView(this);
            rankView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f));
            rankView.setText(entry.flips == 0 ? "-" : "#" + (count + 1));
            rankView.setTextSize(16);

            TextView nameView = new TextView(this);
            nameView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));
            nameView.setText(entry.username);
            nameView.setTextSize(16);

            TextView difficultyView = new TextView(this);
            difficultyView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            difficultyView.setText(entry.difficulty);
            difficultyView.setTextSize(16);

            TextView scoreView = new TextView(this);
            scoreView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
            scoreView.setText(entry.flips == 0 ? "No score yet" : entry.flips + " flips");
            scoreView.setTextSize(16);
            scoreView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            rowLayout.addView(rankView);
            rowLayout.addView(nameView);
            rowLayout.addView(difficultyView);
            rowLayout.addView(scoreView);

            leaderboardContainer.addView(rowLayout);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            leaderboardContainer.addView(divider);

            if (entry.flips > 0 || entry.username.equalsIgnoreCase(currentUsername)) {
                count++;
            }
        }

        if (entries.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No scores recorded yet. Play a game first!");
            noDataText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            noDataText.setTextSize(18);
            noDataText.setPadding(16, 32, 16, 32);
            leaderboardContainer.addView(noDataText);
        }
    }

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
        Intent intent = new Intent(leaderboard.this, choosedifficulty.class);
        if (currentDifficulty != null && !currentDifficulty.equals("all")) {
            intent.putExtra("difficulty", currentDifficulty);
        }
        intent.putExtra("destination", "leaderboard");
        startActivity(intent);
        finish();
    }
}
