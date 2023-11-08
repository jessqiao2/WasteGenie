package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.wastegenie.Analysis.AnalysisActivity;
import com.example.wastegenie.Analysis.GuideActivity;
import com.google.android.material.navigation.NavigationView;

public class ProfileActivity extends AppCompatActivity {

    NavigationView navigationView;
    Button btnLogOut;
    Button btnFAQs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Profile Page");

        btnLogOut = findViewById(R.id.btnLogOut);
        btnFAQs = findViewById(R.id.btnFAQs);

        /**
         * Set up navigation view
         */
        navigationView = findViewById(R.id.nav_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.home) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(ProfileActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(ProfileActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    return true;
                } else if (id == R.id.logout) {
                    Toast.makeText(getApplication(), "You have been logged out.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "You have been logged out.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnFAQs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.faq_popup, null);

                // create the popup window
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, 1500, 1000, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });



    }

    /**
     * Method to allow back button to behave like the default Android back button and not like the
     * "up" button.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}