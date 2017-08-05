package com.example.khrak.wordgame.Model;

/**
 * Created by khrak on 8/6/17.
 */

public class ScoreboardItem {
    public String guessedWord;
    public int score;
    public int userimage;
    public boolean winner;

    public ScoreboardItem(String guessedWord, int score, int userimage, boolean winner) {
        this.guessedWord = guessedWord;
        this.score = score;
        this.userimage = userimage;
        this.winner = winner;
    }
}
