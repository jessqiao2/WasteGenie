package com.example.wastegenie.Analysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastegenie.DataModels.BinData;
import com.example.wastegenie.DataModels.DropOffData;
import com.example.wastegenie.GeocodingService;
import com.example.wastegenie.MainActivity;
import com.example.wastegenie.ProfileActivity;
import com.example.wastegenie.R;
import com.example.wastegenie.RouteService;
import com.example.wastegenie.TrackingActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DisposalActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String INTENT_MESSAGE = "intent message";
    NavigationView navigationView;
    TextView tvDisposalName, tvCouncilName, tvDisposalAddress, tvDisposalDistance;
    String councilName, disposalAddress, disposalDistance;
    TextView tvConsumerElec, tvCompTele, tvSmallApp, tvLighting, tvOther;
    String consumerElec, compTele, smallApp, lighting, other;
    DatabaseReference database;
    GoogleMap map;
    private MapView mvDisposals;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    String key;


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

        // connect to database
        // connecting firebase data
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet2");
        database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        DropOffData dropOffData = dataSnapshot.getValue(DropOffData.class);

                        if (dropOffData.getDropOffName().contains(message)) {
                            councilName = dropOffData.getCouncilName();
                            disposalAddress = dropOffData.getLocation();
                            disposalDistance = dropOffData.getDistanceKm().toString();

                            consumerElec = dropOffData.getConsumerElectronics();
                            compTele = dropOffData.getComputerTelecommunication();
                            smallApp = dropOffData.getSmallAppliances();
                            lighting = dropOffData.getLightingDevices();
                            other = dropOffData.getOther();
                        }

                    }

                    tvCouncilName.setText(councilName);
                    tvDisposalAddress.setText(disposalAddress);
                    tvDisposalDistance.setText(disposalDistance + " Km");

                    tvConsumerElec.setText(consumerElec);
                    tvCompTele.setText(compTele);
                    tvSmallApp.setText(smallApp);
                    tvLighting.setText(lighting);
                    tvOther.setText(other);

                }
            }
        });


        // setup map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mvDisposals = findViewById(R.id.mvDisposals);
        mvDisposals.onCreate(mapViewBundle);
        mvDisposals.getMapAsync(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RouteService routeService = retrofit.create(RouteService.class);

        try {
            key = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.get("com.google.android.geo.API_KEY").toString();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }


        // Get all stops taken by a particular truck
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet2");
        // will change hardcoded value later
        database.orderByChild("DropOffName").equalTo(INTENT_MESSAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gson gson = new Gson();
                DropOffData dropOffData = null;

                if(snapshot.getValue().getClass() == HashMap.class){
                    HashMap<String, HashMap<String, String>> dropOffDataHM = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    for(Map.Entry<String, HashMap<String, String>> entry : dropOffDataHM.entrySet()) {
                        HashMap hash = entry.getValue();
                        if (hash != null) {
                            JsonElement jsonElement = gson.toJsonTree(hash);
                            dropOffData = gson.fromJson(jsonElement, DropOffData.class);
                        }
                    }
                } else {
                    ArrayList<HashMap<String, String>> dropOffDataHMList = (ArrayList<HashMap<String, String>>) snapshot.getValue();
                    for (HashMap<String, String> hash : dropOffDataHMList) {
                        if (hash != null) {
                            JsonElement jsonElement = gson.toJsonTree(hash);
                            dropOffData = gson.fromJson(jsonElement, DropOffData.class);
                        }
                    }
                }

                String origin = null;
                switch(dropOffData.getCouncilName()){
                    case "Parramatta Council":
                        origin = "126 Church Street Parramatta New South Wales 2150";
                        break;
                    case "City of Sydney Council":
                        origin = "456 Kent Street Sydney New South Wales 2000";
                        break;
                    case "Ku-ring-gai Council":
                        origin = "818 Pacific Highway, Gordon NSW 2072";
                        break;
                    case "Burwood Council":
                        origin = "2 Conder Street BURWOOD NSW 2134";
                        break;
                    case "Hornsby Council":
                        origin = "296 Peats Ferry Rd, Hornsby NSW 2077";
                        break;
                    case "Strathfield Council":
                        origin = "65 Homebush Road, Strathfield NSW 2135";
                        break;
                    case "Northern Beaches Council":
                        origin = "1 Belgrave Street, Manly NSW 2095 Australia";
                        break;
                    case "Randwick Council":
                        origin = "30 Frances St, Randwick NSW 2031, Australia";
                        break;
                    case "Mosman Council":
                        origin = "573 Military Rd, Mosman NSW 2088";
                        break;
                    case "Hunters Hill Council":
                        origin = "22 Alexandra St, Hunters Hill NSW 2110, Australia";
                        break;
                }
                String destination = dropOffData.getLocation();

                Call<ResponseBody> routingCall = routeService.getSimpleRoute(origin, destination, key);
                DropOffData finalDropOffData = dropOffData;
                routingCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        JSONObject rawResponse = null;
                        try {
                            rawResponse = new JSONObject(response.body().string());
                            String polyline = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                            List<LatLng> decodedLine = PolyUtil.decode(polyline);
                            map.addPolyline(new PolylineOptions().addAll(decodedLine));

                            List<Double> lats = new ArrayList<>();
                            List<Double> lngs = new ArrayList<>();
                            for(LatLng ll : decodedLine){
                                lats.add(ll.latitude);
                                lngs.add(ll.longitude);
                            }

                            LatLngBounds routeBounds = new LatLngBounds(
                                    new LatLng(Collections.min(lats), Collections.min(lngs)),
                                    new LatLng(Collections.max(lats), Collections.max(lngs))
                            );
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(routeBounds, 100));

                            JSONObject startLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location");
                            LatLng councilLL = new LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng"));
                            JSONObject endLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location");
                            LatLng dropOffLL = new LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng"));

                            map.addMarker(new MarkerOptions()
                                        .position(dropOffLL)
                                        .title(finalDropOffData.getDropOffName())
                                        .snippet(finalDropOffData.getLocation()));
                            map.addMarker(new MarkerOptions()
                                    .position(councilLL)
                                    .title(finalDropOffData.getCouncilName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mvDisposals.onSaveInstanceState(mapViewBundle);
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
        mvDisposals.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mvDisposals.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mvDisposals.onStop();
    }

    @Override
    protected void onPause() {
        mvDisposals.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mvDisposals.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mvDisposals.onLowMemory();
    }
}