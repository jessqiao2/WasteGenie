package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

public class CouncilTracking extends AppCompatActivity implements OnMapReadyCallback {
    String councilName = null;
    TextView tvTrucksDisplayCT;
    TextView tvCouncilDisplayCT;
    NavigationView navigationView;
    GoogleMap map;
    private MapView mvCouncilTracking;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_council_tracking);
        setTitle("Council Routes Page");

        tvCouncilDisplayCT = findViewById(R.id.tvCouncilDisplayCT);
        tvTrucksDisplayCT = findViewById(R.id.tvTrucksDisplayCT);

        Intent intentFromTracking = getIntent();
        councilName = intentFromTracking.getStringExtra("council");
        tvCouncilDisplayCT.setText(councilName);

        switch(councilName) {
            case ("Burwood"):
                tvTrucksDisplayCT.setText("BU128, BU129");
                break;
            case ("Hunters Hill"):
                tvTrucksDisplayCT.setText("HH100, HH120");
                break;
            case ("Hornsby"):
                tvTrucksDisplayCT.setText("HN130, HN131");
                break;
            case ("Ku-ring-gai"):
                tvTrucksDisplayCT.setText("KU126, KU127");
                break;
            case ("Mosman"):
                tvTrucksDisplayCT.setText("MO112, MO113");
                break;
            case ("Northern Beaches"):
                tvTrucksDisplayCT.setText("NB130, NB131");
                break;
            case ("Parramatta"):
                tvTrucksDisplayCT.setText("PA122, PA133");
                break;
            case ("Randwick"):
                tvTrucksDisplayCT.setText("RW110, RW120");
                break;
            case ("Strathfield"):
                tvTrucksDisplayCT.setText("SF111, SF112");
                break;
            case ("City of Sydney"):
                tvTrucksDisplayCT.setText("SY124, SY125");
                break;
            default:
        }

        // setup map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mvCouncilTracking = findViewById(R.id.mvCouncilTracking);
        mvCouncilTracking.onCreate(mapViewBundle);
        mvCouncilTracking.getMapAsync(this);

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
                    Intent intent = new Intent(CouncilTracking.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(CouncilTracking.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(CouncilTracking.this, ProfileActivity.class);
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
    // Map functioning methods

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mvCouncilTracking.onSaveInstanceState(mapViewBundle);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        LatLng sydney = new LatLng(-33.865143, 151.009900);
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
    }
    @Override
    protected void onResume() {
        super.onResume();
        mvCouncilTracking.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mvCouncilTracking.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mvCouncilTracking.onStop();
    }

    @Override
    protected void onPause() {
        mvCouncilTracking.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mvCouncilTracking.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mvCouncilTracking.onLowMemory();
    }
}