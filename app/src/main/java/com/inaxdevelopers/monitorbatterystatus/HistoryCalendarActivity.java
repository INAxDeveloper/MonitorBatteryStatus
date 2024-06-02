package com.inaxdevelopers.monitorbatterystatus;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.inaxdevelopers.monitorbatterystatus.adapters.HistoryAdapter;
import com.inaxdevelopers.monitorbatterystatus.database.DatabaseHelper;
import com.inaxdevelopers.monitorbatterystatus.databinding.ActivityHistoryCalendarBinding;
import com.inaxdevelopers.monitorbatterystatus.model.HistoryModel;
import com.inaxdevelopers.monitorbatterystatus.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryCalendarActivity extends AppCompatActivity {

    ActivityHistoryCalendarBinding binding;
    ArrayList<HistoryModel> challengeHistoryModels;
    ArrayList<HistoryModel> challengeHistorySelectedDay;
    DatabaseHelper databaseHelper;
    String[] dayColumn = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    HistoryAdapter historyAdapter;
    public Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseHelper = new DatabaseHelper(this);
        binding.rvToday.setLayoutManager(new LinearLayoutManager(this));
        binding.rvToday.setItemAnimator(new DefaultItemAnimator());
        binding.rvToday.setHasFixedSize(true);
        Date time = Calendar.getInstance().getTime();
        binding.monthText.setText(((String) DateFormat.format("MMMM", time)) + Constants.STRING_EMPTY + ((String) DateFormat.format("yyyy", time)));
        selectedDate = time;
        bindChallengesAccordingDate(time);
        compactCalendarViewSet();
        binding.showPreviousMonthBut.setOnClickListener(v -> {
            binding.compactcalendarView.scrollLeft();
        });
        binding.showNextMonthBut.setOnClickListener(v -> {
            binding.compactcalendarView.scrollRight();
        });
        binding.compactcalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = binding.compactcalendarView.getEvents(dateClicked);
                Log.d("TAG", "Day was clicked: " + dateClicked + " with events " + events);
                selectedDate = dateClicked;
                bindChallengesAccordingDate(selectedDate);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d("TAG", "Month was scrolled to: " + firstDayOfNewMonth);
                binding.monthText.setText((DateFormat.format("MMMM", selectedDate)) + Constants.STRING_EMPTY + ((String) DateFormat.format("yyyy", selectedDate)));
            }
        });
    }

    private void bindChallengesAccordingDate(Date date) {
        ArrayList<HistoryModel> arrayList = challengeHistorySelectedDay;
        if (arrayList != null) {
            arrayList.clear();
        }
        challengeHistorySelectedDay = databaseHelper.getHistoryAccordingDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date));
        historyAdapter = new HistoryAdapter(this, this.challengeHistorySelectedDay);
        binding.rvToday.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
        if (this.challengeHistorySelectedDay.size() > 0) {
            binding.tvNoData.setVisibility(View.GONE);
        } else {
            binding.tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void compactCalendarViewSet() {
        binding.compactcalendarView.setFirstDayOfWeek(1);
        binding.compactcalendarView.setCurrentDate(selectedDate);
        binding.compactcalendarView.setDayColumnNames(dayColumn);
        for (int i = 0; i < this.challengeHistoryModels.size(); i++) {
            binding.compactcalendarView.addEvent(new Event(Color.parseColor("#7D87F6"), getMilli(challengeHistoryModels.get(i).getStartEndDate()), challengeHistoryModels.get(i).getLevel()));
        }

    }

    private long getMilli(String startEndDate) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(startEndDate).getTime();
        } catch (Exception unused) {
            return 0L;
        }
    }
}