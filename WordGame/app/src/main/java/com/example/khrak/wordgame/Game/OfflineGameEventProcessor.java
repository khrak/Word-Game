package com.example.khrak.wordgame.Game;

import com.example.khrak.wordgame.communication.models.GameEvent;

/**
 * Created by melia on 8/5/2017.
 */

public class OfflineGameEventProcessor extends AbstractGameEventsProcessor {

    public OfflineGameEventProcessor(WordGame game){
        super(game);
        initComputerGameModel();
    }

    private void initComputerGameModel(){
        mGameModel = new GameModel();

        Player youPlayer = new Player();
        Player computerPlayer = new Player();

        mGameModel.players.add(youPlayer);
        mGameModel.players.add(computerPlayer);

        mGameModel.state = GameStates.GAME_PENDING;
        mGameModel.roundNumber = 1;
        //mGameModel.cards

    }

    @Override
    public void refreshGameModel() {
        gameModelUpdated();
    }

    @Override
    public void processGameEvent(GameEvent eventToProcess) {

    }
}
