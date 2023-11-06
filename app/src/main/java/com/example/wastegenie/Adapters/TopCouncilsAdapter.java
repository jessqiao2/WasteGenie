package com.example.wastegenie.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastegenie.DataModels.CouncilMCs;
import com.example.wastegenie.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopCouncilsAdapter extends RecyclerView.Adapter<TopCouncilsAdapter.MyViewHolder> {
    private ArrayList<CouncilMCs> councilData;

    public TopCouncilsAdapter(ArrayList councilData){
        this.councilData = councilData;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvCouncilName, tvMonthlyCount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCouncilName = itemView.findViewById(R.id.tvCouncilName);
            tvMonthlyCount = itemView.findViewById(R.id.tvMonthlyCount);
        }
    }

    @NonNull
    @Override
    public TopCouncilsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_monthly_top_councils, parent, false);
        return new TopCouncilsAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TopCouncilsAdapter.MyViewHolder holder, int position) {
        holder.tvCouncilName.setText(councilData.get(position).getCouncilName());
        holder.tvMonthlyCount.setText(String.valueOf(councilData.get(position).getMonthlyContams()));

    }

    @Override
    public int getItemCount() {
        return councilData.size();
    }

    public void sortList(){
        if(councilData.size() > 0){
            Collections.sort(councilData, new Comparator<CouncilMCs>() {
                @Override
                public int compare(CouncilMCs c1, CouncilMCs c2) {
                    return c2.getMonthlyContams().compareTo(c1.getMonthlyContams());
                }
            });
            notifyDataSetChanged();
        }
    }

}
