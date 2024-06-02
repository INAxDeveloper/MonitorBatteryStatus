package com.inaxdevelopers.monitorbatterystatus.acitvites;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.adapters.HistoryAdapter;
import com.inaxdevelopers.monitorbatterystatus.database.DatabaseHelper;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityChargeHistoryBinding;
import com.inaxdevelopers.monitorbatterystatus.model.HistoryModel;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class ChargeHistoryActivity extends AppCompatActivity {

    ActivityChargeHistoryBinding binding;
    DatabaseHelper databaseHelper;
    HistoryAdapter historyAdapter;
    ArrayList<HistoryModel> historyModelArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChargeHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseHelper = new DatabaseHelper(this);
        historyModelArrayList = new ArrayList<>();
        binding.rvChargingHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChargingHistory.setHasFixedSize(true);
        historyAdapter = new HistoryAdapter(this, historyModelArrayList);
        binding.rvChargingHistory.setAdapter(historyAdapter);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    historyModelArrayList = databaseHelper.getHistoryData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public  void run() {
                         historyAdapter = new HistoryAdapter(ChargeHistoryActivity.this, historyModelArrayList);
                        binding.rvChargingHistory.setAdapter(historyAdapter);
                        if (historyModelArrayList.size() <= 0) {
                            binding.tvNoHistory.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvNoHistory.setVisibility(View.GONE);
                        }
                        Log.e("Done", "Done");
                        Log.e("Complete Process", "Complete Process");
                    }
                });

            }
        });
    }
}