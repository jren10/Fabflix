package com.example.jason.fabflixproject4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        int position = intent.getIntExtra("position", 0);

        TextView textView = (TextView) findViewById(R.id.mMovieDetails);
        textView.setText(info);

    }

}
