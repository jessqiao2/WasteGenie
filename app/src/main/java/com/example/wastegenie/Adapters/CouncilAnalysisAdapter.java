//package com.example.wastegenie.Adapters;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Filterable;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.wastegenie.BinData;
//import com.example.wastegenie.R;
//
//import java.time.Instant;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//
///**
// * This is the adapter for the home screen recyclerview showing the live tracking
// */
//
//public class CouncilAnalysisAdapter extends RecyclerView.Adapter<CouncilAnalysisAdapter.MyViewHolder> {
//
//    Context context;
//    ArrayList<BinData> list;
//
//    public CouncilAnalysisAdapter(Context context, ArrayList<BinData> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//
//        TextView tvWeeklyCount, tvbinName;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvWeeklyCount = itemView.findViewById(R.id.tvWeeklyCount);
//            tvbinName = itemView.findViewById(R.id.tvBinName);
//        }
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.list_council_analysis_tracking, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        BinData binData = list.get(position);
//
//        for
//
//
//
//        holder.tvWeeklyCount.setText(formattedDateTime);
//        holder.tvbinName.setText(binData.getBinName());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//
//}
