package com.example.khrak.wordgame.Utils;

/**
 * Created by khrak on 8/5/17.
 */

public class LetterTranslator {

    public static char translate(char georgianLetter) {
        if (georgianLetter == 'ა') return 'a';
        if (georgianLetter == 'ბ') return 'b';
        if (georgianLetter == 'გ') return 'g';
        if (georgianLetter == 'დ') return 'd';
        if (georgianLetter == 'ე') return 'e';
        if (georgianLetter == 'ვ') return 'v';
        if (georgianLetter == 'ზ') return 'z';
        if (georgianLetter == 'თ') return 'T';
        if (georgianLetter == 'ი') return 'i';
        if (georgianLetter == 'კ') return 'k';
        if (georgianLetter == 'ლ') return 'l';
        if (georgianLetter == 'მ') return 'm';
        if (georgianLetter == 'ნ') return 'n';
        if (georgianLetter == 'ო') return 'o';
        if (georgianLetter == 'პ') return 'p';
        if (georgianLetter == 'ჟ') return 'J';
        if (georgianLetter == 'რ') return 'r';
        if (georgianLetter == 'ს') return 's';
        if (georgianLetter == 'ტ') return 't';
        if (georgianLetter == 'უ') return 'u';
        if (georgianLetter == 'ფ') return 'f';
        if (georgianLetter == 'ქ') return 'q';
        if (georgianLetter == 'ღ') return 'R';
        if (georgianLetter == 'ყ') return 'y';
        if (georgianLetter == 'შ') return 'S';
        if (georgianLetter == 'ჩ') return 'C';
        if (georgianLetter == 'ც') return 'c';
        if (georgianLetter == 'ძ') return 'Z';
        if (georgianLetter == 'წ') return 'w';
        if (georgianLetter == 'ჭ') return 'W';
        if (georgianLetter == 'ხ') return 'x';
        if (georgianLetter == 'ჯ') return 'j';
        if (georgianLetter == 'ჰ') return 'h';

        return '*';
    }
}
