package com.example.wastegenie.Analysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastegenie.Adapters.BinAnalysisAdapter;
import com.example.wastegenie.DataModels.BinData;
import com.example.wastegenie.DataModels.DropOffData;
import com.example.wastegenie.LoginActivity;
import com.example.wastegenie.MainActivity;
import com.example.wastegenie.ProfileActivity;
import com.example.wastegenie.R;
import com.example.wastegenie.TrackingActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CouncilAnalysisActivity extends AppCompatActivity implements RecyclerViewDropOffClickListener{

    NavigationView navigationView;
    TextView tvCouncilName;
    TextView tvCouncilAddress;
    DatabaseReference database;
    String councilLocation;
    // for bar graph
    RelativeLayout rlWeekly;
    RelativeLayout rlMonthly;
    TextView tvWeekly;
    TextView tvMonthly;
    BarChart barChart;
    BarChart barChartMonthly;
    DatabaseReference databaseChart;
    int[] colorClassArray = new int[]{Color.BLUE, Color.CYAN};
    ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
    BarData barData;
    ArrayList<IBarDataSet> iBarDataSetsMonthly = new ArrayList<>();
    BarData barDataMonthly;
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

    // for line graph
    LineChart lineChart;
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;

    LineChart lineChartMonthly;
    ArrayList<ILineDataSet> iLineDataSetsMonthly = new ArrayList<>();
    LineData lineDataMonthly;
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

    // for council bins table
    TextView tvBinName1;
    TextView tvBinName2;
    TextView tvBinName3;
    TextView tvBinName4;
    TextView tvBinName5;
    TextView tvBinName6;
    TextView tvBinName7;
    TextView tvBinName8;
    TextView tvBinName9;
    TextView tvBinName10;

    TextView tvBinLocation1;
    TextView tvBinLocation2;
    TextView tvBinLocation3;
    TextView tvBinLocation4;
    TextView tvBinLocation5;
    TextView tvBinLocation6;
    TextView tvBinLocation7;
    TextView tvBinLocation8;
    TextView tvBinLocation9;
    TextView tvBinLocation10;

    TextView tvBinCount1;
    TextView tvBinCount2;
    TextView tvBinCount3;
    TextView tvBinCount4;
    TextView tvBinCount5;
    TextView tvBinCount6;
    TextView tvBinCount7;
    TextView tvBinCount8;
    TextView tvBinCount9;
    TextView tvBinCount10;

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

    Button btAlertCouncil;

    /**
     * Recyclerview
     */
    RecyclerView recyclerView;
    DatabaseReference databaseRecycler;
    BinAnalysisAdapter adapter;
    ArrayList<DropOffData> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_council_analysis);

        /**
         * Set up council details
         */
        Intent intent = getIntent();
        String councilChoice = intent.getStringExtra("Council Selection");

        tvCouncilName = findViewById(R.id.tvCouncilAnalysisCouncil);
        tvCouncilAddress = findViewById(R.id.tvCouncilAnalysisAddress);
        tvCouncilName.setText(councilChoice);

        tvBinName1 = findViewById(R.id.tvCouncilAnalysisBin1);
        tvBinName2 = findViewById(R.id.tvCouncilAnalysisBin2);
        tvBinName3 = findViewById(R.id.tvCouncilAnalysisBin3);
        tvBinName4 = findViewById(R.id.tvCouncilAnalysisBin4);
        tvBinName5 = findViewById(R.id.tvCouncilAnalysisBin5);
        tvBinName6 = findViewById(R.id.tvCouncilAnalysisBin6);
        tvBinName7 = findViewById(R.id.tvCouncilAnalysisBin7);
        tvBinName8 = findViewById(R.id.tvCouncilAnalysisBin8);
        tvBinName9 = findViewById(R.id.tvCouncilAnalysisBin9);
        tvBinName10 = findViewById(R.id.tvCouncilAnalysisBin10);

        tvBinLocation1 = findViewById(R.id.tvCouncilAnalysisLocation1);
        tvBinLocation2 = findViewById(R.id.tvCouncilAnalysisLocation2);
        tvBinLocation3 = findViewById(R.id.tvCouncilAnalysisLocation3);
        tvBinLocation4 = findViewById(R.id.tvCouncilAnalysisLocation4);
        tvBinLocation5 = findViewById(R.id.tvCouncilAnalysisLocation5);
        tvBinLocation6 = findViewById(R.id.tvCouncilAnalysisLocation6);
        tvBinLocation7 = findViewById(R.id.tvCouncilAnalysisLocation7);
        tvBinLocation8 = findViewById(R.id.tvCouncilAnalysisLocation8);
        tvBinLocation9 = findViewById(R.id.tvCouncilAnalysisLocation9);
        tvBinLocation10 = findViewById(R.id.tvCouncilAnalysisLocation10);

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
         * Set up the recyclerview
         */
        recyclerView = findViewById(R.id.rvCouncilAnalysisDisposal);
        databaseRecycler = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet2");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new BinAnalysisAdapter(this, list, this);

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

        tvBinCount1 = findViewById(R.id.tvCouncilAnalysisCount1);
        tvBinCount2 = findViewById(R.id.tvCouncilAnalysisCount2);
        tvBinCount3 = findViewById(R.id.tvCouncilAnalysisCount3);
        tvBinCount4 = findViewById(R.id.tvCouncilAnalysisCount4);
        tvBinCount5 = findViewById(R.id.tvCouncilAnalysisCount5);
        tvBinCount6 = findViewById(R.id.tvCouncilAnalysisCount6);
        tvBinCount7 = findViewById(R.id.tvCouncilAnalysisCount7);
        tvBinCount8 = findViewById(R.id.tvCouncilAnalysisCount8);
        tvBinCount9 = findViewById(R.id.tvCouncilAnalysisCount9);
        tvBinCount10 = findViewById(R.id.tvCouncilAnalysisCount10);

        tvWeekly = findViewById(R.id.tvCouncilAnalysisWeekly);
        tvMonthly = findViewById(R.id.tvCouncilAnalysisMonthly);
        rlWeekly = findViewById(R.id.rlCouncilAnalysisWeekly);
        rlMonthly = findViewById(R.id.rlCouncilAnalysisMonthChart);

        barChart = findViewById(R.id.bcCouncilAnalysisWeekly);
        lineChart = findViewById(R.id.lcCouncilAnalysisWeekly);
        barChartMonthly = findViewById(R.id.bcCouncilAnalysisMonth);
        lineChartMonthly = findViewById(R.id.lcCouncilAnalysisMonth);

        // if user presses weekly, the colours will change
        tvWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvWeekly.setTextColor(Color.parseColor("#2995E2"));
                tvMonthly.setTextColor(Color.parseColor("#87BBDF"));
                rlWeekly.setVisibility(View.VISIBLE);
                rlMonthly.setVisibility(View.INVISIBLE);

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
                rlWeekly.setVisibility(View.INVISIBLE);
                rlMonthly.setVisibility(View.VISIBLE);

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

                ArrayList<BarEntry> dataVals = new ArrayList<>();
                ArrayList<Entry> lineDataVals = new ArrayList<Entry>();

                ArrayList<BarEntry> dataValsMonthly = new ArrayList<>();
                ArrayList<Entry> lineDataValsMonthly = new ArrayList<Entry>();

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


                    /**
                     * Set weekly bar chart values
                     */
                    dataVals.add(new BarEntry(0, new float[]{recycableCountMon, contaminatedCountMon}));
                    dataVals.add(new BarEntry(1, new float[]{recycableCountTues, contaminatedCountTues}));
                    dataVals.add(new BarEntry(2, new float[]{recycableCountWed, contaminatedCountWed}));
                    dataVals.add(new BarEntry(3, new float[]{recycableCountThurs, contaminatedCountThurs}));
                    dataVals.add(new BarEntry(4, new float[]{recycableCountFri, contaminatedCountFri}));
                    dataVals.add(new BarEntry(5, new float[]{recycableCountSat, contaminatedCountSat}));
                    dataVals.add(new BarEntry(6, new float[]{recycableCountSun, contaminatedCountSun}));

                    BarDataSet barDataSet = new BarDataSet(dataVals, "Bins Fully Recyclable");
                    barDataSet.setColors(colorClassArray);

                    // create a list of IDataSets to build ChartData object
                    iBarDataSets.clear();
                    iBarDataSets.add(barDataSet);
                    barData = new BarData(iBarDataSets);

                    // other formatting
                    barData.setBarWidth(0.7f); // set custom bar width
                    barChart.getDescription().setEnabled(false);
                    barChart.setExtraBottomOffset(3f);

                    // legend formatting
                    Legend l = barChart.getLegend();
                    LegendEntry l1=new LegendEntry("Recycled", Legend.LegendForm.DEFAULT,10f,2f,null, Color.BLUE);
                    LegendEntry l2=new LegendEntry("Contaminated", Legend.LegendForm.DEFAULT,10f,2f,null, Color.CYAN);
                    l.setCustom(new LegendEntry[]{l1,l2});

                    l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    l.setDrawInside(false);
                    l.setTextSize(15);

                    // set X-axis formatting
                    final ArrayList<String> xLabel = new ArrayList<>();
                    xLabel.add("Monday");
                    xLabel.add("Tuesday");
                    xLabel.add("Wednesday");
                    xLabel.add("Thursday");
                    xLabel.add("Friday");
                    xLabel.add("Saturday");
                    xLabel.add("Sunday");

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return xLabel.get((int) value);
                        }
                    });

                    xAxis.setTextSize(15);

                    // set barChart
                    barChart.setData(barData);
                    barChart.getBarData().setValueTextSize(15);
                    barChart.setFitBars(true);
                    barChart.invalidate();

                    /**
                     * Set line chart values
                     */
                    lineDataVals.add(new Entry(0, totalWeightMon));
                    lineDataVals.add(new Entry(1, totalWeightTues));
                    lineDataVals.add(new Entry(2, totalWeightWed));
                    lineDataVals.add(new Entry(3, totalWeightThurs));
                    lineDataVals.add(new Entry(4, totalWeightFri));
                    lineDataVals.add(new Entry(5, totalWeightSat));
                    lineDataVals.add(new Entry(6, totalWeightSun));

                    LineDataSet lineDataSet = new LineDataSet(lineDataVals, "E-Waste Contamination (Kg)");
                    lineDataSet.setColors(Color.BLUE);

                    // create a list of IDataSets to build ChartData object
                    iLineDataSets.clear();
                    iLineDataSets.add(lineDataSet);
                    lineData = new LineData(iLineDataSets);

                    // other formatting
                    lineChart.getDescription().setEnabled(false);
                    lineChart.setExtraBottomOffset(3f);
                    lineChart.setExtraLeftOffset(5f);
                    lineChart.setExtraRightOffset(5f);

                    Legend lLine = lineChart.getLegend();

                    lLine.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    lLine.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    lLine.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    lLine.setDrawInside(false);
                    lLine.setTextSize(15);

                    // set X-axis formatting
                    final ArrayList<String> xLabelLine = new ArrayList<>();
                    xLabelLine.add("Monday");
                    xLabelLine.add("Tuesday");
                    xLabelLine.add("Wednesday");
                    xLabelLine.add("Thursday");
                    xLabelLine.add("Friday");
                    xLabelLine.add("Saturday");
                    xLabelLine.add("Sunday");

                    XAxis xAxisLine = lineChart.getXAxis();
                    xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisLine.setDrawGridLines(false);
                    xAxisLine.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return xLabelLine.get((int) value);
                        }
                    });

                    xAxisLine.setTextSize(15);

                    // set barChart
                    lineChart.setData(lineData);
                    lineChart.getLineData().setValueTextSize(15);
                    lineChart.invalidate();

                    /**
                     * Set up monthly chart values
                     */

                    dataValsMonthly.add(new BarEntry(0, new float[]{recycableCountWeekOne, contaminatedCountWeekOne}));
                    dataValsMonthly.add(new BarEntry(1, new float[]{recycableCountWeekTwo, contaminatedCountWeekTwo}));
                    dataValsMonthly.add(new BarEntry(2, new float[]{recycableCountWeekThree, contaminatedCountWeekThree}));
                    dataValsMonthly.add(new BarEntry(3, new float[]{recycableCountWeekFour, contaminatedCountWeekFour}));

                    BarDataSet barDataSetMonthly = new BarDataSet(dataValsMonthly, "Bins Fully Recyclable");
                    barDataSetMonthly.setColors(colorClassArray);

                    // create a list of IDataSets to build ChartData object
                    iBarDataSetsMonthly.clear();
                    iBarDataSetsMonthly.add(barDataSetMonthly);
                    barDataMonthly = new BarData(iBarDataSetsMonthly);

                    // other formatting
                    //barDataMonthly.setBarWidth(0.7f); // set custom bar width
                    barChartMonthly.getDescription().setEnabled(false);
                    barChartMonthly.setExtraBottomOffset(4f);

                    // legend formatting
                    Legend lMonthly = barChartMonthly.getLegend();
                    LegendEntry l1Monthly=new LegendEntry("Recycled", Legend.LegendForm.DEFAULT,10f,2f,null, Color.BLUE);
                    LegendEntry l2Monthly=new LegendEntry("Contaminated", Legend.LegendForm.DEFAULT,10f,2f,null, Color.CYAN);
                    lMonthly.setCustom(new LegendEntry[]{l1Monthly,l2Monthly});

                    lMonthly.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    lMonthly.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    lMonthly.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    lMonthly.setDrawInside(false);
                    lMonthly.setTextSize(15);

                    // set X-axis formatting
                    final ArrayList<String> xLabelMonthly = new ArrayList<>();
                    xLabelMonthly.add("1-8 Oct");
                    xLabelMonthly.add("9-16 Oct");
                    xLabelMonthly.add("17-24 Oct");
                    xLabelMonthly.add("25-31 Oct");

                    XAxis xAxisMonthly = barChartMonthly.getXAxis();
                    xAxisMonthly.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisMonthly.setDrawGridLines(false);
                    xAxisMonthly.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return xLabelMonthly.get((int) value);
                        }
                    });
                    xAxisMonthly.setLabelCount(xLabelMonthly.size(), false);
                    xAxisMonthly.setGranularityEnabled(true);

                    xAxisMonthly.setTextSize(15);

                    // set barChart
                    barChartMonthly.setData(barDataMonthly);
                    barChartMonthly.getBarData().setValueTextSize(15);
                    barChartMonthly.setFitBars(true);
                    barChartMonthly.invalidate();

                    /**
                     * Set line chart values
                     */
                    lineDataValsMonthly.add(new Entry(0, totalWeightWeekOne));
                    lineDataValsMonthly.add(new Entry(1, totalWeightWeekTwo));
                    lineDataValsMonthly.add(new Entry(2, totalWeightWeekThree));
                    lineDataValsMonthly.add(new Entry(3, totalWeightWeekFour));

                    LineDataSet lineDataSetMonthly = new LineDataSet(lineDataValsMonthly, "E-Waste Contamination (Kg)");
                    lineDataSetMonthly.setColors(Color.BLUE);

                    // create a list of IDataSets to build ChartData object
                    iLineDataSetsMonthly.clear();
                    iLineDataSetsMonthly.add(lineDataSetMonthly);
                    lineDataMonthly = new LineData(iLineDataSetsMonthly);

                    // other formatting
                    lineChartMonthly.getDescription().setEnabled(false);
                    lineChartMonthly.setExtraBottomOffset(3f);
                    lineChartMonthly.setExtraLeftOffset(7f);
                    lineChartMonthly.setExtraRightOffset(10f);

                    Legend lLineMonthly = lineChartMonthly.getLegend();

                    lLineMonthly.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    lLineMonthly.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    lLineMonthly.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    lLineMonthly.setDrawInside(false);
                    lLineMonthly.setTextSize(15);

                    // set X-axis formatting
                    final ArrayList<String> xLabelLineMonthly = new ArrayList<>();
                    xLabelLineMonthly.add("1-8 Oct");
                    xLabelLineMonthly.add("9-16 Oct");
                    xLabelLineMonthly.add("17-24 Oct");
                    xLabelLineMonthly.add("25-31 Oct");

                    XAxis xAxisLineMonthly = lineChartMonthly.getXAxis();
                    xAxisLineMonthly.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisLineMonthly.setDrawGridLines(false);
                    xAxisLineMonthly.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return xLabelLineMonthly.get((int) value);
                        }
                    });

                    lineChartMonthly.getXAxis().setLabelCount(4, /*force: */true);

                    xAxisLineMonthly.setTextSize(15);

                    // set barChart
                    lineChartMonthly.setData(lineDataMonthly);
                    lineChartMonthly.getLineData().setValueTextSize(15);
                    lineChartMonthly.invalidate();

                }
            }
        });

        /**
         * Button to alert council
         */

        btAlertCouncil = findViewById(R.id.btCouncilAnalysisAlertCouncil);
        btAlertCouncil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chosenCouncil = tvCouncilName.getText().toString();
                Intent intent = new Intent(CouncilAnalysisActivity.this, AlertAnalysisActivity.class);
                intent.putExtra("Analysis Council", chosenCouncil);
                startActivity(intent);
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
                    Intent intent = new Intent(CouncilAnalysisActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    Intent intent = new Intent(CouncilAnalysisActivity.this, AnalysisActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(CouncilAnalysisActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(CouncilAnalysisActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.logout) {
                    Toast.makeText(getApplication(), "You have been logged out.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CouncilAnalysisActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });
    }

    @Override
    public void onRowClickDropOff(String name) {
        launchSelectedDropOff(name);
    }

    private void launchSelectedDropOff(String name) {
        Intent intent = new Intent(CouncilAnalysisActivity.this, DisposalActivity.class);
        intent.putExtra(DisposalActivity.INTENT_MESSAGE, name);
        startActivity(intent);

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