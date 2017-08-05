package com.example.khrak.wordgame.Game;

import android.os.health.PidHealthStats;

import com.example.khrak.wordgame.Model.CardScoringResult;
import com.example.khrak.wordgame.Utils.CardGenerator;
import com.example.khrak.wordgame.Utils.DatabaseWordHelper;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.example.khrak.wordgame.communication.models.WordGameUser;
import com.example.khrak.wordgame.communication.models.WordSearchingFinished;
import com.example.khrak.wordgame.communication.models.events.InGameEvent;
import com.example.khrak.wordgame.communication.models.events.WordSearchFinishedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by melia on 8/5/2017.
 */

public class OfflineGameEventProcessor extends AbstractGameEventsProcessor {

    Random rdForIcons = new Random();
    private int gameLevel = 0;
    private static int computerPlayerId = 1;
    private static int youPlayerId = 0;

    public OfflineGameEventProcessor(int gameLevel, WordGame game){
        super(game);
        initComputerGameModel();
        this.gameLevel = gameLevel;
    }

    public OfflineGameEventProcessor(WordGame game){
        super(game);
        initComputerGameModel();
        this.gameLevel = 1;
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
        youPlayer.wordGameUser = generateRandomWordGameUser(GameConstants.OfflinePlayerName);
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
            computerStartWordSearching();
            return;
        }

        if (eventToProcess.IsSameEvent(GameEventFactory.OFFLINE_EVENT_MOVE_FINISHED)){

            WordSearchFinishedEvent event = (WordSearchFinishedEvent) eventToProcess;
            WordSearchingFinished eventData = event.getEventData();

            CardScoringResult scoringResult = DatabaseWordHelper.scoreCardsOrder(eventData.foundWord);
            addPlayerForScore(event.eventAuthor, scoringResult.resultScore, scoringResult.foundWord);
            mGameModel.state = GameStates.GAME_PENDING;
            return;
        }

        if (eventToProcess.IsSameEvent(GameEventFactory.OFFLINE_EVENT_BETTING_STARTED)){
            mGameModel.state = GameStates.GAME_BETTING_STARTED;
            mGameModel.players.get(computerPlayerId).bet = (getRoundMinAmount() + getRoundMaxAmount()) / 2;
            refreshGameModel();
            return;
        }

        if (eventToProcess.IsSameEvent(GameEventFactory.OFFLINE_EVENT_BETTING_FINISHED)){
            mGameModel.state = GameStates.GAME_BETTING_FINISHED;
            InGameEvent inGameEvent = (InGameEvent) eventToProcess;
            mGameModel.players.get(youPlayerId).bet = (Integer) inGameEvent.eventExtraData;

            refreshGameModel();
        }

        if (eventToProcess.IsSameEvent(GameEventFactory.EVENT_GAME_ROUND_FINISH)){
            mGameModel.state = GameStates.ROUND_FINISHED;
            refreshGameModel();
        }
    }

    private int getRoundMinAmount(){
       return mGameModel.roundNumber * 10;
    }

    private int getRoundMaxAmount(){
        return getRoundMinAmount() + 10;
    }

    private void addStartBettingEvent(){
        GameEvent startBettingEvent = new InGameEvent(GameEventFactory.OFFLINE_EVENT_BETTING_STARTED);
        processGameEvent(startBettingEvent);
    }

    private boolean checkGameFinished(){
        for(int pId = 0; pId < mGameModel.players.size(); pId++) {
            Player curPlayer = mGameModel.players.get(pId);
            if (!curPlayer.hasSubmittedWord)
                return false;
        }
        return true;
    }

    private void addPlayerForScore(String userName, int playerScore, String guessedWord){
        for(int pId = 0; pId < mGameModel.players.size(); pId++){
            Player curPlayer = mGameModel.players.get(pId);
            if (curPlayer.wordGameUser.UserName.equals(userName)){
                curPlayer.hasSubmittedWord = true;
                curPlayer.points = playerScore;
                curPlayer.guessedWord = guessedWord;
                break;
            }
        }

        if (checkGameFinished()){
            addStartBettingEvent();
        }
    }

    private Card[] getComputerCards(){
        Card[] computerCards = new Card[9];
        for (int i = 0; i < mGameModel.cards.size(); i++){
            computerCards[i] = mGameModel.cards.get(i);
        }
        computerCards[7] = mGameModel.players.get(1).cards[0];
        computerCards[7] = mGameModel.players.get(1).cards[1];
        return computerCards;
    }

    private void computerStartWordSearching(){

        final Card[] computerCards = getComputerCards();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    //TODO recursion here to calculate cards
                    mResultAlreadyFound = false;
                    goodSolutionsFoundNum = 0;
                    currentMaxScore = 0;
                    checkWord(computerCards, "", new boolean[9], 0, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
    private boolean mResultAlreadyFound = false;
    private int goodSolutionsFoundNum = 0;
    private int currentMaxScore = 0;

    private void checkWord(Card[] cards, String soFar, boolean[] used, int curIndx, int curScore){

        if (mResultAlreadyFound) return;
        if (curIndx == 9)
            return;

        if (DatabaseWordHelper.wordExists(soFar))
        {
            if(curScore > currentMaxScore){
                currentMaxScore = curScore;
                goodSolutionsFoundNum++;
                if (goodSolutionsFoundNum >= GameConstants.LeveAlgorithmGoodSolutionsStop[gameLevel]){
                    addPlayerForScore(GameConstants.ComputerPlayerName, currentMaxScore, soFar);
                    mResultAlreadyFound = true;
                    return;
                }
            }
        }

        if (soFar.length() >= GameConstants.MinWordLengthToCheckExistance){
            if (!DatabaseWordHelper.isPossibleWord(soFar))
                return;
        }

        for (int i = 0; i < used.length; i++){
            if (mResultAlreadyFound)
                return;
            if (!used[i]){
                used[i] = true;
                checkWord(cards, soFar + cards[i].symbol, used, curIndx + 1, curScore + cards[i].score);
                used[i] = false;
            }
        }
    }

}
