package com.example.jason.fabflixproject4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }

    public void login(View view) {
        String mEmail = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String mPassword = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
        final Map<String, String> params = new HashMap<>();
        params.put("email", mEmail);
        params.put("password", mPassword);

        // aws url
        String url = ("https://ec2-18-220-242-149." +
                "us-east-2.compute.amazonaws.com:8443/project4/api/android-login");

    // Use the same network queue across out application
    final RequestQueue queue = SessionManager.sharedManager(this).queue;

    Log.d("myTag","Button Pressed");

    // Connect to AWS
    final StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String status = response;
                    System.out.println(status);
                    try {
                        JSONObject object = new JSONObject(response);
                        System.out.println("status = " + object.get("status").toString());
                        if (object.get("status").equals("success")) {
                            Intent goToIntent = new Intent(LoginActivity.this, SearchActivity.class);
                            startActivity(goToIntent);
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Log.d("security.error", error.toString());

                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }
    ) {
        @Override
        protected Map<String, String> getParams() {
            return params;
        }
    };
        queue.add(loginRequest);
    }
}