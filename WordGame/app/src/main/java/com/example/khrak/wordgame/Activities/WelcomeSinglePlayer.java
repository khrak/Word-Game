package com.example.khrak.wordgame.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.khrak.wordgame.R;

public class WelcomeSinglePlayer extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_singleplayer);

        getSupportActionBar().hide();
    }
}
