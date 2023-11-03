package com.example.wastegenie.Analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastegenie.DataModels.DropOffData;
import com.example.wastegenie.MainActivity;
import com.example.wastegenie.ProfileActivity;
import com.example.wastegenie.R;
import com.example.wastegenie.TrackingActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;

public class DisposalActivity extends AppCompatActivity {

    public static final String INTENT_MESSAGE = "intent message";
    NavigationView navigationView;
    TextView tvDisposalName, tvCouncilName, tvDisposalAddress, tvDisposalDistance;
    TextView tvConsumerElec, tvCompTele, tvSmallApp, tvLighting, tvOther;
    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposal);

        tvDisposalName = findViewById(R.id.tvDisposalSiteName);
        tvCouncilName = findViewById(R.id.tvDisposalCouncilName);
        tvDisposalAddress = findViewById(R.id.tvDisposalAddress);
        tvDisposalDistance = findViewById(R.id.tvDisposalDistance);
        tvConsumerElec = findViewById(R.id.tvDisposalConsumerElec);
        tvCompTele = findViewById(R.id.tvDisposalCompTele);
        tvSmallApp = findViewById(R.id.tvDisposalSmallApp);
        tvLighting = findViewById(R.id.tvDisposalLighting);
        tvOther = findViewById(R.id.tvDisposalOther);

        Intent intent = getIntent();

        String message = intent.getStringExtra(INTENT_MESSAGE);

        tvDisposalName.setText(message);









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
                    Intent intent = new Intent(DisposalActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(DisposalActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(DisposalActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(DisposalActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.logout) {
                    Toast.makeText(getApplication(), "Logout Selected", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });

    }
}