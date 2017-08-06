package com.example.khrak.wordgame.Activities;

import android.os.Bundle;

import com.example.khrak.wordgame.Game.GameModel;
import com.example.khrak.wordgame.Game.IWordGameListener;
import com.example.khrak.wordgame.Game.WordGame;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.events.InGameEvent;

/**
 * Created by melia on 8/6/2017.
 */

public class LiveGameActivity extends ICommunicatorActivity implements IWordGameListener {

    WordGame mGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        createGame(0);
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

    }
}
