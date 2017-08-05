package com.example.khrak.wordgame.Activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.example.khrak.wordgame.communication.CommunicationManager;
import com.example.khrak.wordgame.communication.IGameEventsListener;
import com.example.khrak.wordgame.communication.NetworkStateReceiver;
import com.example.khrak.wordgame.communication.SignalRService;
import com.example.khrak.wordgame.communication.models.GameEvent;

/**
 * Created by melia on 8/4/2017.
 */

public abstract class ICommunicatorActivity extends AppCompatActivity implements IGameEventsListener, NetworkStateReceiver.NetworkStateReceiverListener{

    private final Context mContext = this;
    private SignalRService mService;
    private boolean mBound = false;
    private NetworkStateReceiver networkStateReceiver;
    private boolean isFirstRun = true;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void bindCommunicationServer(){
        unBindCommunicationService();
        Intent intent = new Intent();
        intent.setClass(mContext, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        CommunicationManager.getInstance().addGameEventListener(this);
    }

    public void unBindCommunicationService(){
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            CommunicationManager.getInstance().removeGameEventListener(this);
        }
    }

    @Override
    protected void onPause(){
        WriteLogMessage("On Pause Happened. Service not unbounded. ");
        super.onPause();
    }

    @Override
    protected void onResume(){
        bindCommunicationServer();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        unBindCommunicationService();
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void networkAvailable() {
        Log.d("InternetState", "I'm in, baby!");
        if (isFirstRun)
        {
            isFirstRun = false;
            return;
        }
        bindCommunicationServer();
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if (progress != null)
                    progress.dismiss();
                progress = null;
                connectionToServerEstabilished();
            }
        });
    }

    @Override
    public void networkUnavailable() {
        Log.d("InternetState", "I'm dancing with myself");
        unBindCommunicationService();
        progress = ProgressDialog.show(this, "Connection Lost",
                "Connection To server...", true);
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

    public void WriteLogMessage(String message){
        System.out.println(message);
        Log.e("Communication", message);
    }

    public abstract void processGameEvent(GameEvent event);
    public abstract void connectionToServerEstabilished();
}
