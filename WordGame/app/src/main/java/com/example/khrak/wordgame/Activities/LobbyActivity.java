package com.example.khrak.wordgame.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.khrak.wordgame.Adapters.LobbyAdapter;
import com.example.khrak.wordgame.Model.Room;
import com.example.khrak.wordgame.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


public class LobbyActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final int PLUS_ONE_REQUEST_CODE = 0;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity);

        getSupportActionBar().hide();

        ListView listView = (ListView) findViewById(R.id.rooms_list);

        ArrayList<Room> rooms = new ArrayList<>();

        rooms.add(new Room("Black", "Public", 3));
        rooms.add(new Room("White", "Public", 2));
        rooms.add(new Room("Jack", "Public", 1));
        rooms.add(new Room("Sparrow", "Public", 4));
        rooms.add(new Room("Gym", "Public", 4));
        rooms.add(new Room("Contestants", "Public", 5));
        rooms.add(new Room("Bulls", "Public", 3));
        rooms.add(new Room("Poker", "Public", 2));
        rooms.add(new Room("Face", "Public", 1));
        rooms.add(new Room("Sooo", "Public", 5));

        LobbyAdapter adapter = new LobbyAdapter(rooms, this);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                TextView textView = (TextView) view.findViewById(R.id.room_name);

                System.out.println(textView.getText().toString());
            }
        });


        final FloatingActionButton createRoom = (FloatingActionButton) findViewById(R.id.createroom_button);

        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(LobbyActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.test_activity);

                final EditText editText = (EditText) dialog.findViewById(R.id.editText);
                Button btnSave          = (Button) dialog.findViewById(R.id.save);
                Button btnCancel        = (Button) dialog.findViewById(R.id.cancel);
                dialog.show();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final FloatingActionButton logoutButton = (FloatingActionButton) findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (signedWithGoogle()) {
                        googleSignOut();
                    }

                    if (SignUpActivity.signedWithFacebook()) {
                        SignUpActivity.facebookSignOut();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LobbyActivity.this, WelcomeActivity.class);

                startActivity(intent);
            }
        });
    }

    private boolean signedWithGoogle() {
        return Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).isDone();
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
