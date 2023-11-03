package com.example.wastegenie.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastegenie.Analysis.RecyclerViewDropOffClickListener;
import com.example.wastegenie.Analysis.RecyclerViewResourceClickListener;
import com.example.wastegenie.DataModels.DropOffData;
import com.example.wastegenie.R;

import java.util.ArrayList;

/**
 * This is the adapter for the home screen recyclerview showing the live tracking
 */

public class BinAnalysisAdapter extends RecyclerView.Adapter<BinAnalysisAdapter.MyViewHolder> implements Filterable{

    Context context;
    ArrayList<DropOffData> list;
    ArrayList<DropOffData> fullList;

    private RecyclerViewDropOffClickListener localListener;

    public BinAnalysisAdapter(Context context, ArrayList<DropOffData> list, RecyclerViewDropOffClickListener listener) {
        this.context = context;
        this.fullList = list;
        this.list = new ArrayList<>(fullList);
        localListener = listener;
    }

    @Override
    public Filter getFilter() {
        return dropOffFilter;
    }

    private final Filter dropOffFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DropOffData> filteredDropOff = new ArrayList<>();

            for (DropOffData dropOffData : fullList) {
                if (dropOffData.getCouncilName().contains(constraint))
                    filteredDropOff.add(dropOffData);
            }

            FilterResults results = new FilterResults();
            results.values = filteredDropOff;
            results.count = filteredDropOff.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvSiteName, tvSiteLocation, tvSiteDistance;

        public MyViewHolder(@NonNull View itemView, RecyclerViewDropOffClickListener localListener) {
            super(itemView);

            tvSiteName = itemView.findViewById(R.id.tvDropOffName);
            tvSiteLocation = itemView.findViewById(R.id.tvDropOffLocation);
            tvSiteDistance = itemView.findViewById(R.id.tvDropOffDistance);

            /**
             * Implement setOnClickListener
             */
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (localListener != null) {
                        localListener.onRowClickDropOff((String) itemView.getTag());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_council_analysis_tracking, parent, false);
        return new MyViewHolder(view, localListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DropOffData dropOffData = list.get(position);

        holder.tvSiteName.setText(dropOffData.getDropOffName());
        holder.tvSiteLocation.setText(dropOffData.getLocation());
        holder.tvSiteDistance.setText(String.valueOf(dropOffData.getDistanceKm()) + " Km");

        holder.itemView.setTag(dropOffData.getDropOffName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
