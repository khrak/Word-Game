package com.example.khrak.wordgame.communication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.khrak.wordgame.Activities.SignUpActivity;
import com.example.khrak.wordgame.AppMain;
import com.example.khrak.wordgame.communication.models.EventResponse;
import com.example.khrak.wordgame.communication.models.GameEvent;
import com.example.khrak.wordgame.communication.models.GameEventFactory;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melia on 8/2/2017.
 */

public class CommunicationManager {

    private String mUserName;
    private SignalRService mService;
    private boolean mBound = false;
    private List<IGameEventsListener> mGameEventsListeners = new ArrayList<>();
    private IGameEventsListener mInviteNotificaitonsListener = null;


    public CommunicationManager(){
    }

    public void setUserName(String userName){
        mUserName = userName;
    }

    public String getUserName(){
        return mUserName;
    }

    public void addGameEventListener(IGameEventsListener eventListener){
        mGameEventsListeners.add(eventListener);
    }

    public void removeGameEventListener(IGameEventsListener eventListener){
        if(mGameEventsListeners.contains(eventListener))
            mGameEventsListeners.remove(eventListener);
    }

    public void addInviteNotificationsListener(IGameEventsListener eventListener){
        mInviteNotificaitonsListener = eventListener;
    }

    public void removeInviteNotificationsListener(IGameEventsListener eventListener){
        mInviteNotificaitonsListener = null;
    }

    protected void initializeSignalRService(){
        Intent intent = new Intent();
        intent.setClass(AppMain.getContext(), SignalRService.class);
        AppMain.getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    public void SendEvent(GameEvent event){
        if (mBound) {
            Gson gson = new Gson();
            EventResponse responceEvent = new EventResponse();
            responceEvent.eventKey = event.eventKey;
            responceEvent.eventJsonData = gson.toJson(event.getEventData());
            responceEvent.eventAuthor = getUserName();
            mService.sendEvent(responceEvent);
        }
    }

    public void EventReceived(EventResponse eventResponse){
        GameEvent event = GameEventFactory.getGameEvent(eventResponse);
        if (isInviteNotification(event)){
            if (mInviteNotificaitonsListener != null)
                mInviteNotificaitonsListener.processGameEvent(event);
        }else{
            for (IGameEventsListener eventListener : mGameEventsListeners) {
                eventListener.processGameEvent(event);
            }
        }
    }

    public boolean isInviteNotification(GameEvent event){
        return event.IsSameEvent(GameEventFactory.EVENT_KET_INVITATION_RECEVED);
    }


    /*******************************************************/
    /*********  initialize communication service ***********/
    /*******************************************************/

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private static CommunicationManager mInstance = new CommunicationManager();
    public static CommunicationManager getInstance(){
        return mInstance;
    }

    public static void Initialize(String userName, Activity signUpActivity){

        if (mInstance == null){
            mInstance = new CommunicationManager();
        }

        mInstance.setUserName(userName);
        //SignalrCommunicationMaker communicationMaker = new SignalrCommunicationMaker();
        //communicationMaker.SetCalledActivit(signUpActivity);
        //communicationMaker.execute(mInstance);
        //mInstance.initializeSignalRService();
    }
    
    public String getSignalPath() {
        return "http://amimelia-001-site1.itempurl.com/signalr";
    }

    public String getServerHub() {
        return "SignalRService";
    }
}
