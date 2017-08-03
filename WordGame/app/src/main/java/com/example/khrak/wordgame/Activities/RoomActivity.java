package com.example.khrak.wordgame.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.khrak.wordgame.R;

public class RoomActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.room_activity);

        getSupportActionBar().hide();

        String roomid = getIntent().getStringExtra("roomid");
        String username = getIntent().getStringExtra("username");

        System.out.println(roomid + " " + username);
    }
}
