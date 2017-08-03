package com.example.khrak.wordgame.Activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.R;
import com.example.khrak.wordgame.communication.CommunicationManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private static GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        context = this;

        mGoogleApiClient = WelcomeActivity.mGoogleApiClient;

        if (signedUp()) {
            if (Profile.getCurrentProfile() != null) {
                getUserName("f"+ Profile.getCurrentProfile().getId());
            }

            if (Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).isDone()) {
                GoogleSignInAccount account = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).get().getSignInAccount();

                getUserName("g" + account.getId());
            }
        } else {

            setContentView(R.layout.signup_activity);

            getSupportActionBar().hide();

            loginButton = (LoginButton) findViewById(R.id.login_button);

            callbackManager = CallbackManager.Factory.create();

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getUserName("f" + loginResult.getAccessToken().getUserId());
                }

                @Override
                public void onCancel() {
                    System.out.println("Canceled");
                }

                @Override
                public void onError(FacebookException e) {
                    e.printStackTrace();
                    System.out.println("Error");
                }
            });

            findViewById(R.id.sign_in_button).setOnClickListener(this);

            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
        }
    }

    private static boolean signedUp() {
        return signedWithFacebook() || signedWithGoogle();
    }

    public static boolean signedWithFacebook() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    public static boolean signedWithGoogle() {
        return Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).isDone();
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
                                    Intent intent = new Intent(context, ChooseUsernameActivity.class);

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

        CommunicationManager.Initialize(username, SignUpActivity.this);
        Intent intent = new Intent(context, LobbyActivity.class);

        intent.putExtra("username", username);

        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            System.out.println(data.toString());
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            getUserName("g" + acct.getId());

            updateUI(true);
        } else {
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    public static void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    public static void facebookSignOut() {
        LoginManager.getInstance().logOut();
    }

    public void signOut(View view) {
        signOut();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}
