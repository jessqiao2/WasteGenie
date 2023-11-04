package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastegenie.Adapters.RouteAdapter;
import com.example.wastegenie.Analysis.AnalysisActivity;
import com.example.wastegenie.DataModels.BinData;
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

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    NavigationView navigationView;

    DatabaseReference database;
    ArrayList<String> addressList = new ArrayList<String>();
    String key = null;
    private MapView mvRoute;
    GoogleMap map;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    String truckID = null;
    TextView tvTruckIDDisplay;
    TextView tvCouncilDisplay;
    RouteAdapter adapter;
    ArrayList<BinData> list;
    RecyclerView recyclerView;
    CheckBox cbContamination;
    CheckBox cbRecycle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle("Route Tracking Page");
        tvTruckIDDisplay = findViewById(R.id.tvCouncilDisplayCT);
        tvCouncilDisplay = findViewById(R.id.tvTrucksDisplayCT);

        recyclerView = findViewById(R.id.rvRouteSpecificTruck);
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new RouteAdapter(this, list);
        recyclerView.setAdapter(adapter);

        Intent intentFromTracking = getIntent();
        truckID = intentFromTracking.getStringExtra("truckID");
        tvTruckIDDisplay.setText(truckID);

        switch(truckID.substring(0, 2)){
            case("BU"):
                tvCouncilDisplay.setText("Burwood");
                break;
            case("HH"):
                tvCouncilDisplay.setText("Hunters Hill");
                break;
            case("HN"):
                tvCouncilDisplay.setText("Hornsby");
                break;
            case("KU"):
                tvCouncilDisplay.setText("Ku-ring-gai");
                break;
            case("MO"):
                tvCouncilDisplay.setText("Mosman");
                break;
            case("NB"):
                tvCouncilDisplay.setText("Northern Beaches");
                break;
            case("PA"):
                tvCouncilDisplay.setText("Parramatta");
                break;
            case("RW"):
                tvCouncilDisplay.setText("Randwick");
                break;
            case("SF"):
                tvCouncilDisplay.setText("Strathfield");
                break;
            case("SY"):
                tvCouncilDisplay.setText("City of Sydney");
                break;
            default:

        }

        database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    LocalDate today = LocalDate.of(2023, 10, 24);
                    LocalDate lastWeek = today.minus(1, ChronoUnit.WEEKS);
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);
                        LocalDate addressDate = LocalDate.parse(binData.getDate().split("T")[0]);

                        if(addressDate.isBefore(today) && addressDate.isAfter(lastWeek)) {
                            list.add(binData);
                        }


                    }

                    adapter.notifyDataSetChanged();
                adapter.getFilter().filter(truckID);
                }
            }
        });


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mvRoute = findViewById(R.id.mvRoute);
        mvRoute.onCreate(mapViewBundle);
        mvRoute.getMapAsync(this);

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
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        // will change hardcoded value later
        database.orderByChild("truckId").equalTo(truckID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gson gson = new Gson();
                ArrayList<BinData> binDataList = new ArrayList<BinData>();
                ArrayList<BinData> routeBinList = new ArrayList<BinData>();


                if(snapshot.getValue().getClass() == HashMap.class){
                    Log.d("RouteActivity", "HashMap");
                    HashMap<String, HashMap<String, String>> binDataHM = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    for(Map.Entry<String, HashMap<String, String>> entry : binDataHM.entrySet()) {
                        HashMap hash = entry.getValue();
                        if (hash != null) {
                            JsonElement jsonElement = gson.toJsonTree(hash);
                            BinData binData = gson.fromJson(jsonElement, BinData.class);
                            binDataList.add(binData);
                        }
                    }
                } else {
                    ArrayList<HashMap<String, String>> binDataHMList = (ArrayList<HashMap<String, String>>) snapshot.getValue();
                    Log.d("snapshot", "works correctly");
                    for (HashMap<String, String> hash : binDataHMList) {
                        if (hash != null) {
                            JsonElement jsonElement = gson.toJsonTree(hash);
                            BinData binData = gson.fromJson(jsonElement, BinData.class);
                            binDataList.add(binData);
                        }
                    }
                }

                binDataList.sort(Comparator.comparing(BinData::getDate));
                String mostRecentDate = binDataList.get(binDataList.size() - 1).getDate().split("T")[0];
                for (BinData binData : binDataList){
                    String collectionDate = binData.getDate().split("T")[0];
                    if ((binData.getStatus().contains("Bin Fully Recyclable") || binData.getStatus().contains("Bin Flagged as Contaminated")) && collectionDate.equals(mostRecentDate)) {
                        String address = binData.getBinAddress();
                        addressList.add(address);
                        routeBinList.add(binData);
                    }
                }

                String origin = addressList.get(0);
                String destination = addressList.get(addressList.size() - 1);
                String waypoints = null;
                if (addressList.size() > 2) {
                    waypoints = String.join(" | ", addressList.subList(1, addressList.size() - 1));
                }

                Call<ResponseBody> routingCall = routeService.getRoute(origin, destination, waypoints, key);
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

                            // Format the date-time with AM/PM indicator
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

                            for (int i = 0; i < addressList.size() - 1; i++){
                                JSONObject startLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(i).getJSONObject("start_location");
                                LatLng waypointLL = new LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng"));
                                BinData thisBinData = routeBinList.get(i);
                                // change date from ISO 8601 format to standard year and time format
                                String timeStampFromDatabase = thisBinData.getDateTime();
                                Instant instant = Instant.parse(timeStampFromDatabase);

                                // Convert it to ZonedDateTime using the system's default time zone
                                ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                                String formattedTime = zonedDateTime.format(formatter);

                                // Change marker colour based on contamination

                                float hue = 0;
                                if(thisBinData.getStatus().contains("Bin Flagged as Contaminated")){
                                    hue = BitmapDescriptorFactory.HUE_RED;
                                } else if(thisBinData.getStatus().contains("Bin Fully Recyclable")){
                                    hue = BitmapDescriptorFactory.HUE_GREEN;
                                }
                                map.addMarker(new MarkerOptions()
                                        .position(waypointLL)
                                        .title(thisBinData.getBinName())
                                        .snippet("Collected by Truck " + thisBinData.getTruckId() + " at " + formattedTime)
                                        .icon(BitmapDescriptorFactory.defaultMarker()));
                            }
                            JSONObject endLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(addressList.size() - 2).getJSONObject("end_location");
                            LatLng endpointLL = new LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng"));
                            BinData lastBinData = routeBinList.get(routeBinList.size() - 1);

                            // change date from ISO 8601 format to standard year and time format
                            String timeStampFromDatabase = lastBinData.getDateTime();
                            Instant instant = Instant.parse(timeStampFromDatabase);

                            // Convert it to ZonedDateTime using the system's default time zone
                            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                            float hue = 0;
                            if(lastBinData.getStatus().contains("Bin Flagged as Contaminated")){
                                hue = BitmapDescriptorFactory.HUE_RED;
                            } else if(lastBinData.getStatus().contains("Bin Fully Recyclable")){
                                hue = BitmapDescriptorFactory.HUE_GREEN;
                            }
                            // Format the date-time with AM/PM indicator
                            String formattedTime = zonedDateTime.format(formatter);
                            map.addMarker(new MarkerOptions()
                                    .position(endpointLL)
                                    .title(lastBinData.getBinName())
                                    .snippet("Truck " + lastBinData.getTruckId() + " at " + formattedTime)
                                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));

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


        // Checkbox for contamination
        cbContamination = findViewById(R.id.cbRouteContaminated);
        cbContamination.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbRecycle.isChecked() == false) {
                    adapter.getContaminationFilter().filter("Bin Flagged as Contaminated");

                } else {
                    // if the checkbox is unchecked, return to the original truck filter.
                    adapter.getFilter().filter(truckID);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Checkbox for fully recycled
        cbRecycle = findViewById(R.id.cbRouteRecycle);
        cbRecycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbContamination.isChecked() == false) {
                    adapter.getContaminationFilter().filter("Bin Fully Recyclable ");
                } else {
                    // if the checkbox is unchecked, return to the original truck filter.
                    adapter.getFilter().filter(truckID);
                    adapter.notifyDataSetChanged();
                }
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
                    Intent intent = new Intent(RouteActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(RouteActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(RouteActivity.this, ProfileActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                Intent intent = new Intent(RouteActivity.this, TrackingActivity.class);
                startActivity(intent);
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

        mvRoute.onSaveInstanceState(mapViewBundle);
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
        mvRoute.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mvRoute.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mvRoute.onStop();
    }

    @Override
    protected void onPause() {
        mvRoute.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mvRoute.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mvRoute.onLowMemory();
    }
}