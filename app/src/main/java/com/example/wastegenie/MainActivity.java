package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    NavigationView navigationView;

    RecyclerView recyclerView;
    DatabaseReference database;
    HomeAdapter adapter;
    ArrayList<BinData> list;

    Spinner councilSpinner;
    Spinner truckSpinner;
    Button btSearchTracking;
    CheckBox cbContamination;
    CheckBox cbRecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home Page");

        /**
         * Set up the recyclerview for tracking on the home page
         */
        recyclerView = findViewById(R.id.rvTracking);
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new HomeAdapter(this, list);
        recyclerView.setAdapter(adapter);

        database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);
                        list.add(binData);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        /**
         * Setting up the spinner for councils
         */
        councilSpinner = findViewById(R.id.spHomeTrackCouncil);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> councilAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.councilArray,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        councilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        councilSpinner.setAdapter(councilAdapter);

        /**
         * Setting up corresponding spinners for the council's SPECIFIC trucks depending on what
         * council the user chose
         */
        truckSpinner = findViewById(R.id.spHomeTrackTruckID);
        councilSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if user selects parramatta council
                if (councilSpinner.getSelectedItem().equals("Parramatta")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            MainActivity.this,
                            R.array.parramattaTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);

                    // if user selects sydney council
                } else if (councilSpinner.getSelectedItem().equals("City of Sydney")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            MainActivity.this,
                            R.array.sydneyTrucksArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    truckSpinner.setAdapter(truckAdapter);
                } else if (councilSpinner.getSelectedItem().equals("Ku-ring-gai")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            MainActivity.this,
                            R.array.kuRingGaiTrucksArray,
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

        truckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (truckSpinner.getSelectedItem().equals("")) {
                    Toast.makeText(MainActivity.this, "Please select a truck to view tracking", Toast.LENGTH_LONG).show();
                } else {
                    adapter.getFilter().filter(truckSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Checkbox for contamination
        cbContamination = findViewById(R.id.cbHomeContaminated);
        cbContamination.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbRecycle.isChecked() == false) {
                    adapter.getContaminationFilter().filter("Bin Flagged as Contaminated");

//                    // if both checkboxes are checked, return to normal
//                    if (cbRecycle.isChecked() && cbContamination.isChecked()) {
//                        adapter.getFilter().filter(truckSpinner.getSelectedItem().toString());
//                    } else {
//                        adapter.getContaminationFilter().filter("Bin Flagged as Contaminated");
//                    }

                } else {
                    // if the checkbox is unchecked, return to the original truck filter.
                    adapter.getFilter().filter(truckSpinner.getSelectedItem().toString());
                }
            }
        });

        // Checkbox for fully recycled
        cbRecycle = findViewById(R.id.cbHomeRecycle);
        cbRecycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && cbContamination.isChecked() == false) {
                    adapter.getContaminationFilter().filter("Bin Fully Recyclable ");
                } else {
                    // if the checkbox is unchecked, return to the original truck filter.
                    adapter.getFilter().filter(truckSpinner.getSelectedItem().toString());
                }
            }
        });







        /**
         * For navigation view, if we want to change this to  navigation toggle, use this guide:
         * https://www.youtube.com/watch?v=KO-wBv7Phwo&ab_channel=KGINFO
         */

        navigationView = findViewById(R.id.nav_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.home) {
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(MainActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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