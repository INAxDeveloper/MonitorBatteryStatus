package com.inaxdevelopers.monitorbatterystatus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.inaxdevelopers.monitorbatterystatus.acitvites.BatteryUsageActivity;
import com.inaxdevelopers.monitorbatterystatus.acitvites.IgnoreActivity;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.IgnoreItem;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.db.DbIgnoreExecutor;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.AppUtil;
import com.inaxdevelopers.monitorbatterystatus.databinding.ItemIgnoreBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IgnoreAdapter extends RecyclerView.Adapter<IgnoreAdapter.ViewHolder> {

    Context context;
    List<IgnoreItem> list;
    ItemIgnoreBinding binding;


    public IgnoreAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemIgnoreBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IgnoreItem item = list.get(position);
        binding.appDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(item.mCreated)));
        binding.appTime.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(item.mCreated)));
        binding.appName.setText(item.mName);
        Glide.with(context).load(AppUtil.getPackageIcon(context, item.mPackageName)).transition(new DrawableTransitionOptions().crossFade()).into(binding.appImage);
        binding.appDate.setOnClickListener(v -> {
            DbIgnoreExecutor.getInstance().deleteItem(item);
            new IgnoreActivity.MyAsyncTask(context).execute(new Void[0]);

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<IgnoreItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemIgnoreBinding binding;

        public ViewHolder(@NonNull ItemIgnoreBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
