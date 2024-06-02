package com.inaxdevelopers.monitorbatterystatus.acitvites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.PreferenceManager;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivitySettingsBinding;
import com.inaxdevelopers.monitorbatterystatus.services.BatteryService;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SetCheckbox();
        SetClick();
        restoreStatus();
    }

    private void SetCheckbox() {
        binding.settingsStopService.setChecked(MyUtils.getIsSERVICE_KEY(this));
        binding.settingsBatteryFull.setChecked(MyUtils.getBatteryFullAlert(this));
        binding.settingsBatteryFull.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!MyUtils.getIsSERVICE_KEY(this)) {
                    MyUtils.setBatteryFullAlert(this, true);
                }
                MyUtils.showMessage(this, "Please enable battery monitoring service first.");
                binding.settingsStopService.setChecked(false);
                return;
            }
            MyUtils.setBatteryFullAlert(this, false);
        });
        binding.settingsStopService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MyUtils.setIsSERVICE_KEY(this, isChecked);
            if (isChecked) {
                if (MyUtils.isMyServiceRunning(BatteryService.class, this)) {
                    stopService(new Intent(this, BatteryService.class));
                }
                if (!MyUtils.getBatteryFullAlert(this)) {
                    binding.settingsBatteryFull.setChecked(false);
                    MyUtils.setBatteryFullAlert(this, false);
                }
            } else {
                Intent intent = new Intent(this, BatteryService.class);
                if (Build.VERSION.SDK_INT >= 26) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        });

    }

    private void SetClick() {
        binding.switchSystemApps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (PreferenceManager.getInstance().getSystemSettings(PreferenceManager.PREF_SETTINGS_HIDE_SYSTEM_APPS) != isChecked) {
                PreferenceManager.getInstance().putBoolean(PreferenceManager.PREF_SETTINGS_HIDE_SYSTEM_APPS, isChecked);
            }
        });
        binding.groupSystem.setOnClickListener(v -> {
            binding.switchSystemApps.performClick();
        });
        binding.switchUninstallAppps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (PreferenceManager.getInstance().getUninstallSettings(PreferenceManager.PREF_SETTINGS_HIDE_UNINSTALL_APPS) != isChecked) {
                PreferenceManager.getInstance().putBoolean(PreferenceManager.PREF_SETTINGS_HIDE_UNINSTALL_APPS, isChecked);
            }
        });
        binding.groupUninstall.setOnClickListener(v -> {
            binding.switchUninstallAppps.performClick();
        });
        binding.groupIgnore.setOnClickListener(v -> {
            startActivity(new Intent(this, IgnoreActivity.class));
        });
    }


    private void restoreStatus() {
        binding.switchSystemApps.setChecked(PreferenceManager.getInstance().getSystemSettings(PreferenceManager.PREF_SETTINGS_HIDE_SYSTEM_APPS));
        binding.switchUninstallAppps.setChecked(PreferenceManager.getInstance().getUninstallSettings(PreferenceManager.PREF_SETTINGS_HIDE_UNINSTALL_APPS));

    }
}