package com.example.wastegenie.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastegenie.BinData;
import com.example.wastegenie.R;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> implements Filterable {
    ArrayList<BinData> list;
    ArrayList<BinData> contaminatedList;
    ArrayList<BinData> fullList;
    Context context;
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvDateTime, tvLocation, tvStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDateTime = itemView.findViewById(R.id.tvTrackDateTime);
            tvLocation = itemView.findViewById(R.id.tvTrackLocation);
            tvStatus = itemView.findViewById(R.id.tvTrackStatus);

        }
    }

    public RouteAdapter(Context context, ArrayList<BinData> list){
        this.context = context;
        //this.list = list;
        this.fullList = list;
        this.contaminatedList = list;
        this.list = new ArrayList<>(fullList);
    }

    @NonNull
    @Override
    public RouteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_route_tracking, parent, false);
        return new RouteAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteAdapter.MyViewHolder holder, int position){
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
        holder.itemView.setTag(binData.getId());

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
    public int getItemCount(){
        return(list.size());
    }

    @Override
    public Filter getFilter() {
        return truckFilter;
    }

    private final Filter truckFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence truckFilter) {

            ArrayList<BinData> filteredTruckList = new ArrayList<>();

            // if the user doesn't choose a selection, show the full list of the tracking status
            // for all trucks
            if (truckFilter == null || truckFilter.length() == 0) {
                filteredTruckList.addAll(fullList);
            } else {
                // loop through all the data within the list to find those which
                // contain the filtered truck id
                for (BinData bindata : fullList) {
                    if (bindata.getTruckId().equals(truckFilter))
                        filteredTruckList.add(bindata);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredTruckList;
            results.count = filteredTruckList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };


    public Filter getContaminationFilter() {
        return contaminationFilter;
    }

    private final Filter contaminationFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence contaminationFilter) {
            ArrayList<BinData> filteredTruckList = new ArrayList<>();

            for (BinData bindata : list) {
                if (bindata.getStatus().equals(contaminationFilter))
                    filteredTruckList.add(bindata);
            }

            FilterResults results = new FilterResults();
            results.values = filteredTruckList;
            results.count = filteredTruckList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

}