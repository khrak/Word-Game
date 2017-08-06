package com.example.khrak.wordgame.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.khrak.wordgame.Adapters.GridViewAdapter;
import com.example.khrak.wordgame.Game.Card;
import com.example.khrak.wordgame.Game.GameConstants;
import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.Game.GameStates;
import com.example.khrak.wordgame.Game.IWordGameListener;
import com.example.khrak.wordgame.Game.Player;
import com.example.khrak.wordgame.Game.WordGame;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.events.InGameEvent;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melia on 8/6/2017.
 */

public class LiveGameActivity extends ICommunicatorActivity implements IWordGameListener {

    WordGame mGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        int roomid = getIntent().getIntExtra("roomid", 0);

        createGame(roomid);
    }

    private void createGame(int gameId) {
        mGame = new WordGame(gameId, this, false);
    }

    @Override
    public void processGameEvent(GameEvent event) {
        mGame.sendGameEvent(event);
    }

    @Override
    public void connectionToServerEstabilished() {
        mGame.ConnectionReAvailable();
    }

    @Override
    public void onResume(){
        mGame.ResumeActivity();
        super.onResume();
    }

    @Override
    public void drawGame(GameModel mGameModel) {
        if (mGameModel.state == GameStates.GAME_PENDING) {
            drawPendingGame(mGameModel);
        }
    }

    private static boolean submitted = false;
    private static int value = 0;

    private void updateProgressBar(int progress) {
        final DonutProgress progressbar = (DonutProgress) findViewById(R.id.timeout_progress_view);

        progressbar.setProgress(progress);
    }

    private void drawPendingGame(final GameModel mGameModel) {

        submitted = false;

        Thread mTimerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                final DonutProgress progress = (DonutProgress) findViewById(R.id.timeout_progress_view);

                value = 0;

                while (value < 100) {

                    if(submitted){
                        return;
                    }

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(submitted){
                        return;
                    }

                    value++;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setProgress(progress.getProgress() + 1);
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setProgress(0);

//                        playerDontChooseWord();
                    }
                });
            }
        });

        mTimerThread.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout playersLayout = (LinearLayout) findViewById(R.id.players_layout);

                playersLayout.removeAllViews();

                for (Player player : mGameModel.players) {
                    String username = player.wordGameUser.UserName;
                    String money = "" + player.money;
                    int iconid = player.wordGameUser.IconId;

                    if (!username.equals(GameConstants.OfflinePlayerName)) {
                        addPlayer(playersLayout, username, money, iconid);
                    }
                }

                clearBoard();

                final GridView gridView = (GridView) findViewById(R.id.keyboard_gridview);

                List<Card> cards = mGameModel.cards;

                ArrayList<Card> list = new ArrayList<>(cards);

                Player player = mGameModel.players.get(0);

                Card[] twoCards = player.cards;

                list.add(twoCards[0]);
                list.add(twoCards[1]);

                GridViewAdapter adapter = new GridViewAdapter(LiveGameActivity.this, list);

                gridView.setAdapter(adapter);
            }
        });
    }


    private void clearBoard() {
        LinearLayout boardLayout = (LinearLayout) findViewById(R.id.board_to_fill);

        for (int i = 0; i < boardLayout.getChildCount(); i++) {
            Button button = (Button) boardLayout.getChildAt(i);

            button.setText("");
        }
    }

    private void addPlayer(LinearLayout playersLayout, String username, String money, int iconid) {

        LayoutInflater inflater;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout player = (LinearLayout) inflater.inflate(R.layout.game_playerview, null);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.weight = 1;

        player.setLayoutParams(lp);

        TextView moneyView = (TextView) player.findViewById(R.id.money1);

        ImageView profileView = (ImageView) player.findViewById(R.id.profile1);

        TextView nameView = (TextView) player.findViewById(R.id.name_view);

        moneyView.setText(money);
        profileView.setImageResource(avatars[iconid]);
        nameView.setText(username);

        playersLayout.addView(player);
    }

    private int[] avatars = new int[] {
            R.drawable.avatar_icon1,
            R.drawable.avatar_icon2,
            R.drawable.avatar_icon3,
            R.drawable.avatar_icon4,
            R.drawable.avatar_icon5,
            R.drawable.avatar_icon6,
            R.drawable.avatar_icon7,
            R.drawable.avatar_icon8,
            R.drawable.avatar_icon9,
            R.drawable.avatar_icon10
    };
}
