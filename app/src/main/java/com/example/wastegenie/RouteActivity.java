package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteActivity extends AppCompatActivity {

    NavigationView navigationView;

    DatabaseReference database;
    ArrayList<String> addressList = new ArrayList<String>();
    String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        setTitle("Route Tracking Page");


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .client(client)
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
                        String address = binData.getBinAddress();
                        addressList.add(address);
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
}