package com.inaxdevelopers.monitorbatterystatus.acitvites;

import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.database.DatabaseHelper;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityChartsBinding;
import com.inaxdevelopers.monitorbatterystatus.model.HistoryModel;
import com.inaxdevelopers.monitorbatterystatus.utils.Constants;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ChartsActivity extends AppCompatActivity {

    ActivityChartsBinding binding;
    String[] reportWise = {"All Data", "Today", "Last Month", "Last Week", "Last Year"};
    String reportDate = "All Data";
    ArrayList<HistoryModel> batteryHistory;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseHelper = new DatabaseHelper(this);
        SetSpinner();
        bindHistoryData();
        BatteryLevelChart();
    }

    private void SetSpinner() {
        binding.spReportFormats.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reportWise));
        binding.spReportFormats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (reportWise[position].equals("All Data")) {
                    reportDate = "All Data";
                } else {
                    reportDate = getBeforeFiftyDaysDate(reportWise[position]);
                }
                bindHistoryData();
            }
        });
    }

    private void BatteryLevelChart() {
        binding.chartBatteryLevel.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight h) {
                int x = (int) entry.getX();
                binding.tvNote.setText("Level Chart" + entry.getY() + " %\nTime " + batteryHistory.get(x).getStartEndDate() + Constants.STRING_EMPTY + batteryHistory.get(x).getStartEndTime());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        binding.chartBatteryVoltage.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight h) {
                int x = (int) entry.getX();
                binding.tvNote.setText("Battery Voltage " + entry.getY() + " V\nTime " + batteryHistory.get(x).getStartEndDate() + Constants.STRING_EMPTY + batteryHistory.get(x).getStartEndTime());

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    public String getBeforeFiftyDaysDate(String str) {
        Calendar calendar = Calendar.getInstance();
        if (str.equals("Last Year")) {
            calendar.add(6, -364);
        } else if (str.equals("Last Month")) {
            calendar.add(6, -14);
        } else if (str.equals("Last Week")) {
            calendar.add(6, -6);
        } else if (str.equals("Today")) {
            calendar.add(6, -1);
        }
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }
    private void bindHistoryData() {
        batteryHistory = new ArrayList<>();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("HHHH", "Date  " + reportDate);
                    batteryHistory = databaseHelper.getHistoryLevel(reportDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (batteryHistory.size() == 0) {
                            binding.tvNoDataLevel.setVisibility(View.VISIBLE);
                            binding.tvNoDataVoltage.setVisibility(View.VISIBLE);
                            binding.chartBatteryLevel.setVisibility(View.GONE);
                            binding.chartBatteryVoltage.setVisibility(View.GONE);
                        } else {
                            binding.chartBatteryLevel.setVisibility(View.VISIBLE);
                            binding.chartBatteryVoltage.setVisibility(View.VISIBLE);
                            binding.tvNoDataLevel.setVisibility(View.GONE);
                            binding.tvNoDataVoltage.setVisibility(View.GONE);
                            bindBatteryChart();
                        }
                        Log.e("Done", "Done");
                        Log.e("Complete Process", "Complete Process");

                    }
                });
            }
        });
    }

    private void bindBatteryChart() {
        if (binding.chartBatteryLevel.getData() != null && binding.chartBatteryVoltage.getData() != null) {
            binding.chartBatteryLevel.clearValues();
            binding.chartBatteryVoltage.clearValues();
        }
        setChartView();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < this.batteryHistory.size(); i++) {
            float f = i;
            arrayList.add(new Entry(f, Float.valueOf(this.batteryHistory.get(i).getLevel()).floatValue()));
            arrayList2.add(new Entry(f, this.batteryHistory.get(i).getVoltage()));
        }
        if (binding.chartBatteryLevel.getData() != null && (binding.chartBatteryLevel.getData()).getDataSetCount() > 0) {
            ((LineDataSet) (binding.chartBatteryLevel.getData()).getDataSetByIndex(0)).setValues(arrayList);
            (binding.chartBatteryLevel.getData()).notifyDataChanged();
            binding.chartBatteryLevel.notifyDataSetChanged();
        } else {
            LineDataSet lineDataSet = new LineDataSet(arrayList, "Level Chart");
            lineDataSet.setDrawCircles(true);
            lineDataSet.enableDashedLine(10.0f, 0.0f, 0.0f);
            lineDataSet.enableDashedHighlightLine(10.0f, 0.0f, 0.0f);
            lineDataSet.setColor(getResources().getColor(R.color.colorGreen));
            lineDataSet.setCircleColor(getResources().getColor(R.color.colorChartGreen));
            lineDataSet.setLineWidth(3.0f);
            lineDataSet.setCircleRadius(6.0f);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setValueTextSize(8.0f);
            lineDataSet.setDrawValues(true);
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float f2) {
                    return MyUtils.formatNumber(f2) + " %";
                }
            });
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFormLineWidth(5.0f);
            lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10.0f, 5.0f}, 0.0f));
            lineDataSet.setFormSize(5.0f);
            lineDataSet.setFillColor(-1);
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(lineDataSet);
            binding.chartBatteryLevel.setData(new LineData(arrayList3));
            binding.chartBatteryLevel.notifyDataSetChanged();
            binding.chartBatteryLevel.invalidate();
        }
        if (binding.chartBatteryVoltage.getData() != null && ((LineData) binding.chartBatteryVoltage.getData()).getDataSetCount() > 0) {
            ((LineDataSet) (binding.chartBatteryVoltage.getData()).getDataSetByIndex(0)).setValues(arrayList2);
            (binding.chartBatteryVoltage.getData()).notifyDataChanged();
            binding.chartBatteryVoltage.notifyDataSetChanged();
            return;
        }
        LineDataSet lineDataSet2 = new LineDataSet(arrayList2, "Voltage Chart");
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.enableDashedLine(10.0f, 0.0f, 0.0f);
        lineDataSet2.enableDashedHighlightLine(10.0f, 0.0f, 0.0f);
        lineDataSet2.setColor(getResources().getColor(R.color.colorGreen));
        lineDataSet2.setCircleColor(getResources().getColor(R.color.colorChartGreen));
        lineDataSet2.setLineWidth(3.0f);
        lineDataSet2.setCircleRadius(6.0f);
        lineDataSet2.setDrawCircleHole(false);
        lineDataSet2.setValueTextSize(8.0f);
        lineDataSet2.setDrawValues(true);
        lineDataSet2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float f2) {
                return f2 + " V";
            }
        });
        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setFormLineWidth(5.0f);
        lineDataSet2.setFormLineDashEffect(new DashPathEffect(new float[]{10.0f, 5.0f}, 0.0f));
        lineDataSet2.setFormSize(5.0f);
        lineDataSet2.setFillColor(-1);
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(lineDataSet2);
        binding.chartBatteryVoltage.setData(new LineData(arrayList4));
        binding.chartBatteryVoltage.notifyDataSetChanged();
        binding.chartBatteryVoltage.invalidate();
    }

    public void setChartView() {
        binding.chartBatteryLevel.getAxisLeft().setDrawGridLines(false);
        binding.chartBatteryLevel.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float f) {
                return MyUtils.formatNumber(f) + " %";
            }
        });
        binding.chartBatteryLevel.getAxisRight().setDrawGridLines(false);
        binding.chartBatteryLevel.getAxisRight().setDrawLabels(false);
        XAxis xAxis = binding.chartBatteryLevel.getXAxis();
        YAxis axisLeft = binding.chartBatteryLevel.getAxisLeft();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.enableGridDashedLine(2.0f, 7.0f, 0.0f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1.0f);
        axisLeft.setAxisMinimum(0.0f);
        axisLeft.enableGridDashedLine(10.0f, 10.0f, 0.0f);
        axisLeft.setDrawZeroLine(false);
        axisLeft.setDrawLimitLinesBehindData(false);
        binding.chartBatteryLevel.getLegend().setEnabled(false);
        binding.chartBatteryLevel.getDescription().setEnabled(false);
        binding.chartBatteryLevel.moveViewToX(1.0f);
        binding.chartBatteryLevel.setVisibleXRangeMaximum(5.0f);
        binding.chartBatteryVoltage.getAxisLeft().setDrawGridLines(false);
        binding.chartBatteryVoltage.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float f) {
                return f + " V";
            }
        });
        binding.chartBatteryVoltage.getAxisRight().setDrawGridLines(false);
        binding.chartBatteryVoltage.getAxisRight().setDrawLabels(false);
        XAxis xAxis2 = binding.chartBatteryVoltage.getXAxis();
        YAxis axisLeft2 = binding.chartBatteryVoltage.getAxisLeft();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.enableGridDashedLine(2.0f, 7.0f, 0.0f);
        xAxis2.setDrawGridLines(false);
        xAxis2.setGranularity(1.0f);
        axisLeft2.setAxisMinimum(0.0f);
        axisLeft2.enableGridDashedLine(10.0f, 10.0f, 0.0f);
        axisLeft2.setDrawZeroLine(false);
        axisLeft2.setDrawLimitLinesBehindData(false);
        binding.chartBatteryVoltage.moveViewToX(1.0f);
        binding.chartBatteryVoltage.setVisibleXRangeMaximum(5.0f);
        binding.chartBatteryVoltage.getLegend().setEnabled(false);
        binding.chartBatteryVoltage.getDescription().setEnabled(false);
    }


}