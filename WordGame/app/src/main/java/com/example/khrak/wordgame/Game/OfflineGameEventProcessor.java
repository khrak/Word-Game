package com.example.khrak.wordgame.Game;

import com.example.khrak.wordgame.Utils.CardGenerator;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.WordGameUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by melia on 8/5/2017.
 */

public class OfflineGameEventProcessor extends AbstractGameEventsProcessor {

    Random rdForIcons = new Random();

    public OfflineGameEventProcessor(WordGame game){
        super(game);
        initComputerGameModel();
    }

    private void initComputerGameModel(){
        mGameModel = new GameModel();

        Player youPlayer = new Player();
        Player computerPlayer = new Player();

        Card[] randomCards = CardGenerator.getRandomCards(11);
        mGameModel.cards = new ArrayList<Card>(Arrays.asList(Arrays.copyOfRange(randomCards, 0, 7)));

        computerPlayer.cards = Arrays.copyOfRange(randomCards, 9, 11);
        computerPlayer.money = GameConstants.InitialStartAmount;
        computerPlayer.points = 0;
        computerPlayer.wordGameUser = generateRandomWordGameUser(GameConstants.ComputerPlayerName);
        computerPlayer.guessedWord = "";

        youPlayer.cards =  Arrays.copyOfRange(randomCards, 7, 9);
        youPlayer.money = GameConstants.InitialStartAmount;
        youPlayer.points = 0;
        youPlayer.wordGameUser = generateRandomWordGameUser("You");
        computerPlayer.guessedWord = "";

        mGameModel.players = new ArrayList<>();
        mGameModel.players.add(youPlayer);
        mGameModel.players.add(computerPlayer);

        mGameModel.state = GameStates.GAME_PENDING;
        mGameModel.roundNumber = 1;
    }

    private WordGameUser generateRandomWordGameUser(String userName){
        WordGameUser user = new WordGameUser();
        user.UserName = userName;
        user.IconId = rdForIcons.nextInt(10);
        user.Id = 0;
        return user;
    }

    @Override
    public void refreshGameModel() {
        gameModelUpdated();
    }

    @Override
    public void processGameEvent(GameEvent eventToProcess) {
        if (eventToProcess.IsSameEvent(GameEventFactory.EVENT_GAME_START)){
            mGameModel.state = GameStates.GAME_PENDING;
            refreshGameModel();
            return;
        }

        if (eventToProcess.IsSameEvent(GameEventFactory.EVENT_GAME_ROUND_FINISH)){
            mGameModel.state = GameStates.GAME_PENDING;
        }
    }
}
