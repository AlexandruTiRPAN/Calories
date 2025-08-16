package com.example.calories;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {DailyMacros.class, MacroEntry.class}, // added new table
        version = 2
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DailyMacrosDao dailyMacrosDao();
    public abstract MacroEntryDao macroEntryDao();
}
