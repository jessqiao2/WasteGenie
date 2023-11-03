package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wastegenie.Adapters.TopCouncilsAdapter;
import com.example.wastegenie.Analysis.AnalysisActivity;
import com.example.wastegenie.DataModels.BinData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    NavigationView navigationView;
    Button btViewTruckRoute;
    Button btViewCouncilRoute;
    Button btViewAnalysis;

    private MapView mvTracking;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    DatabaseReference database;
    ArrayList<String> addressList = new ArrayList<String>();
    String key = null;

    Spinner truckSpinner;
    Spinner councilSpinner;
    Spinner generalCouncilSpinner;

    GoogleMap map;
    RecyclerView recyclerView;
    TopCouncilsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        setTitle("Tracking Page");
        btViewTruckRoute = findViewById(R.id.btViewTruckRoute);
        btViewCouncilRoute = findViewById(R.id.btViewCouncilRoutes);
        btViewAnalysis = findViewById(R.id.btViewAnalysis);

        // Top Contamination Councils list
        recyclerView = findViewById(R.id.rvTopMonthlyContCouncils);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<CouncilMCs> councilData = new ArrayList<>();
        councilData.add(new CouncilMCs("Parramatta", 32));
        councilData.add(new CouncilMCs("City of Sydney", 36));
        councilData.add(new CouncilMCs("Ku-ring-gai", 32));
        councilData.add(new CouncilMCs("Burwood", 23));
        councilData.add(new CouncilMCs("Hornsby", 25));
        councilData.add(new CouncilMCs("Strathfield", 27));
        councilData.add(new CouncilMCs("Northern Beaches", 19));
        councilData.add(new CouncilMCs("Randwick", 15));
        councilData.add(new CouncilMCs("Mosman", 17));
        councilData.add(new CouncilMCs("Hunters Hill", 35));
        adapter = new TopCouncilsAdapter(councilData);
        recyclerView.setAdapter(adapter);
        adapter.sortList();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mvTracking = findViewById(R.id.mvRoute);
        mvTracking.onCreate(mapViewBundle);
        mvTracking.getMapAsync(this);

        // Choose Council -> Truck (Flow 1) - TBD
        truckSpinner = findViewById(R.id.spTrackActivityTruck);


        councilSpinner = findViewById(R.id.spTrackActivityCouncilofTruck);
        ArrayAdapter<CharSequence> councilAdapter = ArrayAdapter.createFromResource(
                TrackingActivity.this,
                R.array.councilArray,
                android.R.layout.simple_spinner_item
        );
        councilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        councilSpinner.setAdapter(councilAdapter);


        councilSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if user selects parramatta council
                if (councilSpinner.getSelectedItem().equals("Parramatta")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.parramattaTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);

                    // if user selects sydney council
                } else if (councilSpinner.getSelectedItem().equals("City of Sydney")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.sydneyTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Ku-ring-gai")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.kuRingGaiTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Burwood")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.burwoodTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Hornsby")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.hornsbyTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Strathfield")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.strathfieldTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Northern Beaches")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.northernBeachesTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Randwick")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.randwickTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Mosman")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.mosmanTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Hunters Hill")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            TrackingActivity.this,
                            R.array.huntersHillTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        generalCouncilSpinner = findViewById(R.id.spTrackActivityCouncil);
        ArrayAdapter<CharSequence> generalCouncilAdapter = ArrayAdapter.createFromResource(
                TrackingActivity.this,
                R.array.councilArray,
                android.R.layout.simple_spinner_item
        );
        generalCouncilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generalCouncilSpinner.setAdapter(generalCouncilAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeocodingService geocodingService = retrofit.create(GeocodingService.class);

        try {
            key = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.get("com.google.android.geo.API_KEY").toString();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        database.orderByChild("status").equalTo("Bin Flagged as Contaminated").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BinData bindata = snapshot.getValue(BinData.class);
                String address = bindata.getBinAddress();
                addressList.add(address);

                LocalDate today = LocalDate.of(2023, 10, 24);
                LocalDate lastWeek = today.minus(1, ChronoUnit.WEEKS);
                LocalDate addressDate = LocalDate.parse(bindata.getDate().split("T")[0]);

                if(addressDate.isBefore(today) && addressDate.isAfter(lastWeek)) {
                    Call<ResponseBody> geocodingCall = geocodingService.getLongLat(address, key);
                    geocodingCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            JSONObject rawResponse = null;
                            try {
                                rawResponse = new JSONObject(response.body().string());
                                JSONObject longLat = rawResponse.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                                LatLng binLoc = new LatLng(longLat.getDouble("lat"), longLat.getDouble("lng"));
                                map.addMarker(new MarkerOptions()
                                        .position(binLoc)
                                        .title(bindata.getBinName()));

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });





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
                    Intent intent = new Intent(TrackingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(TrackingActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(TrackingActivity.this, ProfileActivity.class);
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

        btViewTruckRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrackingActivity.this, RouteActivity.class);
                intent.putExtra("truckID", truckSpinner.getSelectedItem().toString());
                startActivity(intent);
                finish();
            }
        });

        btViewCouncilRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrackingActivity.this, CouncilTracking.class);
                intent.putExtra("council", generalCouncilSpinner.getSelectedItem().toString());
                startActivity(intent);
                finish();
            }
        });

        btViewAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrackingActivity.this, AnalysisActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mvTracking.onSaveInstanceState(mapViewBundle);
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
        mvTracking.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mvTracking.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mvTracking.onStop();
    }

    @Override
    protected void onPause() {
        mvTracking.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mvTracking.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mvTracking.onLowMemory();
    }

}