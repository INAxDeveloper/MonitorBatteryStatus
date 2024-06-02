package com.inaxdevelopers.monitorbatterystatus.acitvites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityBatteryInfoBinding;
import com.inaxdevelopers.monitorbatterystatus.utils.Constants;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;
import com.inaxdevelopers.monitorbatterystatus.utils.SaveSharedPreferences;

import java.util.ArrayList;

public class BatteryInfoActivity extends AppCompatActivity {

    ActivityBatteryInfoBinding binding;
    String estimate = "";
    String estimate_head = "";
    String estimate_percent = "";
    String estimate_percent_head = "";

    Handler handler = new Handler();
    int round = 0;
    int sround = 0;
    float Average_COUNT = 0.0f;
    float Average_STAND_COUNT = 0.0f;
    String BATTERY_CHANGE_FILTER = "android.intent.action.BATTERY_CHANGED";
    int COUNT = 0;
    int STATUS = 1;
    ArrayList<Float> average_mah_values = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBatteryInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_black));
        registerReceiver(this.battery_stats_broadcast_receiver, new IntentFilter(BATTERY_CHANGE_FILTER));
        new SaveSharedPreferences(this).increaseCount(new SaveSharedPreferences(this).getCount() + 1);
        handler.postDelayed(current_voltage_runnable, Constants.USAGE_TIME_MIX);
        binding.batteryStatCapacityTxt.setText(Math.round(getBatteryCapacity()) + " mAh");

    }

    private final BroadcastReceiver battery_stats_broadcast_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("level", 0);
            int intExtra2 = intent.getIntExtra("scale", 100);
            boolean booleanExtra = intent.getBooleanExtra("present", false);
            Bundle extras = intent.getExtras();
            if (!booleanExtra || extras == null) {
                binding.batteryStatVoltTxt.setText("-");
                binding.batteryStatLevelTxt.setText("-");
                binding.batteryStatStatusTxt.setText("-");
                binding.batteryStatPlugTxt.setText("-");
                binding.batteryStatTechTxt.setText("-");
                binding.batteryStatHealthTxt.setText("-");
                return;
            }
            int i = (intExtra * 100) / intExtra2;
            binding.batteryStatTechTxt.setText(extras.getString("technology"));
            binding.batteryStatVoltTxt.setText(extras.getInt("voltage") / 1000.0f + "V");
            binding.batteryStatHealthTxt.setText(MyUtils.getHealthString(extras.getInt("health")));
            setCurrentCounter(extras, i);
            setStatusCardAndToolBar(extras);
            setPlugCard(extras);
            setPercentCard(i);
        }
    };

    private Runnable current_voltage_runnable = new Runnable() {
        @Override
        public void run() {
            float current = getCurrent(STATUS);
            saveMaxAps(current);
            float maxCapacity = new SaveSharedPreferences(BatteryInfoActivity.this).getMaxCapacity();
            float f = (current / maxCapacity) * 100.0f;
            float f2 = 0.0f;
            if (f < 0.0f) {
                f *= -1.0f;
            }
            int i = 0;
            if (current > 0.0f) {
                saveChargingAmps(current);
                if (COUNT < maxCapacity) {
                    if (Average_COUNT == 0.0f) {
                        Average_STAND_COUNT = 0.0f;
                        average_mah_values.clear();
                        estimate = "";
                        estimate_head = "Calculating estimated charging time...";
                        estimate_percent = "";
                        estimate_percent_head = "Calculating charging speed...";
                    }
                    average_mah_values.add(Float.valueOf(current));
                    if (average_mah_values.size() == 10) {
                        average_mah_values.remove(0);
                    }
                    int i2 = round + 1;
                    round = i2;
                    if (i2 > 9) {
                        while (i < average_mah_values.size()) {
                            if (i < 10) {
                                f2 += average_mah_values.get(i).floatValue();
                            }
                            i++;
                        }
                        float size = f2 / average_mah_values.size();
                        Average_COUNT = size;
                        int timeRemaining = getTimeRemaining(size, COUNT, maxCapacity);
                        estimate = (timeRemaining / 60) + "hrs " + (timeRemaining % 60) + "mins";
                        estimate_head = "Estimated charging time : ";
                        StringBuilder sb = new StringBuilder();
                        sb.append(Math.round(f));
                        sb.append("% per hour");
                        estimate_percent = sb.toString();
                        estimate_percent_head = "Charging at ";
                    }
                } else {
                    estimate = "Battery fully charged";
                    estimate_head = "";
                    estimate_percent = "";
                    estimate_percent_head = "";
                }
            } else {
                saveDischargingAmps(current);
                if (Average_STAND_COUNT == 0.0f) {
                    Average_COUNT = 0.0f;
                    average_mah_values.clear();
                    estimate = "";
                    estimate_head = "Calculating estimated backup time...";
                    estimate_percent = "";
                    estimate_percent_head = "Calculating discharge speed...";
                }
                average_mah_values.add(Float.valueOf(current * (-1.0f)));
                if (average_mah_values.size() == 10) {
                    average_mah_values.remove(0);
                }
                int i3 = sround + 1;
                sround = i3;
                if (i3 > 9) {
                    while (i < average_mah_values.size()) {
                        f2 += average_mah_values.get(i).floatValue();
                        i++;
                    }
                    float size2 = f2 / average_mah_values.size();
                    Average_STAND_COUNT = size2;
                    int standbyTime = getStandbyTime(size2, COUNT);
                    estimate = (standbyTime / 60) + "hrs " + (standbyTime % 60) + "mins";
                    estimate_head = "Estimated backup time : ";
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(Math.round(f));
                    sb2.append("% per hour");
                    estimate_percent = sb2.toString();
                    estimate_percent_head = "Discharging at ";
                }
            }
            handler.postDelayed(current_voltage_runnable, 1000L);
        }
    };

    public double getBatteryCapacity() {
        Object obj;
        try {
            obj = Class.forName("com.android.internal.os.PowerProfile").getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        try {
            return ((Double) Class.forName("com.android.internal.os.PowerProfile").getMethod("getAveragePower", String.class).invoke(obj, "battery.capacity")).doubleValue();
        } catch (Exception e2) {
            e2.printStackTrace();
            return Double.longBitsToDouble(1L);
        }
    }


    private void setCurrentCounter(Bundle bundle, int i) {
        long round;
        int i2 = bundle.getInt("charge_counter", 0) / 1000;
        this.COUNT = i2;
        try {
            round = (i2 / i) * 100;
        } catch (ArithmeticException e) {
            e.printStackTrace();
            round = Math.round(getBatteryCapacity());
        }
        if (round < getBatteryCapacity()) {
            new SaveSharedPreferences(this).saveMaxCapacity(round);
        } else {
            new SaveSharedPreferences(this).saveMaxCapacity(Math.round(getBatteryCapacity()));
        }
    }


    private void setStatusCardAndToolBar(Bundle bundle) {
        int i = bundle.getInt(NotificationCompat.CATEGORY_STATUS);
        this.STATUS = i;
        binding.batteryStatStatusTxt.setText("Status (" + MyUtils.getStatusString(i) + ")");
    }

    private void setPlugCard(Bundle bundle) {
        String plugTypeString = MyUtils.getPlugTypeString(bundle.getInt("plugged"));
//        battery_stat_plug_txt
        binding.batteryStatPlugTxt.setText(plugTypeString);
        if (plugTypeString.equalsIgnoreCase(Constants.plug_type_ac)) {
            binding.batteryStatImgPlugType.setImageResource(R.drawable.ic_plug_ac);
        } else if (plugTypeString.equalsIgnoreCase(Constants.plug_type_usb)) {
            binding.batteryStatImgPlugType.setImageResource(R.drawable.ic_plug_usb);
        } else if (plugTypeString.equalsIgnoreCase(Constants.plug_type_wireless)) {
            binding.batteryStatImgPlugType.setImageResource(R.drawable.ic_plug_wireless);
        } else if (plugTypeString.equalsIgnoreCase(Constants.label_unknown)) {
            binding.batteryStatImgPlugType.setImageResource(R.drawable.ic_plug_unknown);
        }
    }

    private void setPercentCard(int i) {
        binding.batteryStatLevelTxt.setText(i + "%");
        if (i >= 0 && i <= 10) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_10);
        } else if (i > 10 && i <= 20) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_20);
        } else if (i > 20 && i <= 30) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_30);
        } else if (i > 30 && i <= 40) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_40);
        } else if (i > 40 && i <= 50) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_50);
        } else if (i > 50 && i <= 60) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_60);
        } else if (i > 60 && i <= 70) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_70);
        } else if (i > 70 && i <= 80) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_80);
        } else if (i > 80 && i <= 99) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_90);
        } else if (i == 100) {
            binding.batteryStatImgLevel.setImageResource(R.drawable.battery_100);
        }
    }

    private float getCurrent(int i) {
        try {
            BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            float longProperty = batteryManager != null ? (float) batteryManager.getLongProperty(2) : 0.0f;
            return (longProperty > 6000.0f || longProperty < -6000.0f) ? longProperty / 1000.0f : longProperty;
        } catch (ArithmeticException unused) {
            return 0.0f;
        }
    }

    private int getTimeRemaining(float f, float f2, float f3) {
        return Math.round(((f3 - f2) / f) * 60.0f);
    }

    private int getStandbyTime(float f, int i) {
        return Math.round((i / f) * 60.0f);
    }


    private void saveMaxAps(float f) {
        if (f < 0.0f) {
            f *= -1.0f;
        }
        if (f > roundOffTo1000(new SaveSharedPreferences(this).getMaxChargeAmps())) {
            new SaveSharedPreferences(this).saveMaxChargeAmps(Math.round(f));
        }
    }

    private int roundOffTo1000(int i) {
        if (i > 10000) {
            return 11000;
        }
        if (i > 9000) {
            return 10000;
        }
        if (i > 8000) {
            return 9000;
        }
        if (i > 7000) {
            return 8000;
        }
        if (i > 6000) {
            return 7000;
        }
        if (i > 5000) {
            return 6000;
        }
        if (i > 4000) {
            return 5000;
        }
        if (i > 3000) {
            return 4000;
        }
        if (i > 2000) {
            return 3000;
        }
        return 2000;
    }

    private void saveChargingAmps(float f) {
        float f2 = 0;
        int maxChargeAmps = new SaveSharedPreferences(this).getMaxChargeAmps();
        int minChargeAmps = new SaveSharedPreferences(this).getMinChargeAmps();
        if (f > maxChargeAmps) {
            new SaveSharedPreferences(this).saveMaxChargeAmps(Math.round(f));
        }
        if (f < minChargeAmps) {
            new SaveSharedPreferences(this).saveMinChargeAmps(Math.round(f));
            return;
        }
        Log.e("Amps Text:", "Battery Amps :-" + Math.round(f2) + " mAh");
    }

    private void saveDischargingAmps(float f) {
        float f2 = f * (-1.0f);
        int maxDischargeAmps = new SaveSharedPreferences(this).getMaxDischargeAmps();
        int minDischargeAmps = new SaveSharedPreferences(this).getMinDischargeAmps();
        if (f2 > maxDischargeAmps) {
            new SaveSharedPreferences(this).saveMaxDischargeAmps(Math.round(f2));
        }
        if (f2 < minDischargeAmps) {
            new SaveSharedPreferences(this).saveMinDischargeAmps(Math.round(f2));
        }
    }

}