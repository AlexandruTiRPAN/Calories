package com.example.calories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LogsActivity extends AppCompatActivity {

    AppDatabase db;
    DailyMacrosDao dao;
    String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logs);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "nutrition-db")
                .fallbackToDestructiveMigration()
                .build();
        dao = db.dailyMacrosDao();

        today = getIntent().getStringExtra("today");
        handle_dates();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showDate(DailyMacros date) {
        runOnUiThread(() -> {
            ((TextView) findViewById(R.id.day_counter)).setText(String.valueOf(date.id));
            ((TextView) findViewById(R.id.calories_counter)).setText(String.valueOf(date.calories));
        });
    }

    public void handle_dates() {
        DatabaseExecutor.diskIO().execute(() -> {
            DailyMacros current = dao.getByDate(today);
            if (current != null) {
                showDate(current);
            } else {
                // If today is missing, create it
                addDate(today);
            }
        });
    }

    public void handle_prev(View v) {
        DatabaseExecutor.diskIO().execute(() -> {
            DailyMacros prev = dao.getPrevByDate(today);
            if (prev != null) {
                today = prev.date;
                showDate(prev);
            }
        });
    }


    public void handle_next(View v) {
        DatabaseExecutor.diskIO().execute(() -> {
            DailyMacros next = dao.getNextByDate(today);
            if (next != null) {
                today = next.date;
                showDate(next);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate tomorrow = LocalDate.parse(today, formatter).plusDays(1);
                today = tomorrow.format(formatter);
                addDate(today);
                DailyMacros newDay = dao.getByDate(today);
                if (newDay != null) showDate(newDay);
            }
        });
    }

    private void addDate(String date) {
        DailyMacros existing = dao.getByDate(date);
        if (existing == null) {
            DailyMacros newEntry = new DailyMacros();
            newEntry.date = date;
            newEntry.protein = 0;
            newEntry.carbs = 0;
            newEntry.fat = 0;
            newEntry.calories = 0;
            dao.insert(newEntry);
        }
    }

    public void handle_home(View v) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("today", today);
        startActivity(i);
    }

    public void handle_add_meal(View v) {
        Intent i = new Intent(this, AddMealActivity.class);
        i.putExtra("today", today);
        startActivity(i);
    }

    public void handle_logs(View v) {}
}
