package com.example.wastegenie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wastegenie.Adapters.AnalysisAdapter;
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

public class AnalysisActivity extends AppCompatActivity {

    NavigationView navigationView;

    // for statistics
    TextView tvRecycleScore, tvTotalBins, tvContaminatedBins, tvTotalWeight;
    int totalRecycledBins;
    int totalBins;
    int totalContaminatedBins;
    int totalWeightAll;
    DatabaseReference databaseStats;

    // for bar graph
    Button btWeekly;
    Button btMonthly;
    BarChart barChart;
    DatabaseReference databaseChart;
    int[] colorClassArray = new int[]{Color.BLUE, Color.CYAN};
    ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
    BarData barData;
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
    int totalWeightMon;
    int totalWeightTues;
    int totalWeightWed;
    int totalWeightThurs;
    int totalWeightFri;
    int totalWeightSat;
    int totalWeightSun;


    // for spinners
    Spinner spSpecificCouncil;
    Spinner spCouncilForBins;
    Spinner spSpecificBins;

    Button btCouncilAnalysis;
    Button btBinAnalysis;

    // for recyclerview
    RecyclerView recyclerView;
    DatabaseReference databaseRecyclerView;
    AnalysisAdapter adapter;
    ArrayList<BinData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        setTitle("Analysis Page");

        /**
         * Set up statistics
         */
        tvRecycleScore = findViewById(R.id.tvDashboardRecyclingScore);
        tvTotalBins = findViewById(R.id.tvDashboardTotalBins);
        tvContaminatedBins = findViewById(R.id.tvDashboardContaminated);
        tvTotalWeight = findViewById(R.id.tvDashboardWeight);

