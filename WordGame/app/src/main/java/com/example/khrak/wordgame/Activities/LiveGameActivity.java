package com.example.khrak.wordgame.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.Adapters.GridViewAdapter;
import com.example.khrak.wordgame.Adapters.IGridButtonListener;
import com.example.khrak.wordgame.Adapters.ScoreboardAdapter;
import com.example.khrak.wordgame.Game.Card;
import com.example.khrak.wordgame.Game.GameConstants;
import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.Game.GameStates;
import com.example.khrak.wordgame.Game.IWordGameListener;
import com.example.khrak.wordgame.Game.Player;
import com.example.khrak.wordgame.Game.WordGame;
import com.example.khrak.wordgame.Model.ScoreboardItem;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.TestActivity;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.events.InGameEvent;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by melia on 8/6/2017.
 */

public class LiveGameActivity extends ICommunicatorActivity implements IWordGameListener,
        IGridButtonListener{

    WordGame mGame;

    private int ROOMID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        getSupportActionBar().hide();

        int roomid = getIntent().getIntExtra("roomid", 0);

        ROOMID = roomid;

        createGame(roomid);
    }

    private void createGame(int gameId) {
        mGame = new WordGame(gameId, this, false);
    }

    private void showGameOverDialog(String message, Intent intent) {

        final Dialog dialog = new Dialog(LiveGameActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.gameover_dialog);

        TextView textView = (TextView) dialog.findViewById(R.id.game_result_view);

        textView.setText(message);

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
            }
        });

        handler.postDelayed(runnable, 3000);

        startActivity(intent);
    }

    @Override
    public void processGameEvent(GameEvent event) {
        if (event.IsSameEvent(GameEventFactory.LIVE_GAME_EVENT_WON)) {

            Intent intent = new Intent(this, LobbyActivity.class);

            this.finish();

            showGameOverDialog("You Won!", intent);

            return;
        }

        if (event.IsSameEvent(GameEventFactory.LIVE_GAME_EVENT_LOST)) {
            Intent intent = new Intent(this, LobbyActivity.class);

            this.finish();

            showGameOverDialog("You'll win next time!", intent);

            return;
        }

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

    Dialog activeDialog = null;

    @Override
    public void drawGame(GameModel mGameModel) {
        if (mGameModel.state == GameStates.NEW_ROUND) {
            if (activeDialog != null && activeDialog.isShowing()) {
                activeDialog.dismiss();

                activeDialog = null;
            }

            drawRound(mGameModel);
        }

        if (mGameModel.state == GameStates.GAME_PENDING) {
            if (activeDialog != null && activeDialog.isShowing()) {
                activeDialog.dismiss();

                activeDialog = null;
            }

            drawPendingGame(mGameModel);
        }

        if (mGameModel.state == GameStates.GAME_BETTING_STARTED) {
            if (activeDialog != null && activeDialog.isShowing()) {
                activeDialog.dismiss();

                activeDialog = null;
            }

            drawBetting(mGameModel);
        }

        if (mGameModel.state == GameStates.GAME_BETTING_FINISHED) {
            if (activeDialog != null && activeDialog.isShowing()) {
                activeDialog.dismiss();

                activeDialog = null;
            }

            drawFinish(mGameModel);
        }
    }

    private void drawFinish(final GameModel mGameModel) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ScoreboardItem> list = new ArrayList<>();

                Player winner = null;

                for (Player player : mGameModel.players) {
                    if (winner == null || winner.points < player.points) {
                        winner = player;
                    }
                }

                for (Player player : mGameModel.players) {
                    if (!winner.wordGameUser.UserName.equals(player.wordGameUser.UserName)) {

                        ScoreboardItem item = new ScoreboardItem(player.guessedWord,
                                player.points, player.wordGameUser.IconId, false);

                        list.add(item);
                    } else {
                        ScoreboardItem item = new ScoreboardItem(player.guessedWord,
                                player.points, player.wordGameUser.IconId, true);

                        list.add(item);
                    }
                }

                ScoreboardAdapter adapter = new ScoreboardAdapter(LiveGameActivity.this, list);

                activeDialog = new Dialog(LiveGameActivity.this);

                activeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                activeDialog.setContentView(R.layout.scoreboard_dialog);

                Button button = (Button) activeDialog.findViewById(R.id.round_number_btn);

                button.setText("Round " + mGameModel.roundNumber);

                ListView listView = (ListView) activeDialog.findViewById(R.id.scoreboard_listview);

                listView.setAdapter(adapter);

                activeDialog.show();

            }
        });
    }

    private void drawBetting(final GameModel mGameModel) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activeDialog = new Dialog(LiveGameActivity.this);

                Player me = null;

                for (Player player : mGameModel.players) {

                    if (player.wordGameUser.UserName.equals(CommunicationManager.getInstance().getUserName())) {
                        me = player;
                        break;
                    }
                }

                activeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                activeDialog.setContentView(R.layout.betting_dialog);

                Button button = (Button) activeDialog.findViewById(R.id.submit_bet_button);
                TextView textview = (TextView) activeDialog.findViewById(R.id.user_score_view);
                EditText editText = (EditText) activeDialog.findViewById(R.id.betting_edittext);

                editText.setHint("Your score is " + me.points);

                final int rangemin = 10 * mGameModel.roundNumber;
                final int rangemax = rangemin + 10;

                textview.setText("Bet range is " + rangemin + " to " + rangemax);

                activeDialog.show();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) activeDialog.findViewById(R.id.betting_edittext);

                        try {
                            int bet = Integer.parseInt(editText.getText().toString());

                            if (rangemin <= bet && bet <= rangemax) {
                                submitBet(bet);
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void submitBet(int bet) {

        String username = CommunicationManager.getInstance().getUserName();
        String roomid   = "" + ROOMID;

        String url = "http://amimelia-001-site1.itempurl.com/api/game/BetPlaced?userName=" +
                username + "&gameId=" + roomid + "&betAmount=" + bet;

        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        System.out.println(url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void drawRound(GameModel mGameModel) {
        activeDialog = new Dialog(LiveGameActivity.this);

        activeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        activeDialog.setContentView(R.layout.round_dialog);

        TextView textView = (TextView) activeDialog.findViewById(R.id.round_btn);

        textView.setText("Round " + mGameModel.roundNumber);

        activeDialog.show();

    }

    private static boolean submitted = false;
    private static int value = 0;

    private void updateProgressBar(int progress) {
        final DonutProgress progressbar = (DonutProgress) findViewById(R.id.timeout_progress_view);

        progressbar.setProgress(progress);
    }

    private void drawPendingGame(final GameModel mGameModel) {

        System.out.println("Drawing pending game");

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

                Player me = null;

                for (Player player : mGameModel.players) {
                    String username = player.wordGameUser.UserName;
                    String money = "" + player.money;
                    int iconid = player.wordGameUser.IconId;

                    if (!username.equals(CommunicationManager.getInstance().getUserName())) {
                        addPlayer(playersLayout, username, money, iconid);
                    } else me = player;
                }

                clearBoard();

                final GridView gridView = (GridView) findViewById(R.id.keyboard_gridview);

                List<Card> cards = mGameModel.cards;

                ArrayList<Card> list = new ArrayList<>(cards);

                Card[] twoCards = me.cards;

                list.add(twoCards[0]);
                list.add(twoCards[1]);

                GridViewAdapter adapter = new GridViewAdapter(LiveGameActivity.this, list);

                gridView.setAdapter(adapter);
            }
        });
    }

    public void submitWord(View view) {

        submitted = true;

        final DonutProgress progress = (DonutProgress) findViewById(R.id.timeout_progress_view);

        progress.setProgress(0);

        String st = "";
        int score = 0;

        for (int i = 0; i < clickedButtons.size(); i++) {
            Button button = clickedButtons.get(i);

            button.setBackgroundResource(android.R.drawable.btn_default);

            st += button.getText();
            score += Integer.parseInt((String) button.getTag());
        }

        String username = CommunicationManager.getInstance().getUserName();
        String roomid   = "" + ROOMID;

        clickedButtons.clear();

        clearBoard();

        String url = "http://amimelia-001-site1.itempurl.com/api/game/WordGuessed?userName=" +
                username + "&gameId=" + roomid + "&word=" + st + "&score="  + score;

        sendSubmition(url);
    }

    private void sendSubmition(String url) {
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        System.out.println(url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private Card[] getSelectedCards(){
        Card[] cards = new Card[clickedButtons.size()];
        for (int i = 0; i < clickedButtons.size(); i++) {
            String text = clickedButtons.get(i).getText().toString();
            cards[i] = new Card(text, Integer.parseInt((String) clickedButtons.get(i).getTag()));
        }
        return cards;

    }

    public void symbolClicked(View view) {

        try {
            Button button = (Button) view;

            System.out.println(button.getBackground());

            if (clickedButtons.contains(button)) {
                System.out.println("Already clicked");
            } else {
                button.setBackgroundColor(Integer.parseInt("000000"));

                System.out.println(button.getText() + " " + button.getTag());
                System.out.println("clicked");

                LinearLayout boardLayout = (LinearLayout) findViewById(R.id.board_to_fill);

                Button boardButton = (Button) boardLayout.getChildAt(clickedButtons.size());

                boardButton.setText(button.getText().toString());

                clickedButtons.add(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Button> clickedButtons = new ArrayList<>();
    private LinearLayout boardLayout;

    public void clearClicked(View view) {

        for (int i = 0; i < clickedButtons.size(); i++) {
            Button button = clickedButtons.get(i);

            button.setBackgroundResource(android.R.drawable.btn_default);
        }

        clickedButtons.clear();

        clearBoard();
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

    @Override
    public void gridButtonClicked(View view) {
        symbolClicked(view);
    }
}
