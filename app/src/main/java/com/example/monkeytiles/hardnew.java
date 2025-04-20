package com.example.monkeytiles;

import android.os.Handler;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Collections;

public class hardnew extends AppCompatActivity {

    // Make sure this matches exactly with the number of image buttons in your layout
    private ImageButton[] cards = new ImageButton[16];
    private Integer[] cardImages = {
            R.drawable.mandrill, R.drawable.proboscismonkey, R.drawable.spider, R.drawable.oranngutan,
            R.drawable.squirellmonkey, R.drawable.emperortamarin, R.drawable.goldennose,R.drawable.olviebaboon,
            R.drawable.mandrill, R.drawable.proboscismonkey, R.drawable.spider, R.drawable.oranngutan,
            R.drawable.squirellmonkey, R.drawable.emperortamarin, R.drawable.goldennose,R.drawable.olviebaboon,
    };

    private int firstCardIndex = -1;
    private boolean isBusy = false;
    private int flipCount = 0;
    private TextView flipCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardnew);

        flipCounter = findViewById(R.id.flipCounterHard);
        updateFlipCounter();

        // Initialize all cards first
        initializeCards();

        // Shuffle images
        Collections.shuffle(Arrays.asList(cardImages));

        // Set up pause button
        Button pausebutton = findViewById(R.id.pausbtn_newhard);
        pausebutton.setOnClickListener(v -> {
            Intent intent = new Intent(hardnew.this, pause2.class);
            // Don't use finish() here as it might be causing the problem
            startActivity(intent);
        });

        // Handle restart if needed
        if (getIntent().getBooleanExtra("RESTART_GAME", false)) {
            resetGame();
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

    private void resetGame() {
        // Reset the flip count
        flipCount = 0;
        updateFlipCounter();

        // Reset all card images
        for (ImageButton card : cards) {
            if (card != null) {
                card.setImageResource(R.drawable.card);
                card.setTag(null);
            }
        }

        // Reshuffle the cards
        Collections.shuffle(Arrays.asList(cardImages));

        // Reset game state
        firstCardIndex = -1;
        isBusy = false;
    }

    private void onCardClick(int index) {
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
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    // You could create a GameCompleted activity or use a dialog
                    Intent intent = new Intent(hardnew.this, MainActivity.class);
                    intent.putExtra("GAME_COMPLETED", true);
                    intent.putExtra("FLIP_COUNT", flipCount);
                    intent.putExtra("DIFFICULTY", "Medium-Easy");
                    startActivity(intent);
                }
            }, 500);
        }
    }

    // Override the onPause and onResume methods to handle activity lifecycle
    @Override
    protected void onPause() {
        super.onPause();
        // You can save game state here if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        // You can restore game state here if needed
    }

    @Override
    public void onBackPressed() {
        // Override back button behavior to go to pause screen instead of MainActivity
        Intent intent = new Intent(hardnew.this, pause.class);
        startActivity(intent);
        // Don't call super.onBackPressed() as it would finish this activity
    }
}