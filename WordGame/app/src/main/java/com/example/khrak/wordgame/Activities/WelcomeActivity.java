package com.example.khrak.wordgame.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.khrak.wordgame.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);

        getSupportActionBar().hide();
    }

    public void aboutClicked(View view) {

        Intent intent = new Intent(this, AboutActivity.class);

        startActivity(intent);
    }

    public void singlePlayerClicked(View view) {
        Intent intent = new Intent(this, WelcomeSinglePlayer.class);

        startActivity(intent);
    }

    public void multiplayerClicked(View view) {
        Intent intent = new Intent(this, WelcomeMultiplayer.class);

        startActivity(intent);
    }
}
