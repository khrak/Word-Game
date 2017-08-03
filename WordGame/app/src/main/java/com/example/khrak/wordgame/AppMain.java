package com.example.khrak.wordgame;

/**
 * Created by melia on 8/1/2017.
 */
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.khrak.wordgame.communication.SignalRService;


public class AppMain extends Application{

    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

}