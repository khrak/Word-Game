package com.example.khrak.wordgame.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.WordSearchingFinished;
import com.example.khrak.wordgame.communication.models.events.InGameEvent;
import com.example.khrak.wordgame.communication.models.events.WordSearchFinishedEvent;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class GameActivity extends AppCompatActivity implements IWordGameListener,
IGridButtonListener{
    WordGame mGame;

    private ArrayList<Button> clickedButtons = new ArrayList<>();
    private LinearLayout boardLayout;
    private Thread mTimerThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_activity);

        boardLayout = (LinearLayout) findViewById(R.id.board_to_fill);

        getSupportActionBar().hide();

        final GridView gridView = (GridView) findViewById(R.id.keyboard_gridview);

        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("ა", 1));
        list.add(new Card("ნ", 2));
        list.add(new Card("ა", 1));
        list.add(new Card("ს", 5));
        list.add(new Card("ტ", 7));
        list.add(new Card("ა", 4));
        list.add(new Card("ს", 10));
        list.add(new Card("ი", 4));
        list.add(new Card("ა", 5));

        gridView.setAdapter(new GridViewAdapter(this, list));

        int level = getIntent().getIntExtra("level", 0);

        createGame(level);
    }

    private void createGame(int level) {
        mGame = new WordGame(level, this, true);
        GameEvent startGameEvent = new InGameEvent(GameEventFactory.EVENT_GAME_START);
        mGame.sendGameEvent(startGameEvent);
    }

    public void symbolClicked(View view) {

        Button button = (Button) view;

        System.out.println(button.getBackground());

        if (clickedButtons.contains(button)) {
            System.out.println("Already clicked");
        } else {
            button.setBackgroundColor(Integer.parseInt("000000"));

            System.out.println(button.getText() + " " + button.getTag());
            System.out.println("clicked");

            Button boardButton = (Button) boardLayout.getChildAt(clickedButtons.size());

            boardButton.setText(button.getText().toString());

            clickedButtons.add(button);
        }
    }

    public void submitWord(View view) {

        submitted = true;

        if (mTimerThread != null){
//            mTimerThread.interrupt();
            mTimerThread = null;
        }

        final DonutProgress progress = (DonutProgress) findViewById(R.id.timeout_progress_view);

        progress.setProgress(0);

        sendPlayerChooseWordEvent(getSelectedCards());

        for (int i = 0; i < clickedButtons.size(); i++) {
            Button button = clickedButtons.get(i);

            button.setBackgroundResource(android.R.drawable.btn_default);
        }

        clickedButtons.clear();

        clearBoard();

        /*String result = "";
        int score = 0;

        for (int i = 0; i < clickedButtons.size(); i++) {
            result += clickedButtons.get(i).getText().toString();

            String tag = (String) clickedButtons.get(i).getTag();

            score += Integer.parseInt(tag);
        }

        System.out.println(result + " with score " + score); */
    }

    private Card[] getSelectedCards(){
        Card[] cards = new Card[clickedButtons.size()];
        for (int i = 0; i < clickedButtons.size(); i++) {
            String text = clickedButtons.get(i).getText().toString();
            cards[i] = new Card(text, Integer.parseInt((String) clickedButtons.get(i).getTag()));
        }
        return cards;

    }

    private boolean submitted = false;

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

    private void playerDontChooseWord(){
        sendPlayerChooseWordEvent(new Card[0]);
    }

    private void sendPlayerChooseWordEvent(Card[] cards){
        WordSearchFinishedEvent finishSearchingEvent = new WordSearchFinishedEvent();
        finishSearchingEvent.eventAuthor = GameConstants.OfflinePlayerName;
        WordSearchingFinished dataModel = new WordSearchingFinished();
        dataModel.foundWord = cards;
        finishSearchingEvent.setEventData(dataModel);
        mGame.sendGameEvent(finishSearchingEvent);
    }

    @Override
    public void drawGame(final GameModel mGameModel) {

        if (mGameModel.state == GameStates.GAME_PENDING) {
            drawPendingGame(mGameModel);
        }

        if (mGameModel.state == GameStates.GAME_BETTING_STARTED) {
            drawBetting(mGameModel);
        }

        if (mGameModel.state == GameStates.GAME_BETTING_FINISHED) {
            drawFinish(mGameModel);
        }

        if (mGameModel.state == GameStates.GAME_OVER) {
            drawGameOver(mGameModel);
        }
    }

    private void drawGameOver(final GameModel mGameModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Player player1 = mGameModel.players.get(0);
                Player player2 = mGameModel.players.get(1);

                boolean iwon = player1.money > player2.money;

                final Dialog dialog = new Dialog(GameActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.gameover_dialog);

                TextView textview = (TextView) dialog.findViewById(R.id.game_result_view);

                if (iwon) {
                    textview.setText("You won!");
                } else {
                    textview.setText("You'll win next time!");
                }

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

                        Intent intent = new Intent(GameActivity.this, WelcomeActivity.class);

                        startActivity(intent);
                    }
                });

                handler.postDelayed(runnable, 3000);
            }
        });
    }

    private void drawFinish(final GameModel mGameModel) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ScoreboardItem> list = new ArrayList<>();

                Player player1 = mGameModel.players.get(0);
                Player player2 = mGameModel.players.get(1);

                boolean Iwon = player1.points > player2.points;

                list.add(new ScoreboardItem(player1.guessedWord, player1.points, player1.wordGameUser.IconId, Iwon));
                list.add(new ScoreboardItem(player2.guessedWord, player2.points, player2.wordGameUser.IconId, !Iwon));

                ScoreboardAdapter adapter = new ScoreboardAdapter(GameActivity.this, list);

                final Dialog dialog = new Dialog(GameActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.scoreboard_dialog);

                ListView listView = (ListView) dialog.findViewById(R.id.scoreboard_listview);

                listView.setAdapter(adapter);

                dialog.show();

                // Hide after some seconds
                final Handler handler  = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();

                            scoreboardDismissed();
                        }
                    }
                };

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        handler.removeCallbacks(runnable);
                    }
                });

                handler.postDelayed(runnable, 5000);
            }
        });
    }

    private void scoreboardDismissed() {
        InGameEvent event = new InGameEvent(GameEventFactory.EVENT_GAME_ROUND_FINISH);

        event.eventAuthor = GameConstants.OfflinePlayerName;

        mGame.sendGameEvent(event);
    }

    private void drawBetting(final GameModel mGameModel) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(GameActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.betting_dialog);

                Button button = (Button) dialog.findViewById(R.id.submit_bet_button);
                TextView textview = (TextView) dialog.findViewById(R.id.user_score_view);
                EditText editText = (EditText) dialog.findViewById(R.id.betting_edittext);

                editText.setHint("Your score is " + mGameModel.players.get(0).points);

                textview.setText("Bet range is " + (10 * mGameModel.roundNumber) + " to " +
                        10 * (mGameModel.roundNumber + 1));

                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();

                                    submitBet(0);
                                }
                            }
                        });
                    }
                }).start();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) dialog.findViewById(R.id.betting_edittext);

                        try {
                            int bet = Integer.parseInt(editText.getText().toString());

                            dialog.dismiss();

                            submitBet(bet);

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void submitBet(int bet) {
        InGameEvent event = new InGameEvent(GameEventFactory.OFFLINE_EVENT_BETTING_FINISHED);

        event.eventAuthor = GameConstants.OfflinePlayerName;
        event.eventExtraData = bet;

        mGame.sendGameEvent(event);
    }

    private int value = 0;

    private void drawPendingGame(final GameModel mGameModel) {

        submitted = false;

        mTimerThread = new Thread(new Runnable() {
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

                        playerDontChooseWord();
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

                GridViewAdapter adapter = new GridViewAdapter(GameActivity.this, list);

                gridView.setAdapter(adapter);
            }
        });
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