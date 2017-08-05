package com.example.khrak.wordgame.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

public class GameActivity extends AppCompatActivity implements IWordGameListener {
    WordGame mGame;

    private ArrayList<Button> clickedButtons = new ArrayList<>();
    private LinearLayout boardLayout;


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
        createGame();
    }

    private void createGame() {
        mGame = new WordGame(0, this, true);
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
        String result = "";
        int score = 0;

        for (int i = 0; i < clickedButtons.size(); i++) {
            result += clickedButtons.get(i).getText().toString();

            String tag = (String) clickedButtons.get(i).getTag();

            score += Integer.parseInt(tag);
        }

        System.out.println(result + " with score " + score);
    }

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

    @Override
    public void drawGame(final GameModel mGameModel) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final DonutProgress progress = (DonutProgress) findViewById(R.id.timeout_progress_view);

                int value = 0;

                while (value < 100) {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    value++;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setProgress(progress.getProgress() + 1);
                        }
                    });
                }
            }
        }).start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout playersLayout = (LinearLayout) findViewById(R.id.players_layout);

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
}