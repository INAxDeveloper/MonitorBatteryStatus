package com.inaxdevelopers.monitorbatterystatus.acitvites;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.AppItem;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.DataManager;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.service.AlarmService;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.service.AppService;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.AppUtil;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityBatteryUsageBinding;
import com.inaxdevelopers.monitorbatterystatus.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class BatteryUsageActivity extends AppCompatActivity {

    ActivityBatteryUsageBinding binding;
    private int mDay;
    private MyAdapter mAdapter;

    private PackageManager mPackageManager;
    private long mTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBatteryUsageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more_black));
        mPackageManager = getPackageManager();
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter();
        binding.list.setAdapter(mAdapter);
        initLayout();
        initEvents();
        initSpinner();
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            process();
            startService(new Intent(this, AlarmService.class));
        }
    }

    private void initLayout() {
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            binding.enableText.setText(R.string.enable_apps_monitoring);
            binding.enableSwitch.setVisibility(View.GONE);
            binding.swipeRefresh.setEnabled(true);
            return;
        }
        binding.enableText.setText(R.string.enable_apps_monitor);
        binding.enableSwitch.setVisibility(View.VISIBLE);
        binding.enableSwitch.setChecked(false);
        binding.swipeRefresh.setEnabled(false);

    }

    private void initSpinner() {
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            binding.rlSpinner.setVisibility(View.VISIBLE);
            binding.tvNote.setVisibility(View.VISIBLE);
            binding.spinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.duration, 17367049));
            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    if (mDay != i) {
                        int[] intArray = getResources().getIntArray(R.array.duration_int);
                        mDay = intArray[i];
                        process();
                    }
                }
            });
        }
    }

    private void initEvents() {
        if (!DataManager.getInstance().hasPermission(getApplicationContext())) {
            binding.enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Intent intent = new Intent(BatteryUsageActivity.this, AppService.class);
                        intent.putExtra(AppService.SERVICE_ACTION, AppService.SERVICE_ACTION_CHECK);
                        BatteryUsageActivity.this.startService(intent);
                    }

                }
            });
        }
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                process();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            return;
        }
        binding.enableSwitch.setChecked(false);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (DataManager.getInstance().hasPermission(this)) {
            binding.swipeRefresh.setEnabled(true);
            binding.enableSwitch.setVisibility(View.GONE);
            initSpinner();
            process();
        }
    }


    public void process() {
        if (DataManager.getInstance().hasPermission(getApplicationContext())) {
            binding.list.setVisibility(View.INVISIBLE);
            new MyAsyncTask().execute(3, Integer.valueOf(this.mDay));
        }
    }

    public  class MyAsyncTask extends AsyncTask<Integer, Void, List<AppItem>> {
        MyAsyncTask() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            binding.swipeRefresh.setRefreshing(true);
        }

        @Override
        public List<AppItem> doInBackground(Integer... numArr) {
            return DataManager.getInstance().getApps(BatteryUsageActivity.this.getApplicationContext(), numArr[0].intValue(), numArr[1].intValue());
        }

        @Override
        public void onPostExecute(List<AppItem> list) {


            binding.list.setVisibility(View.VISIBLE);
            BatteryUsageActivity.this.mTotal = 0L;
            Iterator<AppItem> it = list.iterator();
            while (true) {
                if (it.hasNext()) {
                    AppItem next = it.next();
                    if (next.mUsageTime > 0) {
                        BatteryUsageActivity.this.mTotal += next.mUsageTime;
                        next.mCanOpen = BatteryUsageActivity.this.mPackageManager.getLaunchIntentForPackage(next.mPackageName) != null;
                    }
                } else {
                    binding.enableText.setText(String.format(BatteryUsageActivity.this.getResources().getString(R.string.total), AppUtil.formatMilliSeconds(BatteryUsageActivity.this.mTotal)));
                    binding.swipeRefresh.setRefreshing(false);
                    mAdapter.updateData(list);
                    return;
                }
            }
        }

    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<AppItem> mData = new ArrayList();

        MyAdapter() {
        }

        public void updateData(List<AppItem> list) {
            this.mData = list;
            notifyDataSetChanged();
        }

        public AppItem getItemInfoByPosition(int i) {
            if (mData.size() > i) {
                return mData.get(i);
            }
            return null;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            AppItem itemInfoByPosition = getItemInfoByPosition(i);
            myViewHolder.mName.setText(itemInfoByPosition.mName);
            myViewHolder.mUsage.setText(AppUtil.formatMilliSeconds(itemInfoByPosition.mUsageTime));
            String formatMilliSeconds = AppUtil.formatMilliSeconds(itemInfoByPosition.mUsageTime);
            Log.e("time12345", "" + formatMilliSeconds);
            String[] split = formatMilliSeconds.split(Constants.STRING_EMPTY);
            if (split.length == 3) {
                myViewHolder.app_val.setText("Usage: HIGH");
                myViewHolder.app_val.setTextColor(Color.parseColor("#FF0000"));
            } else if (split.length == 2) {
                if (Integer.parseInt(split[0].replace("m", "")) > 20) {
                    myViewHolder.app_val.setText("Usage: MEDIUM");
                    myViewHolder.app_val.setTextColor(Color.parseColor("#FFCD43"));
                } else {
                    myViewHolder.app_val.setText("Usage: LOW");
                    myViewHolder.app_val.setTextColor(Color.parseColor("#1EBB4E"));
                }
            } else if (split.length == 1) {
                myViewHolder.app_val.setText("Usage: LOW");
                myViewHolder.app_val.setTextColor(Color.parseColor("#1EBB4E"));
            }
            myViewHolder.mTime.setText(String.format(Locale.getDefault(), "%s · %d %s", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(itemInfoByPosition.mEventTime)), Integer.valueOf(itemInfoByPosition.mCount), BatteryUsageActivity.this.getResources().getString(R.string.times_only)));
            if (BatteryUsageActivity.this.mTotal > 0) {
                myViewHolder.mProgress.setProgress((int) ((itemInfoByPosition.mUsageTime * 100) / BatteryUsageActivity.this.mTotal));
            } else {
                myViewHolder.mProgress.setProgress(0);
            }
            Glide.with((FragmentActivity) BatteryUsageActivity.this).load(AppUtil.getPackageIcon(BatteryUsageActivity.this, itemInfoByPosition.mPackageName)).diskCacheStrategy(DiskCacheStrategy.ALL).transition(new DrawableTransitionOptions().crossFade()).into(myViewHolder.mIcon);
        }

        @Override
        public int getItemCount() {
            return this.mData.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView app_val;
            private ImageView mIcon;
            private TextView mName;
            private ProgressBar mProgress;
            private TextView mTime;
            private TextView mUsage;

            MyViewHolder(View view) {
                super(view);
                this.mName = (TextView) view.findViewById(R.id.app_name);
                this.mUsage = (TextView) view.findViewById(R.id.app_usage);
                this.mTime = (TextView) view.findViewById(R.id.app_time);
                this.app_val = (TextView) view.findViewById(R.id.app_val);
                this.mIcon = (ImageView) view.findViewById(R.id.app_image);
                this.mProgress = (ProgressBar) view.findViewById(R.id.progressBar);
//                view.setOnCreateContextMenuListener(this);
            }

//            @Override
//            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//                int adapterPosition = getAdapterPosition();
//                contextMenu.setHeaderTitle(MyAdapter.this.getItemInfoByPosition(adapterPosition).mName);
//                contextMenu.add(0, R.id.ignore, adapterPosition, BatteryUsageActivity.this.getResources().getString(R.string.ignore));
//            }
        }
    }

}