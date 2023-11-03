package com.example.wastegenie.Analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastegenie.DataModels.ResourceData;
import com.example.wastegenie.MainActivity;
import com.example.wastegenie.ProfileActivity;
import com.example.wastegenie.R;
import com.example.wastegenie.TrackingActivity;
import com.google.android.material.navigation.NavigationView;

public class GuideActivity extends AppCompatActivity {

    public static final String INTENT_MESSAGE = "intent message";
    NavigationView navigationView;
    TextView tvHeading, tvSource;
    WebView wvGuide;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        tvHeading = findViewById(R.id.tvResourceTitle);
        tvSource = findViewById(R.id.tvResourceSource);
        wvGuide = findViewById(R.id.wvGuide);

        Intent intent = getIntent();
        String heading = intent.getStringExtra(INTENT_MESSAGE);
        tvHeading.setText(heading);

        ResourceData resources = ResourceData.findResources(heading);

        if (resources != null) {
            tvSource.setText(resources.getSource());
            url = resources.getUrl();
        }

        wvGuide.loadUrl(url);

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
                    Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(GuideActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(GuideActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(GuideActivity.this, ProfileActivity.class);
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