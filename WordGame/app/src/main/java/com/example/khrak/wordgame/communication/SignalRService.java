package com.example.khrak.wordgame.communication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.example.khrak.wordgame.communication.models.EventResponse;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

/**
 * Created by melia on 8/1/2017.
 */

public class SignalRService extends Service {
    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients

    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public void onDestroy() {
        mHubConnection.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        startSignalR();
        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    /**
     * method for clients (activities)
     */
    public void sendEvent(EventResponse event) {
        mHubProxy.invoke("PostEvent", event);
    }

    public void sendCoolMessage(){
        mHubProxy.invoke(String.class, "iAmAvailable", "username", "password", "TransMedic").done(new Action<String>() {
            @Override
            public void run(String s) throws Exception {
                Log.w("SimpleSignalR", s);
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("SimpleSignalR", throwable.toString());
            }
        });
    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("User-Name", CommunicationManager.getInstance().getUserName());
            }
        };

        String serverUrl = CommunicationManager.getInstance().getSignalPath();
        mHubConnection = new HubConnection(serverUrl);
        mHubConnection.setCredentials(credentials);
        String SERVER_HUB_CHAT = CommunicationManager.getInstance().getServerHub();
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);
        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }

        String CLIENT_METHOD_BROADAST_MESSAGE = "PostEvent";
        mHubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE,
                new SubscriptionHandler1<EventResponse>() {
                    @Override
                    public void run(final EventResponse eventResponse) {
                        Log.w("SimpleSignalR", "Received " + eventResponse.eventKey  + " data = " + eventResponse.eventJsonData);
                        System.out.print("Received" + eventResponse.eventKey );
                        CommunicationManager.getInstance().EventReceived(eventResponse);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getApplicationContext(), eventResponse, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                , EventResponse.class);
    }
}