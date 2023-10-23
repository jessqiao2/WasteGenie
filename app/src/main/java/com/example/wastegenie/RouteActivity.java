package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle("Route Tracking Page");

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
        database.orderByChild("truckId").equalTo("PA122").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, String>> binDataList = (ArrayList<HashMap<String, String>>) snapshot.getValue();
                Log.d("snapshot", "works correctly");

                Gson gson = new Gson();
                for (HashMap<String, String> hash : binDataList) {
                    if (hash != null) {
                        JsonElement jsonElement = gson.toJsonTree(hash);
                        BinData binData = gson.fromJson(jsonElement, BinData.class);
                        String collectionDate = binData.getDate().split("T")[0];
                        if (binData.getStatus().equals("Bin Picked Up") && collectionDate.equals("2023-09-30")) {
                            String address = binData.getBinAddress();
                            addressList.add(address);
                        }
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

                            // double avgLat = (lats.get(0) + lats.get(decodedLine.size() - 1))/2;
                            // double avgLng = (lngs.get(0) + lngs.get(decodedLine.size() - 1))/2;
                            LatLngBounds routeBounds = new LatLngBounds(
                                    new LatLng(Collections.min(lats), Collections.min(lngs)),
                                            new LatLng(Collections.max(lats), Collections.max(lngs))
                            );
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(routeBounds, 100));
                            // map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(avgLat, avgLng), 13));


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

        /* For creating the route:
        https://developers.google.com/maps/documentation/directions/get-directions#DirectionsResponse
        https://www.geeksforgeeks.org/how-to-draw-polyline-in-google-maps-in-android/
         */



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