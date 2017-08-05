package com.example.khrak.wordgame.Utils;

import android.content.Context;

import com.example.khrak.wordgame.AppMain;
import com.example.khrak.wordgame.database.DatabaseAccess;

/**
 * Created by khrak on 8/5/17.
 */

public class DatabaseWordHelper {

    public static boolean wordExists(String word) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AppMain.getContext());

        databaseAccess.open();

        boolean result = false;

        if (databaseAccess.wordExists("Mama")) {
            result = true;
        }

        databaseAccess.close();

        return result;
    }
}