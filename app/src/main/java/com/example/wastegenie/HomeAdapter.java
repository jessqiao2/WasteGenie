package com.example.wastegenie;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This is the adapter for the home screen recyclerview showing the live tracking
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    Context context;
    ArrayList<BinData> list;

    public HomeAdapter(Context context, ArrayList<BinData> list) {
        this.context = context;
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTime, tvDateTime, tvLocation, tvStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDateTime = itemView.findViewById(R.id.tvTrackDateTime);
            tvLocation = itemView.findViewById(R.id.tvTrackLocation);
            tvStatus = itemView.findViewById(R.id.tvTrackStatus);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_home_tracking, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BinData binData = list.get(position);

        // change date from ISO 8601 format to standard year and time format
        String timeStampFromDatabase = binData.getDateTime();
        Instant instant = Instant.parse(timeStampFromDatabase);

        // Convert it to ZonedDateTime using the system's default time zone
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        // Format the date-time with AM/PM indicator
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        String formattedDateTime = zonedDateTime.format(formatter);

        holder.tvDateTime.setText(formattedDateTime);
        holder.tvLocation.setText(binData.getBinName());
        holder.tvStatus.setText(binData.getStatus());

        // change colour of status depending on what it says - contains is used instead of equal
        // because some of the data might have empty spaces following the text
        if (binData.getStatus().contains("Bin Picked Up")) {
            holder.tvStatus.setTextColor(Color.GRAY);
        } else if (binData.getStatus().contains("Bin Fully Recyclable")) {
            holder.tvStatus.setTextColor(Color.GREEN);
        } else {
            holder.tvStatus.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
