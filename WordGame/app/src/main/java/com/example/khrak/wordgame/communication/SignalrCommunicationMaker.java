package com.example.khrak.wordgame.communication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by melia on 8/3/2017.
 */

public class SignalrCommunicationMaker extends AsyncTask<CommunicationManager, Void, String> {

    @Override
    protected String doInBackground(CommunicationManager... params) {
        params[0].initializeSignalRService();
        return "";
    }

    private Activity mCallerActivity;
    public void SetCalledActivit(Activity callerActivity){
        mCallerActivity = callerActivity;
    }

    private ProgressDialog pdia;

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pdia = new ProgressDialog(mCallerActivity);
        pdia.setTitle("Please Wait");
        pdia.setMessage("Connection to server...");
        pdia.show();
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        pdia.dismiss();
    }
}
