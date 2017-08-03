package com.example.khrak.wordgame.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.TestActivity;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.facebook.Profile;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null ) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        setContentView(R.layout.welcome_activity);

        getSupportActionBar().hide();
    }

    public void aboutClicked(View view) {

        Intent intent = new Intent(this, AboutActivity.class);

        startActivity(intent);
    }

    public void singlePlayerClicked(View view) {
        Intent intent = new Intent(this, TestActivity.class);

        startActivity(intent);
    }


    public void multiplayerClicked(View view) {

        if (Profile.getCurrentProfile() != null) {
            getUserName("f"+ Profile.getCurrentProfile().getId());
            System.out.println("facebook loged in");
        } else {

            if (Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).isDone()) {
                GoogleSignInAccount account = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).get().getSignInAccount();
                System.out.println("Google loged in");

                getUserName("g" + account.getId());
            } else {
                Intent intent = new Intent(this, SignUpActivity.class);

                startActivity(intent);
            }
        }
    }

    private void getUserName(final String userid) {
        // Instantiate the RequestQueue.
        final com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        String keyPath = "http://amimelia-001-site1.itempurl.com/api/user/GetUser?userUid=" + userid;

        System.out.println(keyPath);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, keyPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            JSONObject json = new JSONObject(response);

                            if (json.has("username")) {
                                if (json.get("username").equals("not.found")) {
                                    Intent intent = new Intent(WelcomeActivity.this, ChooseUsernameActivity.class);

                                    intent.putExtra("userid", userid);

                                    startActivity(intent);

                                } else {

                                    System.out.println("user name is " + json.getString("username"));

                                    goToLobby(json.getString("username"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void goToLobby(String username) {

        CommunicationManager.Initialize(username, WelcomeActivity.this);

        Intent intent = new Intent(this, LobbyActivity.class);

        intent.putExtra("username", username);

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
