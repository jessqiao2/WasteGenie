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

import com.example.wastegenie.Adapters.CouncilTrackingAdapter;
import com.example.wastegenie.Adapters.RouteAdapter;
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

public class CouncilTracking extends AppCompatActivity implements OnMapReadyCallback {
    String councilName = null;
    TextView tvTrucksDisplayCT;
    TextView tvCouncilDisplayCT;
    NavigationView navigationView;
    GoogleMap map;
    private MapView mvCouncilTracking;

    DatabaseReference database;
    ArrayList<String> addressList1 = new ArrayList<String>();
    ArrayList<String> addressList2 = new ArrayList<String>();
    String key;
    List<Double> lats = new ArrayList<>();
    List<Double> lngs = new ArrayList<>();
    Boolean isFinished1 = false;
    Boolean isFinished2 = false;
    RecyclerView recyclerView;
    CheckBox cbContamination;
    CheckBox cbRecycle;
    CouncilTrackingAdapter adapter;
    ArrayList<BinData> list;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_council_tracking);
        setTitle("Council Routes Page");

        tvCouncilDisplayCT = findViewById(R.id.tvCouncilDisplayCT);
        tvTrucksDisplayCT = findViewById(R.id.tvTrucksDisplayCT);

        recyclerView = findViewById(R.id.rvCTCouncilTracking);
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new CouncilTrackingAdapter(this, list);
        recyclerView.setAdapter(adapter);

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

        database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    LocalDate today = LocalDate.of(2023, 10, 30);
                    LocalDate lastWeek = today.minus(1, ChronoUnit.WEEKS);
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);
                        LocalDate addressDate = LocalDate.parse(binData.getDate().split("T")[0]);
                        String a = councilName + " Council";
                        Boolean b = a.equals(binData.getCouncilName());
                        if(addressDate.isBefore(today) && addressDate.isAfter(lastWeek)) {
                            list.add(binData);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    adapter.getFilter().filter(councilName);
                }
            }
        });

        // setup map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mvCouncilTracking = findViewById(R.id.mvCouncilTracking);
        mvCouncilTracking.onCreate(mapViewBundle);
        mvCouncilTracking.getMapAsync(this);

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

        // get from Firebase
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        // will change hardcoded value later
        database.orderByChild("councilName").equalTo(councilName + " Council").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Gson gson = new Gson();
                ArrayList<BinData> binDataList = new ArrayList<BinData>();
                ArrayList<BinData> routeBinList1 = new ArrayList<BinData>();
                ArrayList<BinData> routeBinList2 = new ArrayList<BinData>();


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

                String truckID1 = binDataList.get(0).getTruckId();
                String truckID2;

                binDataList.sort(Comparator.comparing(BinData::getDate));
                String mostRecentDate = binDataList.get(binDataList.size() - 1).getDate().split("T")[0];
                String mostRecentDate2 = binDataList.get(binDataList.size() - 11).getDate().split("T")[0];
                for (BinData binData : binDataList){
                    String collectionDate = binData.getDate().split("T")[0];
                    if (binData.getStatus().equals("Bin Picked Up") && (collectionDate.equals(mostRecentDate) || collectionDate.equals(mostRecentDate2))) {
                        String address = binData.getBinAddress();

                        // separate bins by truck
                        if(!binData.getTruckId().equals(truckID1)){
                            truckID2 = binData.getTruckId();
                            routeBinList2.add(binData);
                            addressList2.add(address);
                        } else {
                            routeBinList1.add(binData);
                            addressList1.add(address);
                        }
                    }
                }


                // Truck 1
                String origin = addressList1.get(0);
                String destination = addressList1.get(addressList1.size() - 1);
                String waypoints = null;
                if (addressList1.size() > 2) {
                    waypoints = String.join(" | ", addressList1.subList(1, addressList1.size() - 1));
                }

                Call<ResponseBody> routingCall1 = routeService.getRoute(origin, destination, waypoints, key);
                routingCall1.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        JSONObject rawResponse = null;
                        try {
                            rawResponse = new JSONObject(response.body().string());
                            String polyline = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                            List<LatLng> decodedLine = PolyUtil.decode(polyline);
                            map.addPolyline(new PolylineOptions().addAll(decodedLine));

                            for(LatLng ll : decodedLine){
                                lats.add(ll.latitude);
                                lngs.add(ll.longitude);
                            }

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

                            for (int i = 0; i < addressList1.size() - 1; i++){
                                JSONObject startLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(i).getJSONObject("start_location");
                                LatLng waypointLL = new LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng"));
                                BinData thisBinData = routeBinList1.get(i);
                                // change date from ISO 8601 format to standard year and time format
                                String timeStampFromDatabase = thisBinData.getDateTime();
                                Instant instant = Instant.parse(timeStampFromDatabase);

                                // Convert it to ZonedDateTime using the system's default time zone
                                ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                                // Format the date-time with AM/PM indicator
                                String formattedTime = zonedDateTime.format(formatter);

                                map.addMarker(new MarkerOptions()
                                        .position(waypointLL)
                                        .title(thisBinData.getBinName())
                                        .snippet("Collected by Truck " + thisBinData.getTruckId() + " at " + formattedTime));
                            }
                            JSONObject endLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(addressList1.size() - 2).getJSONObject("end_location");
                            LatLng endpointLL = new LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng"));
                            BinData lastBinData = routeBinList1.get(routeBinList1.size() - 1);

                            // change date from ISO 8601 format to standard year and time format
                            String timeStampFromDatabase = lastBinData.getDateTime();
                            Instant instant = Instant.parse(timeStampFromDatabase);

                            // Convert it to ZonedDateTime using the system's default time zone
                            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                            // Format the date-time with AM/PM indicator
                            String formattedTime = zonedDateTime.format(formatter);
                            map.addMarker(new MarkerOptions()
                                    .position(endpointLL)
                                    .title(routeBinList1.get(routeBinList1.size() - 1).getBinName())
                                    .snippet("Truck " + lastBinData.getTruckId() + " at " + formattedTime)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            isFinished1 = true;
                            adjustBounds();

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


                // Truck 2
                String origin2 = addressList2.get(0);
                String destination2 = addressList2.get(addressList2.size() - 1);
                String waypoints2 = null;
                if (addressList2.size() > 2) {
                    waypoints2 = String.join(" | ", addressList2.subList(1, addressList2.size() - 1));
                }

                Call<ResponseBody> routingCall2 = routeService.getRoute(origin2, destination2, waypoints2, key);
                routingCall2.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        JSONObject rawResponse = null;
                        try {
                            rawResponse = new JSONObject(response.body().string());
                            String polyline = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                            List<LatLng> decodedLine = PolyUtil.decode(polyline);
                            map.addPolyline(new PolylineOptions().addAll(decodedLine));

                            for(LatLng ll : decodedLine){
                                lats.add(ll.latitude);
                                lngs.add(ll.longitude);
                            }

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

                            for (int i = 0; i < addressList1.size() - 1; i++){
                                JSONObject startLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(i).getJSONObject("start_location");
                                LatLng waypointLL = new LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng"));
                                BinData thisBinData = routeBinList2.get(i);

                                // change date from ISO 8601 format to standard year and time format
                                String timeStampFromDatabase = thisBinData.getDateTime();
                                Instant instant = Instant.parse(timeStampFromDatabase);

                                // Convert it to ZonedDateTime using the system's default time zone
                                ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                                // Format the date-time with AM/PM indicator
                                String formattedTime = zonedDateTime.format(formatter);
                                map.addMarker(new MarkerOptions()
                                        .position(waypointLL)
                                        .title(routeBinList2.get(i).getBinName())
                                        .snippet("Collected by Truck " + thisBinData.getTruckId() + " at " + formattedTime));
                            }
                            JSONObject endLoc = rawResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(addressList1.size() - 2).getJSONObject("end_location");
                            LatLng endpointLL = new LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng"));
                            BinData lastBinData = routeBinList2.get(routeBinList2.size() - 1);
                            // change date from ISO 8601 format to standard year and time format
                            String timeStampFromDatabase = lastBinData.getDateTime();
                            Instant instant = Instant.parse(timeStampFromDatabase);

                            // Convert it to ZonedDateTime using the system's default time zone
                            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                            // Format the date-time with AM/PM indicator
                            String formattedTime = zonedDateTime.format(formatter);

                            map.addMarker(new MarkerOptions()
                                    .position(endpointLL)
                                    .title(routeBinList2.get(routeBinList2.size() - 1).getBinName())
                                    .snippet("Truck " + lastBinData.getTruckId() + " at " + formattedTime)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            isFinished2 = true;
                            adjustBounds();

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
        cbContamination = findViewById(R.id.cbCTContaminated);
        cbContamination.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbRecycle.isChecked() == false) {
                    adapter.getContaminationFilter().filter("Bin Flagged as Contaminated");

                } else {
                    // if the checkbox is unchecked, return to the original truck filter.
                    adapter.getFilter().filter(councilName);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Checkbox for fully recycled
        cbRecycle = findViewById(R.id.cbCTRecycle);
        cbRecycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbContamination.isChecked() == false) {
                    adapter.getContaminationFilter().filter("Bin Fully Recyclable ");
                } else {
                    // if the checkbox is unchecked, return to the original truck filter.
                    adapter.getFilter().filter(councilName);
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


    private synchronized void adjustBounds(){
        if(isFinished1 && isFinished2){
            LatLngBounds routeBounds = new LatLngBounds(
                    new LatLng(Collections.min(lats), Collections.min(lngs)),
                    new LatLng(Collections.max(lats), Collections.max(lngs))
            );
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(routeBounds, 100));
            isFinished1 = false;
            isFinished2 = false;

        }
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