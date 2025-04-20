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

public class easynew extends AppCompatActivity {

    // Make sure this matches exactly with the number of image buttons in your layout
    private ImageButton[] cards = new ImageButton[8];
    private Integer[] cardImages = {
            R.drawable.mandrill, R.drawable.proboscismonkey, R.drawable.spider,
            R.drawable.squirellmonkey, R.drawable.mandrill, R.drawable.proboscismonkey, R.drawable.spider,
            R.drawable.squirellmonkey
    };

    private int firstCardIndex = -1;
    private boolean isBusy = false;
    private int flipCount = 0;
    private TextView flipCounter;
    private int matchedPairs = 0;
    private final int totalPairs = 4; // Total number of pairs in this game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easynew);

        flipCounter = findViewById(R.id.flipCounter);
        updateFlipCounter();

        // Initialize all cards first
        initializeCards();

        // Shuffle images
        Collections.shuffle(Arrays.asList(cardImages));

        // Set up pause button
        Button pausebutton = findViewById(R.id.pausebtn_easynew);
        pausebutton.setOnClickListener(v -> {
            Intent intent = new Intent(easynew.this, pause.class);
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

            // Skip if card not found in layout
            if (resID == 0) {
                continue;
            }

            cards[i] = findViewById(resID);
            if (cards[i] != null) {
                cards[i].setImageResource(R.drawable.card);
                cards[i].setTag(null); // Ensure tag is null initially
                final int index = i;
                cards[i].setOnClickListener(v -> onCardClick(index));
            }
        }
    }

    private void resetGame() {
        // Reset the flip count and matched pairs
        flipCount = 0;
        matchedPairs = 0;
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
        // Don't process clicks if:
        // 1. The game is busy processing a previous click
        // 2. The clicked card is already matched
        // 3. The clicked card is already flipped (it's the first card of the current pair)
        if (isBusy || index < 0 || index >= cards.length ||
                cards[index] == null || "matched".equals(cards[index].getTag()) ||
                index == firstCardIndex) {
            return;
        }

        // Flip the card and show its image
        cards[index].setImageResource(cardImages[index]);
        flipCount++;
        updateFlipCounter();

        if (firstCardIndex < 0) {
            // This is the first card flipped
            firstCardIndex = index;
        } else {
            // This is the second card - check for a match
            isBusy = true;

            // Check if the images match (not the indices)
            if (cardImages[firstCardIndex].equals(cardImages[index])) {
                // Match found!
                cards[firstCardIndex].setTag("matched");
                cards[index].setTag("matched");
                matchedPairs++;

                // Reset for the next pair selection
                resetTurn();

                // Check if all pairs are matched
                if (matchedPairs >= totalPairs) {
                    handleGameComplete();
                }
            } else {
                // No match - flip the cards back after a delay
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

    private void handleGameComplete() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                Intent intent = new Intent(easynew.this, MainActivity.class);
                intent.putExtra("GAME_COMPLETED", true);
                intent.putExtra("FLIP_COUNT", flipCount);
                intent.putExtra("DIFFICULTY", "Easy"); // Fixed to match the class name
                startActivity(intent);
                finish(); // End this activity when game is completed
            }
        }, 500);
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
        Intent intent = new Intent(easynew.this, pause.class);
        startActivity(intent);
        // Don't call super.onBackPressed() as it would finish this activity
    }
}