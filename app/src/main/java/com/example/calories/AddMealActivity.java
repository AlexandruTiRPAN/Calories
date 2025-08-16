package com.example.calories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.gridlayout.widget.GridLayout;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AddMealActivity extends AppCompatActivity {

    String today;
    AppDatabase db;
    MealDao meals_dao;
    DailyMacrosDao dao;
    MacroEntryDao macro_dao;
    GridLayout meals_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_meal);
        today = getIntent().getStringExtra("today");
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "nutrition-db").fallbackToDestructiveMigration().build();
        meals_dao=db.mealDao();
        dao=db.dailyMacrosDao();
        macro_dao=db.macroEntryDao();
        meals_grid=findViewById(R.id.meals_grid);
//        DatabaseExecutor.diskIO().execute(() -> {
//            Meal meal= new Meal();
//            meal.image="image";
//            meal.category="protein";
//            meal.name="caltite";
//            meal.protein=0;
//            meal.carbs=0;
//            meal.fat=0;
//            meals_dao.insert(meal);
//        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showPopUp(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddMealActivity.this);
        View popupView = getLayoutInflater().inflate(R.layout.custom_meal_pop_up, null);
        builder.setView(popupView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button closeBtn = popupView.findViewById(R.id.close_button);
        Button saveBtn = popupView.findViewById(R.id.save_button);
        closeBtn.setOnClickListener(view -> dialog.dismiss());
        saveBtn.setOnClickListener(view -> {
            handle_add_new_meal(popupView);
            dialog.dismiss();
        });
    }
    public void handle_add_new_meal(View popupView){
        EditText proteinInput = popupView.findViewById(R.id.meal_protein);
        EditText carbsInput = popupView.findViewById(R.id.meal_carbs);
        EditText fatInput = popupView.findViewById(R.id.meal_fat);
        EditText nameInput = popupView.findViewById(R.id.meal_name);

        String proteinStr = proteinInput.getText().toString();
        String carbsStr = carbsInput.getText().toString();
        String fatStr = fatInput.getText().toString();
        String nameStr = nameInput.getText().toString();

        int protein = proteinStr.isEmpty() ? 0 : Integer.parseInt(proteinStr);
        int carbs = carbsStr.isEmpty() ? 0 : Integer.parseInt(carbsStr);
        int fat = fatStr.isEmpty() ? 0 : Integer.parseInt(fatStr);
        int calories=4*protein+4*carbs+9*fat;

        DatabaseExecutor.diskIO().execute(()->{
            Meal new_meal = new Meal();
            new_meal.name=nameStr;
            new_meal.category="custom";
            new_meal.image="default_image";
            new_meal.protein=protein;
            new_meal.carbs=carbs;
            new_meal.fat=fat;
            new_meal.calories=calories;
            meals_dao.insert(new_meal);
            handle_show_meals("custom");
        });
    }

    public void handle_show_protein(View v){
        handle_show_meals("protein");
    }

    public void handle_show_carbs(View v){
        handle_show_meals("carbs");
    }

    public void handle_show_fats(View v){
        handle_show_meals("fats");
    }

    public void handle_show_custom(View v){
        handle_show_meals("custom");
    }
    public void handle_show_meals(String category){
        DatabaseExecutor.diskIO().execute(() -> {
            List<Meal> meals;
            switch (category){
                case "protein":
                    meals=meals_dao.selectByCategory("protein");
                    runOnUiThread(()->{
                        ((TextView) findViewById(R.id.meal_type)).setText("Protein meals");
                        findViewById(R.id.meal_type).setVisibility(View.VISIBLE);
                    });
                    break;
                case "carbs":
                    meals=meals_dao.selectByCategory("carbs");
                    runOnUiThread(()->{
                        ((TextView) findViewById(R.id.meal_type)).setText("Carbs meals");
                        findViewById(R.id.meal_type).setVisibility(View.VISIBLE);
                    });
                    break;
                case "fats":
                    meals=meals_dao.selectByCategory("fats");
                    runOnUiThread(()->{
                        ((TextView) findViewById(R.id.meal_type)).setText("Fats meals");
                        findViewById(R.id.meal_type).setVisibility(View.VISIBLE);
                    });
                    break;
                case "custom":
                    meals=meals_dao.selectByCategory("custom");
                    runOnUiThread(()->{
                        ((TextView) findViewById(R.id.meal_type)).setText("Custom meals");
                        findViewById(R.id.meal_type).setVisibility(View.VISIBLE);
                    });
                    break;
                default:
                    meals=null;
                    break;
            }
            if(meals!=null ){
                runOnUiThread(()->{
                    meals_grid.removeAllViews();
                    if(!meals.isEmpty()) {
                        for (Meal meal : meals) {
                            View meal_cell = getLayoutInflater().inflate(R.layout.meal_grid_cell, meals_grid, false);

                            ((TextView) meal_cell.findViewById(R.id.meal_name)).setText(meal.name);
                            meal_cell.setTag(meal.id);
                            ImageView imageView = meal_cell.findViewById(R.id.meal_image);
                            String my_image = meal.image;
                            int imageId = getResources().getIdentifier(my_image, "drawable", getPackageName());
                            imageView.setImageResource(imageId);

                            meal_cell.setOnClickListener(view -> {
                                handle_meal_clicked(meal);
                            });

                            meals_grid.addView(meal_cell);
                        }
                    }
                });
            }
        });

    }

    public void handle_meal_clicked(Meal meal) {
        if (meal != null) {
            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMealActivity.this);
                View popupView = getLayoutInflater().inflate(R.layout.add_meal_pop_up, null);
                builder.setView(popupView);

                ((TextView) popupView.findViewById(R.id.meal_name_text)).setText(meal.name);
                ((TextView) popupView.findViewById(R.id.protein_text)).setText(String.valueOf(meal.protein));
                ((TextView) popupView.findViewById(R.id.carbs_text)).setText(String.valueOf(meal.carbs));
                ((TextView) popupView.findViewById(R.id.fat_text)).setText(String.valueOf(meal.fat));
                ((TextView) popupView.findViewById(R.id.calories_text)).setText(String.valueOf(meal.calories));

                AlertDialog dialog = builder.create();
                dialog.show();

                Button closeBtn = popupView.findViewById(R.id.close_button);
                Button saveBtn = popupView.findViewById(R.id.save_button);
                closeBtn.setOnClickListener(view -> dialog.dismiss());
                saveBtn.setOnClickListener(view -> {
                    handle_add_meal_today(popupView, meal);
                    dialog.dismiss();
                });
            });
        }
    }


    public void handle_add_meal_today(View v, Meal meal){
        DatabaseExecutor.diskIO().execute(()->{
            DailyMacros existing = dao.getByDate(today);
            if(existing!=null){
                MacroEntry entry=new MacroEntry(meal, existing.id);
                macro_dao.insert(entry);

                existing.protein = existing.protein+meal.protein;
                existing.carbs = existing.carbs+meal.carbs;
                existing.fat = existing.fat+meal.fat;
                existing.calories = existing.calories+meal.calories;
                dao.update(existing);
                }

        });
    }
    public void handle_home(View v) {
        runOnUiThread(()->{
            findViewById(R.id.meal_type).setVisibility(View.INVISIBLE);
        });
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("today", today);
        startActivity(i);
    }

    public void handle_add_meal(View v) {}

    public void handle_logs(View v) {
        runOnUiThread(()->{
            findViewById(R.id.meal_type).setVisibility(View.INVISIBLE);
        });
        Intent i = new Intent(this, LogsActivity.class);
        i.putExtra("today", today);
        startActivity(i);
    }

}