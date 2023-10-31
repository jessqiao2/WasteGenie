package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class BinAnalysisActivity extends AppCompatActivity {

    NavigationView navigationView;

    /**
     * Bin data
     */
    TextView tvBinName, tvBinCouncil, tvBinTruck;
    String binCouncil, binTruck;
    DatabaseReference database;

    /**
     * Monthly and weekly data
     */
    TextView tvMonthly, tvWeekly;

    // for bar graph
    RelativeLayout rlWeekly, rlMonthly;
    BarChart barChart, barChartMonthly;
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

    /**
     * Bin analysis
     */
    TextView tvContaminationCount, tvContaminationRate, tvEwasteWeight;

    /**
     * Set up table
     */

    TextView tvConsumerElecWeight, tvComputerTeleWeight, tvSmallAppWeight, tvLightingDevWeight, tvOtherWeight;
    TextView tvConsumerElecPerc, tvComputerTelePerc, tvSmallAppPerc, tvLightingDevPerc, tvOtherPerc;
    double consumerElecWeightWeekly;
    double computerTeleWeightWeekly;
    double smallAppWeightWeekly;
    double lightingDevWeightWeekly;
    double otherWeightWeekly;

    double consumerElecWeightMonthly;
    double computerTeleWeightMonthly;
    double smallAppWeightMonthly;
    double lightingDevWeightMonthly;
    double otherWeightMonthly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin_analysis);

        /**
         * Setting up the bin data
         */
        Intent intent = getIntent();
        String binChoice = intent.getStringExtra("Bin Selection");

        tvBinName = findViewById(R.id.tvBinAnalysisBinName);
        tvBinCouncil = findViewById(R.id.tvBinAnalysisCouncil);
        tvBinTruck = findViewById(R.id.tvBinAnalysisTruck);
        tvContaminationCount = findViewById(R.id.tvAnalysisBinContaminationCount);
        tvContaminationRate = findViewById(R.id.tvBinAnalysisContaminationRate);
        tvEwasteWeight = findViewById(R.id.tvBinAnalysisWeight);
        tvConsumerElecWeight = findViewById(R.id.tvBinAnalysisConsumerElectWeight);
        tvComputerTeleWeight = findViewById(R.id.tvBinAnalysisCompTeleWeight);
        tvSmallAppWeight = findViewById(R.id.tvBinAnalysisSmallAppWeight);
        tvLightingDevWeight = findViewById(R.id.tvBinAnalysisLightingDevWeight);
        tvOtherWeight = findViewById(R.id.tvBinAnalysisOtherWeight);
        tvConsumerElecPerc = findViewById(R.id.tvBinAnalysisConsumerElectPerc);
        tvComputerTelePerc = findViewById(R.id.tvBinAnalysisCompTelePerc);
        tvSmallAppPerc = findViewById(R.id.tvBinAnalysisSmallAppPerc);
        tvLightingDevPerc = findViewById(R.id.tvBinAnalysisLightingDevPerc);
        tvOtherPerc = findViewById(R.id.tvBinAnalysisOtherPerc);

        tvBinName.setText(binChoice);

        // connecting firebase data
        database = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);

                        if (binData.getBinName().contains(binChoice)) {
                            binCouncil = binData.getCouncilName();
                            binTruck = binData.getTruckId();
                        }

                    }
                    tvBinCouncil.setText(binCouncil);
                    tvBinTruck.setText(binTruck);
                }
            }
        });

        /**
         * Setting up weekly and monthly stats
         */

        tvWeekly = findViewById(R.id.tvBinAnalysisWeekly);
        tvMonthly = findViewById(R.id.tvBinAnalysisMonthly);
        rlWeekly = findViewById(R.id.rlBinAnalysisWeekly);
        rlMonthly = findViewById(R.id.rlBinAnalysisMonthly);

        barChart = findViewById(R.id.bcBinAnalysisWeekly);
        lineChart = findViewById(R.id.lcBinAnalysisWeekly);
        barChartMonthly = findViewById(R.id.bcBinAnalysisMonthly);
        lineChartMonthly = findViewById(R.id.lcBinAnalysisMonthly);

        // if user presses weekly, the colours will change
        tvWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvWeekly.setTextColor(Color.parseColor("#2995E2"));
                tvMonthly.setTextColor(Color.parseColor("#87BBDF"));
                rlWeekly.setVisibility(View.VISIBLE);
                rlMonthly.setVisibility(View.INVISIBLE);

                // contamination count
                int contaminatedWeeklyTotal = contaminatedCountMon + contaminatedCountTues + contaminatedCountWed
                        + contaminatedCountThurs + contaminatedCountFri + contaminatedCountSat + contaminatedCountSun;
                int recycledWeeklyTotal = recycableCountMon + recycableCountTues + recycableCountWed + recycableCountThurs
                        + recycableCountFri + recycableCountSat + recycableCountSun;
                tvContaminationCount.setText(String.valueOf(contaminatedWeeklyTotal) + "/" + String.valueOf(contaminatedWeeklyTotal+recycledWeeklyTotal));

                // contamination rate
                int contaminationRate = contaminatedWeeklyTotal*100/(contaminatedWeeklyTotal + recycledWeeklyTotal);
                tvContaminationRate.setText(String.valueOf(contaminationRate) + "%");

                // ewaste weight
                int eWasteWeight = totalWeightMon + totalWeightTues + totalWeightWed + totalWeightThurs + totalWeightFri + totalWeightSat + totalWeightSun;
                tvEwasteWeight.setText(String.valueOf(eWasteWeight));

                tvConsumerElecWeight.setText(String.valueOf(consumerElecWeightWeekly));
                tvConsumerElecPerc.setText(String.valueOf((consumerElecWeightWeekly*100)/eWasteWeight));

                tvComputerTeleWeight.setText(String.valueOf(computerTeleWeightWeekly));
                tvComputerTelePerc.setText(String.valueOf((computerTeleWeightWeekly*100)/eWasteWeight));

                tvSmallAppWeight.setText(String.valueOf(smallAppWeightWeekly));
                tvSmallAppPerc.setText(String.valueOf((smallAppWeightWeekly*100)/eWasteWeight));

                tvLightingDevWeight.setText(String.valueOf(lightingDevWeightWeekly));
                tvLightingDevPerc.setText(String.valueOf((lightingDevWeightWeekly*100)/eWasteWeight));

                tvOtherWeight.setText(String.valueOf(otherWeightWeekly));
                tvOtherPerc.setText(String.valueOf((otherWeightWeekly*100)/eWasteWeight));

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

                int contaminatedMonthlyTotal = contaminatedCountWeekOne + contaminatedCountWeekTwo + contaminatedCountWeekThree + contaminatedCountWeekFour;
                int recycledMonthlyTotal = recycableCountWeekOne + recycableCountWeekTwo + recycableCountWeekThree + recycableCountWeekFour;
                tvContaminationCount.setText(String.valueOf(contaminatedMonthlyTotal) + "/" + String.valueOf(contaminatedMonthlyTotal+recycledMonthlyTotal));

                // contamination rate
                int contaminationRate = contaminatedMonthlyTotal*100/(contaminatedMonthlyTotal + recycledMonthlyTotal);
                tvContaminationRate.setText(String.valueOf(contaminationRate) + "%");

                // ewaste weight
                int eWasteWeight = totalWeightWeekOne + totalWeightWeekTwo + totalWeightWeekThree + totalWeightWeekFour;
                tvEwasteWeight.setText(String.valueOf(eWasteWeight));

                tvConsumerElecWeight.setText(String.valueOf(consumerElecWeightMonthly));
                tvConsumerElecPerc.setText(String.valueOf((consumerElecWeightMonthly*100)/eWasteWeight));

                tvComputerTeleWeight.setText(String.valueOf(computerTeleWeightMonthly));
                tvComputerTelePerc.setText(String.valueOf((computerTeleWeightMonthly*100)/eWasteWeight));

                tvSmallAppWeight.setText(String.valueOf(smallAppWeightMonthly));
                tvSmallAppPerc.setText(String.valueOf((smallAppWeightMonthly*100)/eWasteWeight));

                tvLightingDevWeight.setText(String.valueOf(lightingDevWeightMonthly));
                tvLightingDevPerc.setText(String.valueOf((lightingDevWeightMonthly*100)/eWasteWeight));

                tvOtherWeight.setText(String.valueOf(otherWeightMonthly));
                tvOtherPerc.setText(String.valueOf((otherWeightMonthly*100)/eWasteWeight));
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
                        if (binData.getBinName().contains(binChoice)) {

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

                            if (binData.getStatus().equals("Bin Flagged as Contaminated")
                                    && ((binData.getDate()).split("T")[0].equals("2023-10-23")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-24")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-25")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-26")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-27")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-28")
                                    || (binData.getDate()).split("T")[0].equals("2023-10-29"))) {

                                if (binData.getConsumerElectronicsPercent() != 0) {
                                    consumerElecWeightWeekly = (((double)binData.getConsumerElectronicsPercent()/100) * binData.geteWasteWeightKilos()) + consumerElecWeightWeekly;
                                }
                                if (binData.getComputerTelecomPercent() != 0) {
                                    computerTeleWeightWeekly= (((double)binData.getComputerTelecomPercent()/100) * binData.geteWasteWeightKilos()) + computerTeleWeightWeekly;
                                }
                                if (binData.getSmallAppliancesPercent() != 0) {
                                    smallAppWeightWeekly = (((double)binData.getSmallAppliancesPercent()/100) * binData.geteWasteWeightKilos()) + smallAppWeightWeekly;
                                }
                                if (binData.getLightingDevicesPercent() != 0) {
                                    lightingDevWeightWeekly = (((double)binData.getLightingDevicesPercent()/100) * binData.geteWasteWeightKilos()) + lightingDevWeightWeekly;
                                }
                                if (binData.getOtherPercent() != 0) {
                                    otherWeightWeekly = (((double)binData.getOtherPercent()/100) * binData.geteWasteWeightKilos()) + otherWeightWeekly;
                                }

                            }

                            if (binData.getConsumerElectronicsPercent() != 0) {
                                consumerElecWeightMonthly = (((double)binData.getConsumerElectronicsPercent()/100) * binData.geteWasteWeightKilos()) + consumerElecWeightMonthly;
                            }
                            if (binData.getComputerTelecomPercent() != 0) {
                                computerTeleWeightMonthly= ((double)(binData.getComputerTelecomPercent()/100) * binData.geteWasteWeightKilos()) + computerTeleWeightMonthly;
                            }
                            if (binData.getSmallAppliancesPercent() != 0) {
                                smallAppWeightMonthly = (((double)binData.getSmallAppliancesPercent()/100) * binData.geteWasteWeightKilos()) + smallAppWeightMonthly;
                            }
                            if (binData.getLightingDevicesPercent() != 0) {
                                lightingDevWeightMonthly = (((double)binData.getLightingDevicesPercent()/100) * binData.geteWasteWeightKilos()) + lightingDevWeightMonthly;
                            }
                            if (binData.getOtherPercent() != 0) {
                                otherWeightMonthly = (((double)binData.getOtherPercent()/100) * binData.geteWasteWeightKilos()) + otherWeightMonthly;
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

                        } else {
                            Log.i("BinAnalysis", "bin name does not match selection");
                        }

                    }

                    /**
                     * Set up weekly and monthly data
                     */
                    if (tvWeekly.getCurrentTextColor() == Color.parseColor("#2995E2")) {
                        // contamination count
                        int contaminatedWeeklyTotal = contaminatedCountMon + contaminatedCountTues + contaminatedCountWed
                                + contaminatedCountThurs + contaminatedCountFri + contaminatedCountSat + contaminatedCountSun;
                        int recycledWeeklyTotal = recycableCountMon + recycableCountTues + recycableCountWed + recycableCountThurs
                                + recycableCountFri + recycableCountSat + recycableCountSun;
                        tvContaminationCount.setText(String.valueOf(contaminatedWeeklyTotal) + "/" + String.valueOf(contaminatedWeeklyTotal+recycledWeeklyTotal));

                        // contamination rate
                        int contaminationRate = contaminatedWeeklyTotal*100/(contaminatedWeeklyTotal + recycledWeeklyTotal);
                        tvContaminationRate.setText(String.valueOf(contaminationRate) + "%");

                        // ewaste weight
                        int eWasteWeight = totalWeightMon + totalWeightTues + totalWeightWed + totalWeightThurs + totalWeightFri + totalWeightSat + totalWeightSun;
                        tvEwasteWeight.setText(String.valueOf(eWasteWeight));

                        tvConsumerElecWeight.setText(String.valueOf(consumerElecWeightWeekly));
                        tvConsumerElecPerc.setText(String.valueOf((consumerElecWeightWeekly*100)/eWasteWeight));

                        tvComputerTeleWeight.setText(String.valueOf(computerTeleWeightWeekly));
                        tvComputerTelePerc.setText(String.valueOf((computerTeleWeightWeekly*100)/eWasteWeight));

                        tvSmallAppWeight.setText(String.valueOf(smallAppWeightWeekly));
                        tvSmallAppPerc.setText(String.valueOf((smallAppWeightWeekly*100)/eWasteWeight));

                        tvLightingDevWeight.setText(String.valueOf(lightingDevWeightWeekly));
                        tvLightingDevPerc.setText(String.valueOf((lightingDevWeightWeekly*100)/eWasteWeight));

                        tvOtherWeight.setText(String.valueOf(otherWeightWeekly));
                        tvOtherPerc.setText(String.valueOf((otherWeightWeekly*100)/eWasteWeight));

                    } else {
                        int contaminatedMonthlyTotal = contaminatedCountWeekOne + contaminatedCountWeekTwo + contaminatedCountWeekThree + contaminatedCountWeekFour;
                        int recycledMonthlyTotal = recycableCountWeekOne + recycableCountWeekTwo + recycableCountWeekThree + recycableCountWeekFour;
                        tvContaminationCount.setText(String.valueOf(contaminatedMonthlyTotal) + "/" + String.valueOf(contaminatedMonthlyTotal+recycledMonthlyTotal));

                        // contamination rate
                        int contaminationRate = contaminatedMonthlyTotal*100/(contaminatedMonthlyTotal + recycledMonthlyTotal);
                        tvContaminationRate.setText(String.valueOf(contaminationRate) + "%");

                        // ewaste weight
                        int eWasteWeight = totalWeightWeekOne + totalWeightWeekTwo + totalWeightWeekThree + totalWeightWeekFour;
                        tvEwasteWeight.setText(String.valueOf(eWasteWeight));

                        tvConsumerElecWeight.setText(String.valueOf(consumerElecWeightMonthly));
                        tvConsumerElecPerc.setText(String.valueOf((consumerElecWeightMonthly*100)/eWasteWeight));

                        tvComputerTeleWeight.setText(String.valueOf(computerTeleWeightMonthly));
                        tvComputerTelePerc.setText(String.valueOf((computerTeleWeightMonthly*100)/eWasteWeight));

                        tvSmallAppWeight.setText(String.valueOf(smallAppWeightMonthly));
                        tvSmallAppPerc.setText(String.valueOf((smallAppWeightMonthly*100)/eWasteWeight));

                        tvLightingDevWeight.setText(String.valueOf(lightingDevWeightMonthly));
                        tvLightingDevPerc.setText(String.valueOf((lightingDevWeightMonthly*100)/eWasteWeight));

                        tvOtherWeight.setText(String.valueOf(otherWeightMonthly));
                        tvOtherPerc.setText(String.valueOf((otherWeightMonthly*100)/eWasteWeight));

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
                    xAxisLine.setEnabled(true);
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
         * Set up navigation view
         */
        navigationView = findViewById(R.id.nav_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.home) {
                    Intent intent = new Intent(BinAnalysisActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(BinAnalysisActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(BinAnalysisActivity.this, ProfileActivity.class);
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