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

public class mediumeasy extends AppCompatActivity {

    // Modified to have 12 cards to match the 12 cardImages
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediumeasy);

        flipCounter = findViewById(R.id.flipCounterMedium);

        // Shuffle images
        Collections.shuffle(Arrays.asList(cardImages));

        // Initialize cards
        for (int i = 0; i < cards.length; i++) {
            int resID = getResources().getIdentifier("card" + i, "id", getPackageName());
            cards[i] = findViewById(resID);
            final int index = i;
            cards[i].setImageResource(R.drawable.card);
            cards[i].setOnClickListener(v -> onCardClick(index));
        }

        // Handle game restart
        if (getIntent().getBooleanExtra("RESTART_GAME", false)) {
            // Reset the flip count
            flipCount = 0;
            flipCounter.setText("0");

            // Reset all card images to card_back
            for (int i = 0; i < cards.length; i++) {
                cards[i].setImageResource(R.drawable.card);
                cards[i].setTag(null);  // Clear any tag associated with the cards
            }

            // Shuffle the cards again
            Collections.shuffle(Arrays.asList(cardImages));
        }

        // pause button
        Button pausebutton = findViewById(R.id.pausebtn_mediumeasy);
        pausebutton.setOnClickListener(v -> {
            Intent intent = new Intent(mediumeasy.this, pause.class);
            // Add flag to prevent return to MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void onCardClick(int index) {
        if (isBusy || cards[index].getTag() != null) return;

        cards[index].setImageResource(cardImages[index]);
        flipCount++;
        flipCounter.setText("" + flipCount);

        if (firstCardIndex < 0) {
            firstCardIndex = index;
        } else {
            isBusy = true;
            if (cardImages[firstCardIndex].equals(cardImages[index])) {
                // Match
                cards[firstCardIndex].setTag("matched");
                cards[index].setTag("matched");
                resetTurn();

                // Check if game is over
                checkGameOver();
            } else {
                // No match
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    cards[firstCardIndex].setImageResource(R.drawable.card);
                    cards[index].setImageResource(R.drawable.card);
                    resetTurn();
                }, 1000);
            }
        }
    }

    private void resetTurn() {
        firstCardIndex = -1;
        isBusy = false;
    }

    // Add method to check if all cards are matched
    private void checkGameOver() {
        boolean allMatched = true;
        for (ImageButton card : cards) {
            if (card.getTag() == null) {
                allMatched = false;
                break;
            }
        }

        if (allMatched) {
            // Game over, show success or navigate to another screen
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent intent = new Intent(mediumeasy.this, youwin2.class);
                intent.putExtra("FLIP_COUNT", flipCount);
                intent.putExtra("DIFFICULTY", "Medium-Easy");
                startActivity(intent);
                finish();
            }, 500);
        }
    }

    // Override back button to prevent returning to MainActivity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mediumeasy.this, pause.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}