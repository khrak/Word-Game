package com.example.khrak.wordgame.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.khrak.wordgame.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_activity);

        getSupportActionBar().hide();
    }
}
