package com.example.calories;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DailyMacros.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DailyMacrosDao dailyMacrosDao();
}
