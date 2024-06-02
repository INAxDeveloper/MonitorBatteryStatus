package com.inaxdevelopers.monitorbatterystatus;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inaxdevelopers.monitorbatterystatus.acitvites.BatteryInfoActivity;
import com.inaxdevelopers.monitorbatterystatus.acitvites.BatteryUsageActivity;
import com.inaxdevelopers.monitorbatterystatus.acitvites.ChargeHistoryActivity;
import com.inaxdevelopers.monitorbatterystatus.acitvites.ChartsActivity;
import com.inaxdevelopers.monitorbatterystatus.acitvites.SettingsActivity;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityMainBinding;
import com.inaxdevelopers.monitorbatterystatus.services.BatteryService;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;

import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static String INSTALL_PREF = "install_pref";
    private Context mContext;
    String plugged;
    FirstReceiver firstReceiver;
    BroadcastReceiver batteryBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                int intExtra = intent.getIntExtra("level", -1);
                Log.e("TAG", "onReceive: " + intExtra);
                plugged = MyUtils.getPlugTypeString(intent.getIntExtra("plugged", -1));
                setPercentCard(intExtra);
                binding.tvBatteryPercentages.setText("Charging : " + intExtra + " %");
                binding.tvBatteryPercentages.setText("" + intExtra + " %");
                if (intExtra == 100) {
                    binding.tvTimeRemain.setVisibility(View.GONE);
                    binding.vvLine.setVisibility(View.GONE);
                } else {
                    if (plugged.equals("Unknown")) {
                        binding.tvTimeRemain.setVisibility(View.GONE);
                        binding.vvLine.setVisibility(View.GONE);
                    } else {
                        binding.tvTimeRemain.setVisibility(View.VISIBLE);
                        binding.vvLine.setVisibility(View.VISIBLE);
                    }
                    int i = (100 - intExtra) * 2;
                    int i2 = i / 60;
                    set_values(intExtra, i2, i - (i2 * 60), 2);
                }
            }
            if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")) {
                binding.tvTimeRemain.setVisibility(View.VISIBLE);
                binding.vvLine.setVisibility(View.VISIBLE);
            } else if (intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
                binding.tvTimeRemain.setVisibility(View.GONE);
                binding.vvLine.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
            }
        }
        this.mContext = this;
        IDClick();
//        binding.toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_white));
        registerReceiver(batteryBroadcast, new IntentFilter("android.intent.action.ACTION_POWER_DISCONNECTED"));
        registerReceiver(batteryBroadcast, new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED"));
        registerReceiver(batteryBroadcast, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        IntentFilter intentFilter = new IntentFilter("ACTION_CLOSE");
        firstReceiver = new FirstReceiver();
        registerReceiver(firstReceiver, intentFilter);
        if (checkNewInstall()) {
            return;
        }
    }

    private boolean checkNewInstall() {
        Context context = this.mContext;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);
        boolean z = sharedPreferences.getBoolean(INSTALL_PREF, false);
        if (!z) {
            sharedPreferences.edit().putBoolean(INSTALL_PREF, true).commit();
        }
        return z;
    }


    private void IDClick() {
        binding.btnBatteryInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, BatteryInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            Log.e("TAG", "IDClick: ");
        });
        binding.btnAppBatteryConsume.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, BatteryUsageActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
        });
        binding.btnCharts.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, ChartsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
        });

        binding.btnHistoryCalendar.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, HistoryCalendarActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
        });
        binding.btnChargeHistory.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, ChargeHistoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
        });
        binding.btnSettings.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
        });
    }

    class FirstReceiver extends BroadcastReceiver {
        FirstReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("FirstReceiver", "FirstReceiver");
            if (intent.getAction().equals("ACTION_CLOSE")) {
                finish();
            }
        }
    }

    public void setPercentCard(int i) {
        if (i >= 0 && i <= 10) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_10);
        } else if (i > 10 && i <= 20) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_20);
        } else if (i > 20 && i <= 30) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_30);
        } else if (i > 30 && i <= 40) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_40);
        } else if (i > 40 && i <= 50) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_50);
        } else if (i > 50 && i <= 60) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_60);
        } else if (i > 60 && i <= 70) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_70);
        } else if (i > 70 && i <= 80) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_80);
        } else if (i > 80 && i <= 99) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_90);
        } else if (i == 100) {
            binding.ivBatteryIcon.setImageResource(R.drawable.battery_100);
        }
    }

    public void set_values(int i, int i2, int i3, int i4) {
        String str;
        if (i2 != 0) {
            try {
                str = "" + i2 + " hour ";
            } catch (Exception unused) {
                return;
            }
        } else {
            str = "";
        }
        if (i3 != 0) {
            str = str + i3 + " min ";
        }
        binding.tvTimeRemain.setText("" + str + "until full battery charge.");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyUtils.getIsSERVICE_KEY(this) || MyUtils.isMyServiceRunning(BatteryService.class, this)) {
            return;
        }
        Intent intent = new Intent(this, BatteryService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        if (batteryBroadcast != null) {
            unregisterReceiver(batteryBroadcast);
        }
        unregisterReceiver(firstReceiver);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ExitActivity.class));
    }

}