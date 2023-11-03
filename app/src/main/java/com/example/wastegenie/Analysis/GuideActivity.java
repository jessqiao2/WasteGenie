package com.example.wastegenie.Analysis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.wastegenie.R;

public class GuideActivity extends AppCompatActivity {

    public static final String INTENT_MESSAGE = "intent message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }
}