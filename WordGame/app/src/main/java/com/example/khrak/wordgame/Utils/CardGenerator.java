package com.example.khrak.wordgame.Utils;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.khrak.wordgame.Game.Card;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by melia on 8/5/2017.
 */

public class CardGenerator {

    private static Random random = new Random();

    public static Card[] getRandomCards(int cardsNum){

        Card[] cards = new Card[cardsNum];

        for (int i = 0; i < cardsNum; i++) {
            int score = randomInRange(0, 9);
            int symbolIndex = randomInRange(0, 33);

            char ch = 'ა';

            if (symbolIndex == 33) {
                ch = '*';
            } else ch += symbolIndex;

            cards[i] = new Card("" + ch, score);
        }

        return cards;
    }

    private static int randomInRange(int min, int max) {

        int diff = max - min + 1;

        int rand = random.nextInt();

        if (rand < 0) rand = -rand;

        rand = rand % diff;

        return rand + min;
    }
}
