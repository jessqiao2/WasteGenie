package com.example.wastegenie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GuideActivity extends AppCompatActivity {

    public static final String INTENT_MESSAGE = "intent message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }
}