package com.example.khrak.wordgame.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.example.khrak.wordgame.Adapters.GridViewAdapter;
import com.example.khrak.wordgame.Game.Card;
import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.Game.IWordGameListener;
import com.example.khrak.wordgame.Game.WordGame;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.events.InGameEvent;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements IWordGameListener {
    WordGame mGame;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_activity);

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

    private void createGame(){
        mGame = new WordGame(0, this, false);
        GameEvent startGameEvent = new InGameEvent(GameEventFactory.EVENT_GAME_START);
        mGame.sendGameEvent(startGameEvent);
    }

    public void symbolClicked(View view) {

        Button button = (Button) view;

        System.out.println(button.getText() + " " + button.getTag());
        System.out.println("clicked");
    }



    @Override
    public void drawGame(GameModel mGameModel) {

    }
}
