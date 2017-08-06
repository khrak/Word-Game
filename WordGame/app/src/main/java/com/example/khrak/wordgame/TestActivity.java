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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.khrak.wordgame.Activities.RoomActivity;
import com.example.khrak.wordgame.Activities.WelcomeActivity;
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

        final Dialog dialog = new Dialog(TestActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.gameover_dialog);

        dialog.show();

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

                Intent intent = new Intent(TestActivity.this, WelcomeActivity.class);

                startActivity(intent);
            }
        });

        handler.postDelayed(runnable, 3000);
    }
}