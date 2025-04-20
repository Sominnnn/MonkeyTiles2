package com.example.monkeytiles;

import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Collections;

public class mediumeasy extends AppCompatActivity {

    // Make sure this matches exactly with the number of image buttons in your layout
    private ImageButton[] cards = new ImageButton[12];
    private Integer[] cardImages = {
            R.drawable.mandrill, R.drawable.proboscismonkey, R.drawable.spider,
            R.drawable.squirellmonkey, R.drawable.emperortamarin, R.drawable.goldennose,
            R.drawable.mandrill, R.drawable.proboscismonkey, R.drawable.spider,
            R.drawable.squirellmonkey, R.drawable.emperortamarin, R.drawable.goldennose
    };

    private int firstCardIndex = -1;
    private boolean isBusy = false;
    private int flipCount = 0;
    private TextView flipCounter;
    private static boolean needsReshuffling = true; // Track if cards need reshuffling

    // Timer variables
    private TextView timerTextView;
    private long startTimeMillis = 0;
    private long pausedTimeMillis = 0;  // Store the elapsed time when paused
    private Handler timerHandler = new Handler();
    private boolean timerRunning = false;
    private boolean gamePaused = false;  // Track if the game is paused

    // Runnable for updating the timer
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTimeMillis = SystemClock.elapsedRealtime();
            long elapsedMillis = currentTimeMillis - startTimeMillis;

            // Convert to seconds
            int seconds = (int) (elapsedMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            // Update the timer TextView
            updateTimerDisplay(minutes, seconds);

            // Post again to keep the timer running
            timerHandler.postDelayed(this, 1000); // Update every second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediumeasy);

        flipCounter = findViewById(R.id.flipCounterMedium);
        // Initialize timer TextView - make sure to add this to your layout!
        timerTextView = findViewById(R.id.timerMedium);
        updateFlipCounter();

        // Initialize all cards first
        initializeCards();

        // Set up pause button
        Button pausebutton = findViewById(R.id.pausebtn_mediumeasy);
        pausebutton.setOnClickListener(v -> {
            pauseGame(); // Pause the timer and set game state
            Intent intent = new Intent(mediumeasy.this, pause1.class);
            startActivity(intent);
            // Add a transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Check if we're coming from the restart button
        if (getIntent().getBooleanExtra("RESTART_GAME", false)) {
            // Force a reset if we're coming from the restart button
            resetGame();
        } else {
            // Always do a full reset/shuffle when the activity is created normally
            resetGame();
        }
    }

    private void startTimer() {
        if (!timerRunning) {
            if (pausedTimeMillis > 0) {
                // Resume from where we left off
                startTimeMillis = SystemClock.elapsedRealtime() - pausedTimeMillis;
            } else {
                // Start fresh
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

            // Debug info to check if the cards are found
            if (resID == 0) {
                // If card not found, log or handle appropriately
                continue;
            }

            cards[i] = findViewById(resID);
            if (cards[i] != null) {
                cards[i].setImageResource(R.drawable.card);
                final int index = i;
                cards[i].setOnClickListener(v -> onCardClick(index));
            }
        }
    }

    // Ensure shuffle happens every time
    private void shuffleCards() {
        Collections.shuffle(Arrays.asList(cardImages));
        needsReshuffling = false; // Reset the flag after shuffling
    }

    private void resetGame() {
        // Reset the flip count
        flipCount = 0;
        updateFlipCounter();

        // Reset the timer
        resetTimer();
        gamePaused = false;
        startTimer();

        // Reset all card images and tags
        for (ImageButton card : cards) {
            if (card != null) {
                card.setImageResource(R.drawable.card);
                card.setTag(null);
                card.setClickable(true); // Ensure cards are clickable again
            }
        }

        // Always reshuffle the cards on reset
        shuffleCards();

        // Reset game state
        firstCardIndex = -1;
        isBusy = false;
    }

    private void onCardClick(int index) {
        // Don't process clicks if game is paused
        if (gamePaused) return;

        // Validate index to prevent crashes
        if (index < 0 || index >= cards.length || cards[index] == null) {
            return;
        }

        if (isBusy || cards[index].getTag() != null) return;

        cards[index].setImageResource(cardImages[index]);
        flipCount++;
        updateFlipCounter();

        if (firstCardIndex < 0) {
            firstCardIndex = index;
        } else {
            isBusy = true;
            // Check if the indices are valid
            if (firstCardIndex >= 0 && firstCardIndex < cardImages.length &&
                    index >= 0 && index < cardImages.length) {

                if (cardImages[firstCardIndex].equals(cardImages[index])) {
                    // Match
                    cards[firstCardIndex].setTag("matched");
                    cards[index].setTag("matched");
                    resetTurn();
                    checkGameOver();
                } else {
                    // No match
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        // Check if the activity is still active
                        if (!isFinishing() && !isDestroyed()) {
                            cards[firstCardIndex].setImageResource(R.drawable.card);
                            cards[index].setImageResource(R.drawable.card);
                            resetTurn();
                        }
                    }, 1000);
                }
            } else {
                // Invalid indices, reset turn
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

    private void checkGameOver() {
        boolean allMatched = true;
        for (ImageButton card : cards) {
            if (card != null && card.getTag() == null) {
                allMatched = false;
                break;
            }
        }

        if (allMatched) {
            // Set flag to indicate reshuffling is needed on next start
            needsReshuffling = true;

            // Stop the timer when game is complete
            pauseTimer();

            // Disable further clicks while showing completion
            for (ImageButton card : cards) {
                if (card != null) {
                    card.setClickable(false);
                }
            }

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    // Get the final time values
                    String finalTime = timerTextView.getText().toString();

                    // You could create a GameCompleted activity or use a dialog
                    Intent intent = new Intent(mediumeasy.this, youwin2.class);
                    intent.putExtra("GAME_COMPLETED", true);
                    intent.putExtra("FLIP_COUNT", flipCount);
                    intent.putExtra("FINAL_TIME", finalTime);
                    intent.putExtra("DIFFICULTY", "Medium");
                    startActivity(intent);
                    finish(); // End this activity to ensure a fresh start
                }
            }, 500);
        }
    }

    // Override the onNewIntent method to handle when the activity is reused
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // If returning to this activity, ensure we reset the game if requested
        if (intent.getBooleanExtra("RESTART_GAME", false)) {
            resetGame();
        } else if (needsReshuffling) {
            resetGame();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // When restarting from background, make sure to reset if needed
        if (needsReshuffling) {
            resetGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the game if we're coming back from pause screen
        if (gamePaused) {
            resumeGame();
        }
        // If we need reshuffling, do a full reset
        else if (needsReshuffling) {
            resetGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the timer when the activity is paused
        pauseTimer();
    }

    @Override
    public void onBackPressed() {
        // Override back button behavior to go to pause screen
        pauseGame(); // Pause the timer and game state
        Intent intent = new Intent(mediumeasy.this, pause1.class);
        startActivity(intent);
        // Don't call super.onBackPressed() as it would finish this activity
    }
}