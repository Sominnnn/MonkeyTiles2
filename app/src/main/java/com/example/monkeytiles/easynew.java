package com.example.monkeytiles;

import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Collections;

public class easynew extends AppCompatActivity {

    private ImageButton[] cards = new ImageButton[8];
    private Integer[] cardImages = {
            R.drawable.mandrill, R.drawable.proboscismonkey,
            R.drawable.spider, R.drawable.squirellmonkey,
            R.drawable.mandrill, R.drawable.proboscismonkey,
            R.drawable.spider, R.drawable.squirellmonkey
    };

    private int firstCardIndex = -1;
    private boolean isBusy = false;
    private int flipCount = 0;
    private TextView flipCounter;
    private static boolean needsReshuffling = true;

    private TextView timerTextView;
    private long startTimeMillis = 0;
    private long pausedTimeMillis = 0;
    private Handler timerHandler = new Handler();
    private boolean timerRunning = false;
    private boolean gamePaused = false;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTimeMillis = SystemClock.elapsedRealtime();
            long elapsedMillis = currentTimeMillis - startTimeMillis;
            int seconds = (int) (elapsedMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            updateTimerDisplay(minutes, seconds);
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easynew);

        flipCounter = findViewById(R.id.flipCounter);
        timerTextView = findViewById(R.id.timerEasy);
        updateFlipCounter();

        initializeCards();

        Button pausebutton = findViewById(R.id.pausebtn_easynew);
        pausebutton.setOnClickListener(v -> {
            pauseGame();
            Intent intent = new Intent(easynew.this, pause.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (getIntent().getBooleanExtra("RESTART_GAME", false)) {
            resetGame();
        } else {
            resetGame();
        }
    }

    private void startTimer() {
        if (!timerRunning) {
            if (pausedTimeMillis > 0) {
                startTimeMillis = SystemClock.elapsedRealtime() - pausedTimeMillis;
            } else {
                startTimeMillis = SystemClock.elapsedRealtime();
            }
            timerHandler.postDelayed(timerRunnable, 0);
            timerRunning = true;
        }
    }

    private void pauseTimer() {
        if (timerRunning) {
            timerHandler.removeCallbacks(timerRunnable);
            pausedTimeMillis = SystemClock.elapsedRealtime() - startTimeMillis;
            timerRunning = false;
        }
    }

    private void resetTimer() {
        pauseTimer();
        pausedTimeMillis = 0;
        updateTimerDisplay(0, 0);
    }

    private void pauseGame() {
        gamePaused = true;
        pauseTimer();
    }

    private void resumeGame() {
        if (gamePaused) {
            gamePaused = false;
            startTimer();
        }
    }

    private void updateTimerDisplay(int minutes, int seconds) {
        if (timerTextView != null) {
            String timeStr = String.format("%02d:%02d", minutes, seconds);
            timerTextView.setText(timeStr);
        }
    }

    private void initializeCards() {
        for (int i = 0; i < cards.length; i++) {
            String cardID = "card" + i;
            int resID = getResources().getIdentifier(cardID, "id", getPackageName());
            if (resID == 0) continue;
            cards[i] = findViewById(resID);
            if (cards[i] != null) {
                cards[i].setImageResource(R.drawable.card);
                final int index = i;
                cards[i].setOnClickListener(v -> onCardClick(index));
            }
        }
    }

    private void shuffleCards() {
        Collections.shuffle(Arrays.asList(cardImages));
        needsReshuffling = false;
    }

    private void resetGame() {
        flipCount = 0;
        updateFlipCounter();
        resetTimer();
        gamePaused = false;
        startTimer();
        for (ImageButton card : cards) {
            if (card != null) {
                card.setImageResource(R.drawable.card);
                card.setTag(null);
                card.setClickable(true);
            }
        }
        shuffleCards();
        firstCardIndex = -1;
        isBusy = false;
    }

    private void onCardClick(int index) {
        if (gamePaused) return;
        if (index < 0 || index >= cards.length || cards[index] == null) return;
        if (isBusy || cards[index].getTag() != null) return;

        cards[index].setImageResource(cardImages[index]);
        flipCount++;
        updateFlipCounter();

        if (firstCardIndex < 0) {
            firstCardIndex = index;
        } else {
            isBusy = true;
            if (firstCardIndex >= 0 && firstCardIndex < cardImages.length &&
                    index >= 0 && index < cardImages.length) {
                if (cardImages[firstCardIndex].equals(cardImages[index])) {
                    cards[firstCardIndex].setTag("matched");
                    cards[index].setTag("matched");
                    resetTurn();
                    checkGameOver();
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            cards[firstCardIndex].setImageResource(R.drawable.card);
                            cards[index].setImageResource(R.drawable.card);
                            resetTurn();
                        }
                    }, 1000);
                }
            } else {
                resetTurn();
            }
        }
    }

    private void updateFlipCounter() {
        if (flipCounter != null) {
            flipCounter.setText(String.valueOf(flipCount));
        }
    }

    private void resetTurn() {
        firstCardIndex = -1;
        isBusy = false;
    }

    private void saveScore(int flips, String timeString) {
        String username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            username = "Player";
        }

        String difficulty = "easy";
        String[] timeParts = timeString.split(":");
        int minutes = Integer.parseInt(timeParts[0]);
        int seconds = Integer.parseInt(timeParts[1]);
        int totalSeconds = (minutes * 60) + seconds;

        SharedPreferences scoresPrefs = getSharedPreferences("MonkeyMindMatchScores", MODE_PRIVATE);
        SharedPreferences.Editor editor = scoresPrefs.edit();

        String flipsKey = username + "_" + difficulty + "_flips";
        String timeKey = username + "_" + difficulty + "_time";

        int currentBestFlips = scoresPrefs.getInt(flipsKey, Integer.MAX_VALUE);
        int currentBestTime = scoresPrefs.getInt(timeKey, Integer.MAX_VALUE);

        boolean newRecord = false;

        if (flips < currentBestFlips || (flips == currentBestFlips && totalSeconds < currentBestTime)) {
            editor.putInt(flipsKey, flips);
            editor.putInt(timeKey, totalSeconds);
            editor.putString(username + "_" + difficulty + "_timestring", timeString);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            editor.putString(username + "_" + difficulty + "_date", currentDate);
            newRecord = true;
        }

        editor.putBoolean(username + "_played", true);

        if (newRecord) {
            editor.apply();
        }
    }

    private void checkGameOver() {
        boolean allMatched = true;
        for (ImageButton card : cards) {
            if (card != null && card.getTag() == null) {
                allMatched = false;
                break;
            }
        }

        if (allMatched) {
            needsReshuffling = true;
            pauseTimer();

            for (ImageButton card : cards) {
                if (card != null) {
                    card.setClickable(false);
                }
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    String finalTime = timerTextView.getText().toString();
                    saveScore(flipCount, finalTime);
                    Intent intent = new Intent(easynew.this, win.class);
                    intent.putExtra("GAME_COMPLETED", true);
                    intent.putExtra("FLIP_COUNT", flipCount);
                    intent.putExtra("FINAL_TIME", finalTime);
                    intent.putExtra("DIFFICULTY", "Easy");
                    startActivity(intent);
                    finish();
                }
            }, 500);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("RESTART_GAME", false)) {
            resetGame();
        } else if (needsReshuffling) {
            resetGame();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (needsReshuffling) {
            resetGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gamePaused) {
            resumeGame();
        } else if (needsReshuffling) {
            resetGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTimer();
    }

    @Override
    public void onBackPressed() {
        pauseGame();
        Intent intent = new Intent(easynew.this, pause.class);
        startActivity(intent);
    }
}
