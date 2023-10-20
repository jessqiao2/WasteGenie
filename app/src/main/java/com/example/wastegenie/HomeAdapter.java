package com.example.wastegenie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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

        TextView tvTime, tvDate, tvLocation, tvStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tvTrackTime);
            tvDate = itemView.findViewById(R.id.tvTrackDate);
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
        holder.tvTime.setText(binData.getTime());
        holder.tvDate.setText(binData.getDate());
        holder.tvLocation.setText(binData.getBinName());
        holder.tvStatus.setText(binData.getStatus());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
