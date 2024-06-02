package com.inaxdevelopers.monitorbatterystatus.acitvites;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.adapters.IgnoreAdapter;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.IgnoreItem;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.db.DbIgnoreExecutor;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.AppUtil;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityIgnoreBinding;

import java.lang.ref.WeakReference;
import java.util.List;

public class IgnoreActivity extends AppCompatActivity {

    public static IgnoreAdapter mAdapter;

    ActivityIgnoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityIgnoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IgnoreAdapter(this);
        binding.list.setAdapter(mAdapter);
        new MyAsyncTask(this).execute(new Void[0]);
    }

    public static class MyAsyncTask extends AsyncTask<Void, Void, List<IgnoreItem>> {
        private WeakReference<Context> mContext;

        public MyAsyncTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        public List<IgnoreItem> doInBackground(Void... voidArr) {
            return DbIgnoreExecutor.getInstance().getAllItems();
        }

        @Override
        public void onPostExecute(List<IgnoreItem> list) {
            if (this.mContext.get() == null || list.size() <= 0) {
                return;
            }
            for (IgnoreItem ignoreItem : list) {
                ignoreItem.mName = AppUtil.parsePackageName(this.mContext.get().getPackageManager(), ignoreItem.mPackageName);
            }
            mAdapter.setData(list);
        }
    }
}