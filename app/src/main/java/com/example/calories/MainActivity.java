package com.example.calories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    DailyMacrosDao dao;
    MacroEntryDao macro_dao;
    String today;

    LinearLayout additionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "nutrition-db").fallbackToDestructiveMigration().build();
        dao = db.dailyMacrosDao();
        macro_dao = db.macroEntryDao();
        today = getIntent().getStringExtra("today");
        if (today == null) {
            today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        additionsContainer = findViewById(R.id.additions_container);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        DatabaseExecutor.diskIO().execute(() -> {
            handle_dates(today);
            runOnUiThread(() -> showDate(today));
        });
    }

    private void updateDate(String date, int protein, int carbs, int fat, int calories) {
        DatabaseExecutor.diskIO().execute(() -> {
            DailyMacros existing = dao.getByDate(date);

            if (existing != null) {
                existing.protein = existing.protein+protein;
                existing.carbs = existing.carbs+carbs;
                existing.fat = existing.fat+fat;
                existing.calories = existing.calories+calories;
                dao.update(existing);
            }
        });
    }

    private void showDate(String date){
        DatabaseExecutor.diskIO().execute(() -> {
            DailyMacros existing = dao.getByDate(date);

            if (existing != null) {
                runOnUiThread(() -> {
                    ((TextView) findViewById(R.id.date)).setText(existing.date);
                    ((TextView) findViewById(R.id.protein_value)).setText(String.valueOf(existing.protein));
                    ((TextView) findViewById(R.id.carbs_value)).setText(String.valueOf(existing.carbs));
                    ((TextView) findViewById(R.id.fats_value)).setText(String.valueOf(existing.fat));
                    ((TextView) findViewById(R.id.calories_value)).setText(String.valueOf(existing.calories));
                });
                List<MacroEntry> todayEntrys = macro_dao.getEntriesForDay(existing.id);
                if (todayEntrys != null && !todayEntrys.isEmpty()) {
                    runOnUiThread(() -> {
                        additionsContainer.removeAllViews();
                        for (MacroEntry entry : todayEntrys) {
                            View additionEntry = getLayoutInflater().inflate(R.layout.addition_entry, additionsContainer, false);

                            ((TextView) additionEntry.findViewById(R.id.protein_entry_value)).setText(String.valueOf(entry.protein));
                            ((TextView) additionEntry.findViewById(R.id.carbs_entry_value)).setText(String.valueOf(entry.carbs));
                            ((TextView) additionEntry.findViewById(R.id.fat_entry_value)).setText(String.valueOf(entry.fat));
                            ((TextView) additionEntry.findViewById(R.id.calories_entry_value)).setText(String.valueOf(entry.calories));

                            FloatingActionButton removeBtn = additionEntry.findViewById(R.id.remove_entry);
                            removeBtn.setOnClickListener(view -> handle_remove_entry(additionEntry, entry));

                            additionsContainer.addView(additionEntry);
                        }
                    });
                }
            }
        });
    }

    private void addDate(String date) {
        DatabaseExecutor.diskIO().execute(() -> {
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
        });
    }

    public void showPopUp(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View popupView = getLayoutInflater().inflate(R.layout.add_macros_pop_up, null);
        builder.setView(popupView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button closeBtn = popupView.findViewById(R.id.close_button);
        Button saveBtn = popupView.findViewById(R.id.save_button);
        closeBtn.setOnClickListener(view -> dialog.dismiss());
        saveBtn.setOnClickListener(view -> {
            handle_save_macros(popupView);
            dialog.dismiss();
        });
    }

    public void handle_save_macros(View popupView){
        EditText proteinInput = popupView.findViewById(R.id.add_protein);
        EditText carbsInput = popupView.findViewById(R.id.add_carbs);
        EditText fatInput = popupView.findViewById(R.id.add_fat);

        String proteinStr = proteinInput.getText().toString();
        String carbsStr = carbsInput.getText().toString();
        String fatStr = fatInput.getText().toString();

        int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);
        int carbs = carbsStr.isEmpty() ? 0 : Integer.parseInt(carbsStr);
        int fat = fatStr.isEmpty() ? 0 : Integer.parseInt(fatStr);
        int calories=4*protein+4*carbs+9*fat;

        updateDate(today,protein, carbs, fat, calories);
        addEntry(today,protein, carbs, fat, calories);
        showDate(today);
    }

    public void addEntry(String date, int protein, int carbs, int fat, int calories){
        DatabaseExecutor.diskIO().execute(() -> {
            DailyMacros existing = dao.getByDate(date);

            if (existing != null) {
                MacroEntry entry= new MacroEntry();
                entry.day_id=existing.id;
                entry.protein=protein;
                entry.carbs=carbs;
                entry.fat=fat;
                entry.calories=calories;
                macro_dao.insert(entry);
            }
        });
    }

    public void handle_dates(String today){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate todayDate = LocalDate.parse(today, formatter);

        DailyMacros latest = dao.getLatest();
        if (latest == null) {
            addDate(today);
            return;
        }

        LocalDate latestDate = LocalDate.parse(latest.date, formatter);

        for (LocalDate date = latestDate; !date.isAfter(todayDate); date = date.plusDays(1)) {
            addDate(date.format(formatter));
        }
    }
    public void handle_remove_entry(View additionEntry, MacroEntry entry){
        DatabaseExecutor.diskIO().execute(() -> {
            macro_dao.deleteById(entry.entryId);
        });
        additionsContainer.removeView(additionEntry);
        updateDate(today, -entry.protein,-entry.carbs,-entry.fat,-entry.calories);
        showDate(today);
    }
    public void handle_home(View v) {}

    public void handle_add_meal(View v) {
        Intent i = new Intent(this, AddMealActivity.class);
        startActivity(i);
    }

    public void handle_logs(View v) {
        Intent i = new Intent(this, LogsActivity.class);
        i.putExtra("today", today);
        startActivity(i);
    }
}