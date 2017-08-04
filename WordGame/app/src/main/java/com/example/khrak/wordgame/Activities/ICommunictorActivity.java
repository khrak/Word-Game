package com.example.khrak.wordgame.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.khrak.wordgame.communication.SignalRService;

/**
 * Created by melia on 8/4/2017.
 */

public class ICommunictorActivity extends AppCompatActivity {
    private final Context mContext = this;
    private SignalRService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void bindCommunicationServer(){
        Intent intent = new Intent();
        intent.setClass(mContext, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unBindCommunicationService(){
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onPause(){
        bindCommunicationServer();
        super.onPause();
    }

    @Override
    protected void onResume(){
        unBindCommunicationService();
        super.onResume();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            WriteLogMessage("Connection Made");
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            WriteLogMessage("Disconnected!");
            mBound = false;
        }
    };

    private void WriteLogMessage(String message){
        System.out.println(message);
        Log.e("Communication", message);
    }
}