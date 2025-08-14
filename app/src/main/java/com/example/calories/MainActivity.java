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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    DailyMacrosDao dao;
    String today;

    LinearLayout additionsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "nutrition-db").build();
        dao = db.dailyMacrosDao();
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        additionsContainer = findViewById(R.id.additions_container);
        addDate(today);
        showDate(today);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

        View additionEntry= getLayoutInflater().inflate(R.layout.addition_entry, additionsContainer, false);
        TextView proteinValue = additionEntry.findViewById(R.id.protein_entry_value);
        proteinValue.setText(String.valueOf(protein));
        TextView carbsValue= additionEntry.findViewById(R.id.carbs_entry_value);
        carbsValue.setText(String.valueOf(carbs));
        TextView fatValue = additionEntry.findViewById(R.id.fat_entry_value);
        fatValue.setText(String.valueOf(fat));
        TextView caloriesValue= additionEntry.findViewById(R.id.calories_entry_value);
        caloriesValue.setText(String.valueOf(calories));

        additionsContainer.addView(additionEntry);


        FloatingActionButton removeBtn = additionEntry.findViewById(R.id.remove_entry);
        removeBtn.setOnClickListener(view -> {
            handle_remove_entry(additionEntry);
        });


        showDate(today);
    }

    public void handle_remove_entry(View additionEntry){
        TextView proteinValue = additionEntry.findViewById(R.id.protein_entry_value);
        TextView carbsValue= additionEntry.findViewById(R.id.carbs_entry_value);
        TextView fatValue = additionEntry.findViewById(R.id.fat_entry_value);
        TextView caloriesValue= additionEntry.findViewById(R.id.calories_entry_value);
        int protein = Integer.parseInt(proteinValue.getText().toString());
        int carbs =Integer.parseInt(carbsValue.getText().toString());
        int fats = Integer.parseInt(fatValue.getText().toString());
        int calories = Integer.parseInt(caloriesValue.getText().toString());
        updateDate(today, -protein,-carbs,-fats,-calories);
        showDate(today);
        additionsContainer.removeView(additionEntry);
    }
    public void handle_home(View v) {}

    public void handle_add_meal(View v) {
        Intent i = new Intent(this, AddMealActivity.class);
        startActivity(i);
        finish();
    }

    public void handle_logs(View v) {
        Intent i = new Intent(this, LogsActivity.class);
        startActivity(i);
        finish();
    }
}