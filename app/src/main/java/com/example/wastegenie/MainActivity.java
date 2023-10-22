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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    NavigationView navigationView;

    RecyclerView recyclerView;
    DatabaseReference database;
    HomeAdapter adapter;
    ArrayList<BinData> list;

    Spinner councilSpinner;
    Spinner truckSpinner;
    CheckBox cbContamination;
    CheckBox cbRecycle;

    // declare for barchart stuff
    BarChart barChart;
    DatabaseReference databaseBarChart;
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
    int[] colorClassArray = new int[]{Color.BLUE, Color.CYAN};

    ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
    BarData barData;

    // for waste breakdown table
    TextView tvElecWeight, tvElecPercent;
    TextView tvCompWeight, tvCompPercent;
    TextView tvAppWeight, tvAppPercent;
    TextView tvDevWeight, tvDevPercent;
    TextView tvOtherWeight, tvOtherPercent;
    DatabaseReference databaseTable;
    double ElecWeight;
    double CompWeight;
    double AppWeight;
    double DevWeight;
    double OtherWeight;
    double TotalEwasteWeight;

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
         * Setting up the graphs on the home screen
         */
        barChart = findViewById(R.id.bcWeeklyBreakdown);

        databaseBarChart = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");

        databaseBarChart.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                ArrayList<BarEntry> dataVals = new ArrayList<>();

                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);

                        // find the number of recycled bins for latest week (Mon 23rd Oct - Sunday 29th Oct)
                        // MONDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-23"))  {
                            recycableCountMon = recycableCountMon + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-23")) {
                            contaminatedCountMon = contaminatedCountMon + 1;

                        }

                        // TUESDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-24"))  {
                            recycableCountTues = recycableCountTues + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-24")) {
                            contaminatedCountTues = contaminatedCountTues + 1;

                        }

                        // WEDNESDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-25"))  {
                            recycableCountWed = recycableCountWed + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-25")) {
                            contaminatedCountWed = contaminatedCountWed + 1;

                        }

                        // THURSDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-26"))  {
                            recycableCountThurs = recycableCountThurs + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-26")) {
                            contaminatedCountThurs = contaminatedCountThurs + 1;

                        }

                        // FRIDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-27"))  {
                            recycableCountFri = recycableCountFri + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-27")) {
                            contaminatedCountFri = contaminatedCountFri + 1;

                        }

                        // SATURDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-28"))  {
                            recycableCountSat = recycableCountSat + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-28")) {
                            contaminatedCountSat = contaminatedCountSat + 1;


                        }

                        // SUNDAY:
                        if (binData.getStatus().equals("Bin Fully Recyclable ") && (binData.getDate()).split("T")[0].equals("2023-10-29"))  {
                            recycableCountSun = recycableCountSun + 1;
                        } else if (binData.getStatus().equals("Bin Flagged as Contaminated") && (binData.getDate()).split("T")[0].equals("2023-10-29")) {
                            contaminatedCountSun = contaminatedCountSun + 1;


                        }


                    }
                    /**
                     * Set bar chart values
                     */
                    Log.i("Recyclable countMon", String.valueOf(recycableCountMon));

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

                }
            }
        });

        /**
         * Setting up waste breakdown table
         */
        tvElecPercent = findViewById(R.id.tvElecPercent);
        tvElecWeight = findViewById(R.id.tvElecWeight);
        tvCompPercent = findViewById(R.id.tvComputerPercent);
        tvCompWeight = findViewById(R.id.tvComputerWeight);
        tvAppPercent = findViewById(R.id.tvAppliancePercent);
        tvAppWeight = findViewById(R.id.tvApplianceWeight);
        tvDevPercent = findViewById(R.id.tvLightingPercent);
        tvDevWeight = findViewById(R.id.tvLightingWeight);
        tvOtherPercent = findViewById(R.id.tvOtherPercent);
        tvOtherWeight = findViewById(R.id.tvOtherWeight);

        // connecting firebase data
        databaseTable = FirebaseDatabase.getInstance().getReference().child("1qHYUHw1GGaVy9oW_pT8LMAWjR9fODaJE1qWqhcSNHBs").child("Sheet1");
        databaseTable.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        BinData binData = dataSnapshot.getValue(BinData.class);

                        // find all data that has dates between Mon 23rd Oct - Sunday 29th Oct
                        if ((binData.getDate()).split("T")[0].equals("2023-10-23")
                            || (binData.getDate()).split("T")[0].equals("2023-10-24")
                            || (binData.getDate()).split("T")[0].equals("2023-10-25")
                            || (binData.getDate()).split("T")[0].equals("2023-10-26")
                            || (binData.getDate()).split("T")[0].equals("2023-10-27")
                            || (binData.getDate()).split("T")[0].equals("2023-10-28")
                            || (binData.getDate()).split("T")[0].equals("2023-10-29")) {



                                ElecWeight = ElecWeight + (binData.geteWasteWeightKilos() * ((double)binData.getConsumerElectronicsPercent()/100));
                                CompWeight = CompWeight + (binData.geteWasteWeightKilos() * ((double)binData.getComputerTelecomPercent()/100));
                                AppWeight = AppWeight + (binData.geteWasteWeightKilos() * ((double)binData.getSmallAppliancesPercent()/100));
                                DevWeight = DevWeight + (binData.geteWasteWeightKilos() * ((double)binData.getLightingDevicesPercent()/100));
                                OtherWeight = OtherWeight + (binData.geteWasteWeightKilos() * ((double)binData.getOtherPercent()/100));

                                TotalEwasteWeight = TotalEwasteWeight + binData.geteWasteWeightKilos();

                        }
                    }

                    DecimalFormat df = new DecimalFormat("0.00");

                    tvElecWeight.setText(String.valueOf(df.format(ElecWeight)));
                    tvCompWeight.setText(String.valueOf(df.format(CompWeight)));
                    tvAppWeight.setText(String.valueOf(df.format(AppWeight)));
                    tvDevWeight.setText(String.valueOf(df.format(DevWeight)));
                    tvOtherWeight.setText(String.valueOf(df.format(OtherWeight)));

                    double elecPercent = (ElecWeight/TotalEwasteWeight) * 100;
                    double compPercent = (CompWeight/TotalEwasteWeight) * 100;
                    double appPercent = (AppWeight/TotalEwasteWeight) * 100;
                    double devPercent = (DevWeight/TotalEwasteWeight) * 100;
                    double otherPercent = (OtherWeight/TotalEwasteWeight) * 100;

                    tvElecPercent.setText(String.valueOf(df.format(elecPercent)));
                    tvCompPercent.setText(String.valueOf(df.format(compPercent)));
                    tvAppPercent.setText(String.valueOf(df.format(appPercent)));
                    tvDevPercent.setText(String.valueOf(df.format(devPercent)));
                    tvOtherPercent.setText(String.valueOf(df.format(otherPercent)));

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