package com.inaxdevelopers.monitorbatterystatus.acitvites;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityBatteryFullAlertBinding;

public class BatteryFullAlertActivity extends AppCompatActivity {

    ActivityBatteryFullAlertBinding binding;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBatteryFullAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        binding.btnStop.setOnClickListener(v -> {
            mediaPlayer.stop();
            finish();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}