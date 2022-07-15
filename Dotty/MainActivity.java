/*
Chris Shaw
Mobile Programming
3/18/20
Dotty App
 */
package edu.lwtech.dotty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DotsGame mGame;
    private DotsGrid mDotsGrid;
    private TextView mMovesRemaining;
    private TextView mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovesRemaining = findViewById(R.id.movesRemaining);
        mScore = findViewById(R.id.score);
        mDotsGrid = findViewById(R.id.gameGrid);
        mDotsGrid.setGridListener(mGridListener);

        mGame = DotsGame.getInstance();
        newGame();
    }

    private DotsGrid.DotsGridListener mGridListener = new DotsGrid.DotsGridListener() {

        @Override
        public void onDotSelected(Dot dot, DotsGrid.DotSelectionStatus status) {

            // Ignore selections when game is over
            if (mGame.isGameOver()) return;

            // Add to list of selected dots
            mGame.addSelectedDot(dot);

            // If done selecting dots then replace selected dots and display new moves and score
            if (status == DotsGrid.DotSelectionStatus.Last) {
                if (mGame.getSelectedDots().size() > 1) {
                    mGame.finishMove();
                    updateMovesAndScore();
                } else {
                    mGame.clearSelectedDots();
                }
            }

            // Display changes to the game
            mDotsGrid.invalidate();

        }
        @Override
        public void onAnimationFinished() {
            mGame.finishMove();
            mDotsGrid.invalidate();
            updateMovesAndScore();
        }
    };

    public void newGameClick(View view) {

        // Animate down off screen
        ObjectAnimator moveBoardOff = ObjectAnimator.ofFloat(mDotsGrid,
                "translationY", mDotsGrid.getHeight() * 1.5f);
        moveBoardOff.setDuration(700);
        moveBoardOff.start();

        moveBoardOff.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                newGame();

                // Animate from above the screen down to default location
                ObjectAnimator moveBoardOn = ObjectAnimator.ofFloat(mDotsGrid,
                        "translationY", mDotsGrid.getHeight() * -1.5f, 0);
                moveBoardOn.setDuration(700);
                moveBoardOn.start();
            }
        });
    }

    private void newGame() {
        mGame.newGame();
        mDotsGrid.invalidate();
        updateMovesAndScore();
    }

    private void updateMovesAndScore() {
        mMovesRemaining.setText(Integer.toString(mGame.getMovesLeft()));
        mScore.setText(Integer.toString(mGame.getScore()));
    }

    public void onDotSelected(Dot dot, DotsGrid.DotSelectionStatus status) {

        // Done selecting dots
        if (status == DotsGrid.DotSelectionStatus.Last) {
            if (mGame.getSelectedDots().size() > 1) {
                mDotsGrid.animateDots();

                // These methods must be called AFTER the animation completes
                //mGame.finishMove();
                //updateMovesAndScore();
            } else {
                mGame.clearSelectedDots();
            }
        }

        mDotsGrid.invalidate();
    }
}
