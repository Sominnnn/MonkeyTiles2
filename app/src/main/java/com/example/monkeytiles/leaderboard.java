package com.example.monkeytiles;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class leaderboard extends AppCompatActivity {

    private LinearLayout leaderboardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardContainer = findViewById(R.id.leaderboard_container);

        showLeaderboard("easy");
    }

    private void showLeaderboard(String difficulty) {
        SharedPreferences prefs = getSharedPreferences("MonkeyMindMatchScores", MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        if (allEntries != null && !allEntries.isEmpty()) {
            for (String key : allEntries.keySet()) {
                if (key.startsWith("user_") && !key.contains("_flips") && !key.contains("_timestring") && !key.contains("_date")) {
                    String username = key.substring(5); // remove "user_"

                    int flips = prefs.getInt("user_" + username + "_" + difficulty + "_flips", -1);
                    String time = prefs.getString("user_" + username + "_" + difficulty + "_timestring", "--:--");
                    String date = prefs.getString("user_" + username + "_" + difficulty + "_date", "Unknown");

                    if (flips != -1) {
                        TextView scoreView = new TextView(this);
                        scoreView.setText("Username: " + username + "\nFlips: " + flips + "\nTime: " + time + "\nDate: " + date);
                        scoreView.setPadding(16, 16, 16, 16);
                        leaderboardContainer.addView(scoreView);
                    }
                }
            }
        }
    }
}

