package com.example.calories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DailyMacrosDao {

    @Insert
    void insert(DailyMacros dailyMacros);

    @Update
    void update(DailyMacros dailyMacros);

    @Query("SELECT * FROM DailyMacros WHERE date = :date LIMIT 1")
    DailyMacros getByDate(String date);

    @Query("SELECT * FROM DailyMacros ORDER BY date DESC")
    List<DailyMacros> getAll();
}
