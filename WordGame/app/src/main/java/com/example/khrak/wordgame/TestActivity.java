package com.example.khrak.wordgame;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.khrak.wordgame.Activities.RoomActivity;
import com.example.khrak.wordgame.Adapters.ScoreboardAdapter;
import com.example.khrak.wordgame.Model.ScoreboardItem;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_dialog);

        final Dialog dialog = new Dialog(TestActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.round_dialog);

        dialog.show();

        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 3000);

//        final Dialog dialog = new Dialog(TestActivity.this);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        dialog.setContentView(R.layout.betting_dialog);
//
//        Button button = (Button) dialog.findViewById(R.id.submit_bet_button);
//
//        dialog.show();
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText editText = (EditText) dialog.findViewById(R.id.betting_edittext);
//
//                try {
//                    int bet = Integer.parseInt(editText.getText().toString());
//
//                    dialog.dismiss();
//
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//            }
//        });



//        ArrayList<ScoreboardItem> list = new ArrayList<>();
//
//        list.add(new ScoreboardItem("მამალი", 17, 0));
//        list.add(new ScoreboardItem("მამლის", 19, 1));
//        list.add(new ScoreboardItem("მაღალი", 13, 2));
//        list.add(new ScoreboardItem("მადა", 9, 4));
//
//        ScoreboardAdapter adapter = new ScoreboardAdapter(this, list);
//
//        final Dialog dialog = new Dialog(TestActivity.this);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        dialog.setContentView(R.layout.scoreboard_dialog);
//
//        ListView listView = (ListView) dialog.findViewById(R.id.scoreboard_listview);
//
//        listView.setAdapter(adapter);
//
//        dialog.show();
    }
}