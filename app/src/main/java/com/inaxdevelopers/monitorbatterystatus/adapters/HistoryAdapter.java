package com.inaxdevelopers.monitorbatterystatus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.database.DatabaseHelper;
import com.inaxdevelopers.monitorbatterystatus.databinding.ItemHistoryBinding;
import com.inaxdevelopers.monitorbatterystatus.model.HistoryModel;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    ItemHistoryBinding binding;
    Context context;
    ArrayList<HistoryModel> list;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        binding.tvDate.setText("" + list.get(position).getStartEndDate());
        binding.tvTime.setText("" + list.get(position).getStartEndTime());
        binding.tvPlugged.setText("" + list.get(position).getPlugged());
        binding.tvHealth.setText("" + list.get(position).getHealth());
        binding.tvStatus.setText("" + list.get(position).getStatus());
        binding.tvBatteryLevel.setText("" + list.get(position).getLevel() + " %");
        binding.tvBattVol.setText("" + list.get(position).getVoltage());
        final DatabaseHelper databaseHelper = new DatabaseHelper(context);
        binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.deleteHistory(list.get(position).getHistoryId());
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding binding;

        public ViewHolder(@NonNull ItemHistoryBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