        // connecting firebase data
        databaseStats = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        databaseStats.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);

                        if (binData.getStatus().equals("Bin Fully Recyclable ")) {
                            totalRecycledBins = totalRecycledBins + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated")) {
                            totalContaminatedBins = totalContaminatedBins + 1;
                            totalWeightAll = totalWeightAll + binData.geteWasteWeightKilos();
                        } else {
                            totalBins = totalBins + 1;
                        }

                    }

                    tvRecycleScore.setText(String.valueOf((totalRecycledBins*100/totalBins) + "%"));
                    tvContaminatedBins.setText(String.valueOf(totalContaminatedBins));
                    tvTotalWeight.setText(String.valueOf(totalWeightAll));
                    tvTotalBins.setText(String.valueOf(totalBins));

                    if ((totalRecycledBins * 100)/totalBins >= 50) {
                        tvRecycleScore.setTextColor(Color.GREEN);
                    } else {
                        tvRecycleScore.setTextColor(Color.RED);
                    }

                }

            }
        });

        /**
         * Set up graphs
         */
        barChart = findViewById(R.id.bcDashboardWeekly);
        lineChart = findViewById(R.id.lcDashboardWeekly);

        databaseChart = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        databaseChart.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                ArrayList<BarEntry> dataVals = new ArrayList<>();
                ArrayList<Entry> lineDataVals = new ArrayList<Entry>();

                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);

                        // calculate total bins contaminated vs not contaminated in the latest week (Mon 23rd Oct - Sunday 29th Oct)
                        // MONDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-23"))  {
                            recycableCountMon = recycableCountMon + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-23")) {
                            contaminatedCountMon = contaminatedCountMon + 1;

                            totalWeightMon = totalWeightMon + binData.geteWasteWeightKilos();
                        }

                        // TUESDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-24"))  {
                            recycableCountTues = recycableCountTues + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-24")) {
                            contaminatedCountTues = contaminatedCountTues + 1;

                            totalWeightTues = totalWeightTues + binData.geteWasteWeightKilos();
                        }

                        // WEDNESDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-25"))  {
                            recycableCountWed = recycableCountWed + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-25")) {
                            contaminatedCountWed = contaminatedCountWed + 1;

                            totalWeightWed = totalWeightWed + binData.geteWasteWeightKilos();
                        }

                        // THURSDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-26"))  {
                            recycableCountThurs = recycableCountThurs + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-26")) {
                            contaminatedCountThurs = contaminatedCountThurs + 1;

                            totalWeightThurs = totalWeightThurs + binData.geteWasteWeightKilos();
                        }

                        // FRIDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-27"))  {
                            recycableCountFri = recycableCountFri + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-27")) {
                            contaminatedCountFri = contaminatedCountFri + 1;

                            totalWeightFri = totalWeightFri + binData.geteWasteWeightKilos();
                        }

                        // SATURDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-28"))  {
                            recycableCountSat = recycableCountSat + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-28")) {
                            contaminatedCountSat = contaminatedCountSat + 1;

                            totalWeightSat = totalWeightSat + binData.geteWasteWeightKilos();
                        }

                        // SUNDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-29"))  {
                            recycableCountSun = recycableCountSun + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-29")) {
                            contaminatedCountSun = contaminatedCountSun + 1;

                            totalWeightSun = totalWeightSun + binData.geteWasteWeightKilos();
                        }


                    }
                    /**
                     * Set bar chart values
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
                }
            }
        });

        /**
         * Setting up the spinner for councils
         */
        spSpecificCouncil = findViewById(R.id.spDashboardSpecificCouncil);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> councilAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.councilArray,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        councilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spSpecificCouncil.setAdapter(councilAdapter);

        // when user clicks "view council analysis" button
        btCouncilAnalysis = findViewById(R.id.btViewCouncilAnalysis);
        btCouncilAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String councilChoice = spSpecificCouncil.getSelectedItem().toString();
                Intent intent = new Intent(AnalysisActivity.this, CouncilAnalysisActivity.class);
                intent.putExtra("Council Selection", councilChoice);
                startActivity(intent);
            }
        });

        /**
         * Setting up corresponding spinners for the council's SPECIFIC trucks depending on what
         * council the user chose
         */
        spSpecificBins = findViewById(R.id.SpDashboardBinName);
        spCouncilForBins = findViewById(R.id.spCouncilforBins);

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> councilForBinsAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.councilArray,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        councilForBinsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spCouncilForBins.setAdapter(councilForBinsAdapter);

        spCouncilForBins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if user selects parramatta council
                if (spCouncilForBins.getSelectedItem().equals("Parramatta")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            AnalysisActivity.this,
                            R.array.parramattaBinsArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSpecificBins.setAdapter(truckAdapter);

                    // if user selects sydney council
                } else if (spCouncilForBins.getSelectedItem().equals("City of Sydney")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            AnalysisActivity.this,
                            R.array.sydneyBinsArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSpecificBins.setAdapter(truckAdapter);
                } else if (spCouncilForBins.getSelectedItem().equals("Ku-ring-gai")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            AnalysisActivity.this,
                            R.array.kuRingGaiBinsArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSpecificBins.setAdapter(truckAdapter);
                } else if (spCouncilForBins.getSelectedItem().equals("Burwood")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            AnalysisActivity.this,
                            R.array.burwoodBinsArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSpecificBins.setAdapter(truckAdapter);
                } else if (spCouncilForBins.getSelectedItem().equals("Hornsby")) {
                    ArrayAdapter<CharSequence> truckAdapter = ArrayAdapter.createFromResource(
                            AnalysisActivity.this,
                            R.array.hornsbyBinsArray,
                            android.R.layout.simple_spinner_item
                    );
                    truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSpecificBins.setAdapter(truckAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btBinAnalysis = findViewById(R.id.btViewBinAnalysis);
        btBinAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String binSelection = spSpecificBins.getSelectedItem().toString();
                Intent intent = new Intent(AnalysisActivity.this, BinAnalysisActivity.class);
                intent.putExtra("Bin Selection", binSelection);
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
                    Intent intent = new Intent(AnalysisActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.analysis) {
                    return true;
                } else if (id == R.id.tracking) {
                    Intent intent = new Intent(AnalysisActivity.this, TrackingActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (id == R.id.profile) {
                    Intent intent = new Intent(AnalysisActivity.this, ProfileActivity.class);
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