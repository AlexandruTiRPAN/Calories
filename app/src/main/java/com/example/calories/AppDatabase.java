package com.example.calories;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {DailyMacros.class, MacroEntry.class, Meal.class}, // added new table
        version = 3
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DailyMacrosDao dailyMacrosDao();
    public abstract MacroEntryDao macroEntryDao();
    public abstract MealDao mealDao();
}
