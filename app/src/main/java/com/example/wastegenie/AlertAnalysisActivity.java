package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastegenie.Adapters.BinAnalysisAdapter;
import com.example.wastegenie.Adapters.ResourceAdapter;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AlertAnalysisActivity extends AppCompatActivity implements RecyclerViewResourceClickListener{
    public static final String INTENT_MESSAGE = "message";
    NavigationView navigationView;
    TextView tvCouncilName, tvCouncilAddress;
    DatabaseReference database;
    String councilLocation;

    TextView tvBinName1, tvBinName2, tvBinName3, tvBinName4, tvBinName5, tvBinName6, tvBinName7, tvBinName8, tvBinName9, tvBinName10;
    TextView tvBinLocation1, tvBinLocation2, tvBinLocation3, tvBinLocation4, tvBinLocation5, tvBinLocation6, tvBinLocation7, tvBinLocation8, tvBinLocation9, tvBinLocation10;
    TextView tvBinCount1, tvBinCount2, tvBinCount3, tvBinCount4, tvBinCount5, tvBinCount6, tvBinCount7, tvBinCount8, tvBinCount9, tvBinCount10;

    TextView tvWeekly, tvMonthly;
    RecyclerView recyclerView;
    DatabaseReference databaseRecycler;
    BinAnalysisAdapter adapter;
    ArrayList<DropOffData> list;

    int contaminatedBin1;
    int contaminatedBin2;
    int contaminatedBin3;
    int contaminatedBin4;
    int contaminatedBin5;
    int contaminatedBin6;
    int contaminatedBin7;
    int contaminatedBin8;
    int contaminatedBin9;
    int contaminatedBin10;
    int contaminatedMonthBin1;
    int contaminatedMonthBin2;
    int contaminatedMonthBin3;
    int contaminatedMonthBin4;
    int contaminatedMonthBin5;
    int contaminatedMonthBin6;
    int contaminatedMonthBin7;
    int contaminatedMonthBin8;
    int contaminatedMonthBin9;
    int contaminatedMonthBin10;

    int totalWeightMon;
    int totalWeightTues;
    int totalWeightWed;
    int totalWeightThurs;
    int totalWeightFri;
    int totalWeightSat;
    int totalWeightSun;
    int totalWeightWeekOne;
    int totalWeightWeekTwo;
    int totalWeightWeekThree;
    int totalWeightWeekFour;
    int recycableCountWeekOne;
    int recycableCountWeekTwo;
    int recycableCountWeekThree;
    int recycableCountWeekFour;
    int contaminatedCountWeekOne;
    int contaminatedCountWeekTwo;
    int contaminatedCountWeekThree;
    int contaminatedCountWeekFour;
    int recycableCountMon;
    int recycableCountTues;
    int recycableCountWed;
    int recycableCountThurs;
    int recycableCountFri;
    int recycableCountSat;
    int recycableCountSun;
    int contaminatedCountMon;
    int contaminatedCountTues;
    int contaminatedCountWed;
    int contaminatedCountThurs;
    int contaminatedCountFri;
    int contaminatedCountSat;
    int contaminatedCountSun;
    DatabaseReference databaseChart;

    /**
     * Guides
     */
    RecyclerView rvGuides;
    private RecyclerView.LayoutManager layoutManager;
    private ResourceAdapter resourceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_analysis);

        /**
         * Set up council details
         */
        Intent intent = getIntent();
        String councilChoice = intent.getStringExtra("Analysis Council");

        tvCouncilName = findViewById(R.id.tvAlertCouncilName);
        tvCouncilAddress = findViewById(R.id.tvAlertCouncilAddress);

        tvBinLocation1 = findViewById(R.id.tvAlertLocation1);
        tvBinLocation2 = findViewById(R.id.tvAlertLocation2);
        tvBinLocation3 = findViewById(R.id.tvAlertLocation3);
        tvBinLocation4 = findViewById(R.id.tvAlertLocation4);
        tvBinLocation5 = findViewById(R.id.tvAlertLocation5);
        tvBinLocation6 = findViewById(R.id.tvAlertLocation6);
        tvBinLocation7 = findViewById(R.id.tvAlertLocation7);
        tvBinLocation8 = findViewById(R.id.tvAlertLocation8);
        tvBinLocation9 = findViewById(R.id.tvAlertLocation9);
        tvBinLocation10 = findViewById(R.id.tvAlertLocation10);

        tvBinName1 = findViewById(R.id.tvAlertBin1);
        tvBinName2 = findViewById(R.id.tvAlertBin2);
        tvBinName3 = findViewById(R.id.tvAlertBin3);
        tvBinName4 = findViewById(R.id.tvAlertBin4);
        tvBinName5 = findViewById(R.id.tvAlertBin5);
        tvBinName6 = findViewById(R.id.tvAlertBin6);
        tvBinName7 = findViewById(R.id.tvAlertBin7);
        tvBinName8 = findViewById(R.id.tvAlertBin8);
        tvBinName9 = findViewById(R.id.tvAlertBin9);
        tvBinName10 = findViewById(R.id.tvAlertBin10);

        tvBinCount1 = findViewById(R.id.tvAlertCount1);
        tvBinCount2 = findViewById(R.id.tvAlertCount2);
        tvBinCount3 = findViewById(R.id.tvAlertCount3);
        tvBinCount4 = findViewById(R.id.tvAlertCount4);
        tvBinCount5 = findViewById(R.id.tvAlertCount5);
        tvBinCount6 = findViewById(R.id.tvAlertCount6);
        tvBinCount7 = findViewById(R.id.tvAlertCount7);
        tvBinCount8 = findViewById(R.id.tvAlertCount8);
        tvBinCount9 = findViewById(R.id.tvAlertCount9);
        tvBinCount10 = findViewById(R.id.tvAlertCount10);

        tvCouncilName.setText(councilChoice);

        if (councilChoice.contains("Parramatta") || councilChoice.contains("Parramatta Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.parramattaBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.parramattaBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.parramattaBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.parramattaBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.parramattaBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.parramattaBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.parramattaBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.parramattaBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.parramattaBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.parramattaBinsArray)[9]);
        } else if (councilChoice.contains("City of Sydney") || councilChoice.contains("City of Sydney Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.sydneyBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.sydneyBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.sydneyBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.sydneyBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.sydneyBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.sydneyBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.sydneyBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.sydneyBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.sydneyBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.sydneyBinsArray)[9]);
        } else if (councilChoice.contains("Ku-ring-gai") || councilChoice.contains("Ku-ring-gai Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.kuRingGaiBinsArray)[9]);
        } else if (councilChoice.contains("Burwood") || councilChoice.contains("Burwood Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.burwoodBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.burwoodBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.burwoodBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.burwoodBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.burwoodBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.burwoodBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.burwoodBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.burwoodBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.burwoodBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.burwoodBinsArray)[9]);
        } else if (councilChoice.contains("Hornsby") || councilChoice.contains("Hornsby Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.hornsbyBinsArray)[9]);
        } else if (councilChoice.contains("Strathfield") || councilChoice.contains("Strathfield Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.strathfieldBinsArray)[9]);
        } else if (councilChoice.contains("Northern Beaches") || councilChoice.contains("Northern Beaches Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.northernBeachesBinsArray)[9]);
        } else if (councilChoice.contains("Randwick") || councilChoice.contains("Randwick Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.randwickBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.randwickBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.randwickBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.randwickBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.randwickBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.randwickBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.randwickBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.randwickBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.randwickBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.randwickBinsArray)[9]);
        } else if (councilChoice.contains("Mosman") || councilChoice.contains("Mosman Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.mosmanBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.mosmanBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.mosmanBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.mosmanBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.mosmanBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.mosmanBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.mosmanBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.mosmanBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.mosmanBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.mosmanBinsArray)[9]);
        } else if (councilChoice.contains("Hunters Hill") || councilChoice.contains("Hunters Hill Council")) {
            tvBinName1.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[0]);
            tvBinName2.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[1]);
            tvBinName3.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[2]);
            tvBinName4.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[3]);
            tvBinName5.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[4]);
            tvBinName6.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[5]);
            tvBinName7.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[6]);
            tvBinName8.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[7]);
            tvBinName9.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[8]);
            tvBinName10.setText(getResources().getStringArray(R.array.huntersHillBinsArray)[9]);
        }

        // connecting firebase data
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);

                        if (binData.getCouncilName().contains(councilChoice)) {
                            councilLocation = binData.getCouncilAddress();
                        }

                        // set the bin location according to the bin name
                        if (binData.getBinName().contains(tvBinName1.getText())) {
                            tvBinLocation1.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName2.getText())) {
                            tvBinLocation2.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName3.getText())) {
                            tvBinLocation3.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName4.getText())) {
                            tvBinLocation4.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName5.getText())) {
                            tvBinLocation5.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName6.getText())) {
                            tvBinLocation6.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName7.getText())) {
                            tvBinLocation7.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName8.getText())) {
                            tvBinLocation8.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName9.getText())) {
                            tvBinLocation9.setText(binData.getBinAddress());
                        } else if (binData.getBinName().contains(tvBinName10.getText())) {
                            tvBinLocation10.setText(binData.getBinAddress());
                        }

                    }

                    tvCouncilAddress.setText(councilLocation);
                }
            }
        });

        /**
         * Set up the recyclerview for resource screen
         */
        rvGuides = findViewById(R.id.rvAlertGuides);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        rvGuides.setLayoutManager(layoutManager);
        resourceAdapter = new ResourceAdapter(ResourceData.getResourceData(), this);
        rvGuides.setAdapter(resourceAdapter);


        /**
         * Set up the recyclerview
         */
        recyclerView = findViewById(R.id.rvAlertDropOff);
        databaseRecycler = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet2");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new BinAnalysisAdapter(this, list);

        recyclerView.setAdapter(adapter);

        databaseRecycler.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        DropOffData siteData = dataSnapshot.getValue(DropOffData.class);
                        list.add(siteData);
                    }
                    adapter.getFilter().filter(councilChoice);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        /**
         * Set up graphs and buttons for weekly and monthly data
         */

        tvBinCount1 = findViewById(R.id.tvAlertCount1);
        tvBinCount2 = findViewById(R.id.tvAlertCount2);
        tvBinCount3 = findViewById(R.id.tvAlertCount3);
        tvBinCount4 = findViewById(R.id.tvAlertCount4);
        tvBinCount5 = findViewById(R.id.tvAlertCount5);
        tvBinCount6 = findViewById(R.id.tvAlertCount6);
        tvBinCount7 = findViewById(R.id.tvAlertCount7);
        tvBinCount8 = findViewById(R.id.tvAlertCount8);
        tvBinCount9 = findViewById(R.id.tvAlertCount9);
        tvBinCount10 = findViewById(R.id.tvAlertCount10);

        tvWeekly = findViewById(R.id.tvAlertWeekly);
        tvMonthly = findViewById(R.id.tvAlertMonthly);

        // if user presses weekly, the colours will change
        tvWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvWeekly.setTextColor(Color.parseColor("#2995E2"));
                tvMonthly.setTextColor(Color.parseColor("#87BBDF"));

                tvBinCount1.setText(String.valueOf(contaminatedBin1));
                tvBinCount2.setText(String.valueOf(contaminatedBin2));
                tvBinCount3.setText(String.valueOf(contaminatedBin3));
                tvBinCount4.setText(String.valueOf(contaminatedBin4));
                tvBinCount5.setText(String.valueOf(contaminatedBin5));
                tvBinCount6.setText(String.valueOf(contaminatedBin6));
                tvBinCount7.setText(String.valueOf(contaminatedBin7));
                tvBinCount8.setText(String.valueOf(contaminatedBin8));
                tvBinCount9.setText(String.valueOf(contaminatedBin9));
                tvBinCount10.setText(String.valueOf(contaminatedBin10));

            }
        });

        // if user clicks monthly, monthly data will be displayed
        tvMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // light blue
                tvWeekly.setTextColor(Color.parseColor("#87BBDF"));
                // dark blue
                tvMonthly.setTextColor(Color.parseColor("#2995E2"));

                tvBinCount1.setText(String.valueOf(contaminatedMonthBin1));
                tvBinCount2.setText(String.valueOf(contaminatedMonthBin2));
                tvBinCount3.setText(String.valueOf(contaminatedMonthBin3));
                tvBinCount4.setText(String.valueOf(contaminatedMonthBin4));
                tvBinCount5.setText(String.valueOf(contaminatedMonthBin5));
                tvBinCount6.setText(String.valueOf(contaminatedMonthBin6));
                tvBinCount7.setText(String.valueOf(contaminatedMonthBin7));
                tvBinCount8.setText(String.valueOf(contaminatedMonthBin8));
                tvBinCount9.setText(String.valueOf(contaminatedMonthBin9));
                tvBinCount10.setText(String.valueOf(contaminatedMonthBin10));

            }
        });

        databaseChart = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        databaseChart.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);

                        /**
                         * Set up weekly data
                         */
                        if (binData.getCouncilName().contains(councilChoice)) {

                            if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-23")) {
                                recycableCountMon = recycableCountMon + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-23")) {
                                contaminatedCountMon = contaminatedCountMon + 1;

                                totalWeightMon = totalWeightMon + binData.geteWasteWeightKilos();
                            }

                            // TUESDAY:
                            if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-24")) {
                                recycableCountTues = recycableCountTues + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-24")) {
                                contaminatedCountTues = contaminatedCountTues + 1;

                                totalWeightTues = totalWeightTues + binData.geteWasteWeightKilos();
                            }

                            // WEDNESDAY:
                            if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-25")) {
                                recycableCountWed = recycableCountWed + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-25")) {
                                contaminatedCountWed = contaminatedCountWed + 1;

                                totalWeightWed = totalWeightWed + binData.geteWasteWeightKilos();
                            }

                            // THURSDAY:
                            if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-26")) {
                                recycableCountThurs = recycableCountThurs + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-26")) {
                                contaminatedCountThurs = contaminatedCountThurs + 1;

                                totalWeightThurs = totalWeightThurs + binData.geteWasteWeightKilos();
                            }

                            // FRIDAY:
                            if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-27")) {
                                recycableCountFri = recycableCountFri + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-27")) {
                                contaminatedCountFri = contaminatedCountFri + 1;

                                totalWeightFri = totalWeightFri + binData.geteWasteWeightKilos();
                            }

                            // SATURDAY:
                            if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-28")) {
                                recycableCountSat = recycableCountSat + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-28")) {
                                contaminatedCountSat = contaminatedCountSat + 1;

                                totalWeightSat = totalWeightSat + binData.geteWasteWeightKilos();
                            }

                            // SUNDAY:
                            if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-29")) {
                                recycableCountSun = recycableCountSun + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-29")) {
                                contaminatedCountSun = contaminatedCountSun + 1;

                                totalWeightSun = totalWeightSun + binData.geteWasteWeightKilos();
                            }
                            /**
                             * Set up Monthly data
                             */

                            // calculate total bins contaminated vs not contaminated in the latest Month (Week 1,2,3,4)
                            // 1-8 October:
                            if (binData.getStatus().equals("Bin Fully Recyclable ")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-01")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-02")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-03")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-04")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-05")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-06")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-07")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-08"))) {
                                recycableCountWeekOne = recycableCountWeekOne + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-01")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-02")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-03")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-04")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-05")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-06")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-07")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-08"))) {
                                contaminatedCountWeekOne = contaminatedCountWeekOne + 1;

                                totalWeightWeekOne = totalWeightWeekOne + binData.geteWasteWeightKilos();
                            }

                            // 9-16 October
                            if (binData.getStatus().equals("Bin Fully Recyclable ")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-09")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-10")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-11")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-12")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-13")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-14")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-15")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-16"))) {
                                recycableCountWeekTwo = recycableCountWeekTwo + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-09")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-10")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-11")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-12")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-13")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-14")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-15")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-16"))) {
                                contaminatedCountWeekTwo = contaminatedCountWeekTwo + 1;

                                totalWeightWeekTwo = totalWeightWeekTwo + binData.geteWasteWeightKilos();
                            }

                            // 17-24 October
                            if (binData.getStatus().equals("Bin Fully Recyclable ")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-17")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-18")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-19")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-20")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-21")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-22")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-23")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-24"))) {
                                recycableCountWeekThree = recycableCountWeekThree + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-17")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-18")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-19")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-20")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-21")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-22")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-23")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-24"))) {
                                contaminatedCountWeekThree = contaminatedCountWeekThree + 1;

                                totalWeightWeekThree = totalWeightWeekThree + binData.geteWasteWeightKilos();
                            }

                            // 25-31 October
                            if (binData.getStatus().equals("Bin Fully Recyclable ")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-25")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-26")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-27")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-28")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-29")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-30")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-31"))) {
                                recycableCountWeekFour = recycableCountWeekFour + 1;
                            } else if (binData.getStatus().equals("Bin Flagged as Contaminated")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-25")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-26")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-27")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-28")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-29")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-30")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-31"))) {
                                contaminatedCountWeekFour = contaminatedCountWeekFour + 1;

                                totalWeightWeekFour = totalWeightWeekFour + binData.geteWasteWeightKilos();

                            }

                            /**
                             * Set up table bin contamination count - weekly
                             */

                            if (binData.getStatus().equals("Bin Flagged as Contaminated")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-25")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-26")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-27")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-28")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-29")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-30")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-31"))) {

                                if (binData.getBinName().contains(tvBinName1.getText())) {
                                    contaminatedBin1 = contaminatedBin1 + 1;
                                } else if (binData.getBinName().contains(tvBinName2.getText())) {
                                    contaminatedBin2 = contaminatedBin2 + 1;
                                } else if (binData.getBinName().contains(tvBinName3.getText())) {
                                    contaminatedBin3 = contaminatedBin3 + 1;
                                } else if (binData.getBinName().contains(tvBinName4.getText())) {
                                    contaminatedBin4 = contaminatedBin4 + 1;
                                } else if (binData.getBinName().contains(tvBinName5.getText())) {
                                    contaminatedBin5 = contaminatedBin5 + 1;
                                } else if (binData.getBinName().contains(tvBinName6.getText())) {
                                    contaminatedBin6 = contaminatedBin6 + 1;
                                } else if (binData.getBinName().contains(tvBinName7.getText())) {
                                    contaminatedBin7 = contaminatedBin7 + 1;
                                } else if (binData.getBinName().contains(tvBinName8.getText())) {
                                    contaminatedBin8 = contaminatedBin8 + 1;
                                } else if (binData.getBinName().contains(tvBinName9.getText())) {
                                    contaminatedBin9 = contaminatedBin9 + 1;
                                } else if (binData.getBinName().contains(tvBinName10.getText())) {
                                    contaminatedBin10 = contaminatedBin10 + 1;
                                }

                            }

                            /**
                             * Set up contaminated bin count for table - monthly
                             */
                            if (binData.getStatus().equals("Bin Flagged as Contaminated")) {
                                if (binData.getBinName().contains(tvBinName1.getText())) {
                                    contaminatedMonthBin1 = contaminatedMonthBin1 + 1;
                                } else if (binData.getBinName().contains(tvBinName2.getText())) {
                                    contaminatedMonthBin2 = contaminatedMonthBin2 + 1;
                                } else if (binData.getBinName().contains(tvBinName3.getText())) {
                                    contaminatedMonthBin3 = contaminatedMonthBin3 + 1;
                                } else if (binData.getBinName().contains(tvBinName4.getText())) {
                                    contaminatedMonthBin4 = contaminatedMonthBin4 + 1;
                                } else if (binData.getBinName().contains(tvBinName5.getText())) {
                                    contaminatedMonthBin5 = contaminatedMonthBin5 + 1;
                                } else if (binData.getBinName().contains(tvBinName6.getText())) {
                                    contaminatedMonthBin6 = contaminatedMonthBin6 + 1;
                                } else if (binData.getBinName().contains(tvBinName7.getText())) {
                                    contaminatedMonthBin7 = contaminatedMonthBin7 + 1;
                                } else if (binData.getBinName().contains(tvBinName8.getText())) {
                                    contaminatedMonthBin8 = contaminatedMonthBin8 + 1;
                                } else if (binData.getBinName().contains(tvBinName9.getText())) {
                                    contaminatedMonthBin9 = contaminatedMonthBin9 + 1;
                                } else if (binData.getBinName().contains(tvBinName10.getText())) {
                                    contaminatedMonthBin10 = contaminatedMonthBin10 + 1;
                                }
                            }

                        }

                        // if weekly is selected, contamination count will be calculated depending on
                        // weekly data
                        if (tvWeekly.getCurrentTextColor() == Color.parseColor("#2995E2")) {

                        }
                        tvWeekly.setTextColor(Color.parseColor("#2995E2"));

                    }
                    /**
                     * Set table bin contamination count values
                     */
                    // if weekly is selected
                    if (tvWeekly.getCurrentTextColor() == Color.parseColor("#2995E2")) {
                        tvBinCount1.setText(String.valueOf(contaminatedBin1));
                        tvBinCount2.setText(String.valueOf(contaminatedBin2));
                        tvBinCount3.setText(String.valueOf(contaminatedBin3));
                        tvBinCount4.setText(String.valueOf(contaminatedBin4));
                        tvBinCount5.setText(String.valueOf(contaminatedBin5));
                        tvBinCount6.setText(String.valueOf(contaminatedBin6));
                        tvBinCount7.setText(String.valueOf(contaminatedBin7));
                        tvBinCount8.setText(String.valueOf(contaminatedBin8));
                        tvBinCount9.setText(String.valueOf(contaminatedBin9));
                        tvBinCount10.setText(String.valueOf(contaminatedBin10));
                    } else {
                        tvBinCount1.setText(String.valueOf(contaminatedMonthBin1));
                        tvBinCount2.setText(String.valueOf(contaminatedMonthBin2));
                        tvBinCount3.setText(String.valueOf(contaminatedMonthBin3));
                        tvBinCount4.setText(String.valueOf(contaminatedMonthBin4));
                        tvBinCount5.setText(String.valueOf(contaminatedMonthBin5));
                        tvBinCount6.setText(String.valueOf(contaminatedMonthBin6));
                        tvBinCount7.setText(String.valueOf(contaminatedMonthBin7));
                        tvBinCount8.setText(String.valueOf(contaminatedMonthBin8));
                        tvBinCount9.setText(String.valueOf(contaminatedMonthBin9));
                        tvBinCount10.setText(String.valueOf(contaminatedMonthBin10));
                    }

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
                    Intent intent = new Intent(AlertAnalysisActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(AlertAnalysisActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(AlertAnalysisActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(AlertAnalysisActivity.this, ProfileActivity.class);
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
    public void onRowClick(String name) {
        launchSelectedResource(name);

    }

    private void launchSelectedResource(String name) {
        Intent intent = new Intent(AlertAnalysisActivity.this, GuideActivity.class);
        intent.putExtra(GuideActivity.INTENT_MESSAGE, name);
        startActivity(intent);
    }
}