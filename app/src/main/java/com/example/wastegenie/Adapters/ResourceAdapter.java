package com.example.wastegenie.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastegenie.R;
import com.example.wastegenie.RecyclerViewResourceClickListener;
import com.example.wastegenie.ResourceData;

import java.util.ArrayList;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.MyViewHolder> {

    private ArrayList<ResourceData> localDataSet;
    private RecyclerViewResourceClickListener localListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvName;
        private TextView tvSource;

        public MyViewHolder(@NonNull View itemView, RecyclerViewResourceClickListener localListener) {
            super(itemView);

            /**
             * Declare and link variables
             */
            ivImage = itemView.findViewById(R.id.ivArticleImage);
            tvName = itemView.findViewById(R.id.tvArticleTitle);
            tvSource = itemView.findViewById(R.id.tvArticleSource);
            /**
             * Implement setOnClickListener
             */
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (localListener != null) {
                        localListener.onRowClick((String) itemView.getTag());
                    }
                }
            });
        }
    }

    /**
     * Constructor method to initialise local dataset
     */
    public ResourceAdapter(ArrayList<ResourceData> dataSet,
                                     RecyclerViewResourceClickListener listener) {
        localDataSet = dataSet;
        localListener = listener;
    }

    /**
     * Method to create new views
     */
    @NonNull
    @Override
    public ResourceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_resources,
                parent, false);
        return new MyViewHolder(view, localListener);

    }

    /**
     * Method to replace the contents of the view with the elements of the learning topics
     */
    @Override
    public void onBindViewHolder(@NonNull ResourceAdapter.MyViewHolder holder,
                                 int position) {
        ResourceData resources = localDataSet.get(position);

        holder.ivImage.setImageResource(resources.getImage());
        holder.tvName.setText(resources.getName());
        holder.tvSource.setText(resources.getSource());

        holder.itemView.setTag(resources.getName());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }






}
