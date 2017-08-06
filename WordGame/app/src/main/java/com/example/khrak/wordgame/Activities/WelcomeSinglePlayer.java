package com.example.khrak.wordgame.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.khrak.wordgame.R;

public class WelcomeSinglePlayer extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_singleplayer);

        getSupportActionBar().hide();
    }

    public void easyClicked(View view) {
        startLevel(0);
    }

    public void mediumClicked(View view) {
        startLevel(0);
    }

    public void hardClicked(View view) {
        startLevel(0);
    }

    private void startLevel(int i) {
        Intent intent = new Intent(this, GameActivity.class);

        intent.putExtra("level", i);

        startActivity(intent);
    }

}
