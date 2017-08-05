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

        int vowelCount = (int)(cardsNum * 0.4);

        Card[] cards = new Card[cardsNum];

        for (int i = 0; i < vowelCount; i++) {
            int score = randomInRange(1, 9);
            int symbolIndex = randomInRange(0, 4);

            cards[i] = new Card("" + vowels[symbolIndex], score);
        }

        for (int i = vowelCount; i < cardsNum; i++) {
            int score = randomInRange(1, 9);
            int symbolIndex = randomInRange(0, 33);

            char ch = 'ა';

            if (symbolIndex == 33) {
                ch = '*';
                score = 0;
            } else ch += symbolIndex;

            cards[i] = new Card("" + ch, score);
        }

        return cards;
    }

    private static char[] vowels = new char[]{'ა', 'ე', 'ი', 'ო', 'უ'};

    private boolean isvowel(char ch) {
        return (ch == 'ა' || ch == 'ე'
                || ch == 'ი' || ch == 'ო' || ch == 'უ');
    }

    private static int randomInRange(int min, int max) {

        int diff = max - min + 1;

        int rand = random.nextInt();

        if (rand < 0) rand = -rand;

        rand = rand % diff;

        return rand + min;
    }
}
