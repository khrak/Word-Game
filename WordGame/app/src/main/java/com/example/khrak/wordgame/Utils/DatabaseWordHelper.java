package com.example.khrak.wordgame.Utils;

import android.content.Context;

import com.example.khrak.wordgame.AppMain;
import com.example.khrak.wordgame.Game.Card;
import com.example.khrak.wordgame.Model.CardScoringResult;
import com.example.khrak.wordgame.database.DatabaseAccess;

/**
 * Created by khrak on 8/5/17.
 */

public class DatabaseWordHelper {

    public static boolean wordExists(String word) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AppMain.getContext());
        databaseAccess.open();
        boolean result = false;
        if (databaseAccess.wordExists(word)) {
            result = true;
        }
        databaseAccess.close();
        return result;
    }

    public static CardScoringResult scoreCardsOrder(Card[] cards){
        CardScoringResult result = new CardScoringResult();
        String word = ""; int score = 0;
        for (int i = 0; i < cards.length; i++){
            word += cards[i].symbol;
            score += cards[i].score;
        }

        result.foundWord = "";
        result.resultScore = 0;

        if (wordExists(word)){
            result.foundWord = word;
            result.resultScore = score;
        }

        return result;
    }

    /**
     * checks if there exists any word starting from so far
     * */
    public static boolean isPossibleWord(String soFar) {

        String likeWord = "";
        for (int i = 0; i < soFar.length(); i++){
            if (soFar.charAt(i) == '*')
                likeWord += soFar.charAt(i);
            else
                likeWord += '_';
        }

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AppMain.getContext());
        databaseAccess.open();
        boolean result = databaseAccess.isPossibleWord(likeWord);
        databaseAccess.close();
        return result;
    }
}
