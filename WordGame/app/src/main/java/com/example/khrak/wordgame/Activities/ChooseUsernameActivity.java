package com.example.khrak.wordgame.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.khrak.wordgame.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ChooseUsernameActivity extends AppCompatActivity {

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.choose_username_activity);

        getSupportActionBar().hide();

        Intent intent = getIntent();

        final String userid = intent.getStringExtra("userid");

        EditText editText = (EditText) findViewById(R.id.edit_text);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(s);
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            System.out.println("Enter pressed");
                            EditText editText = (EditText) v;

                            registerNewUser(editText.getText().toString(), userid);

                            editText.setText("");

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        Button submit = (Button) findViewById(R.id.submit_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.edit_text);


                registerNewUser(editText.getText().toString(), userid);
                editText.setText("");
            }
        });
    }

    private void registerNewUser(final String username, String userid) {
        // Instantiate the RequestQueue.
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);

        String keyPath = "http://amimelia-001-site1.itempurl.com/api/user/AddUserName/?useruid=" +
                              userid + "&username=" + username;

        System.out.println(keyPath);

        System.out.println("Adding new user with " + username + " " + userid);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, keyPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        try {
                            JSONObject json = new JSONObject(response);

                            if (json.has("username") && json.get("username").equals("user.already.exists")) {
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(ChooseUsernameActivity.this).
                                        setTitle("User name already exists").
                                        setMessage("Choose another one");

                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                });

                                final AlertDialog alert = dialog.create();
                                alert.show();

                                // Hide after some seconds
                                final Handler handler  = new Handler();
                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (alert.isShowing()) {
                                            alert.dismiss();
                                        }
                                    }
                                };

                                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        handler.removeCallbacks(runnable);
                                    }
                                });

                                handler.postDelayed(runnable, 3000);
                            }

                            if (json.has("messageId") && json.getInt("messageId") == 1) {
                                Intent intent = new Intent(context, LobbyActivity.class);

                                intent.putExtra("username", username);

                                startActivity(intent);
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
}
