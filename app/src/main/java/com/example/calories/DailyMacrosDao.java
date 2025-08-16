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

    @Query("SELECT * FROM DailyMacros WHERE id = :id LIMIT 1")
    DailyMacros getById(int id);

    @Query("SELECT * FROM DailyMacros ORDER BY date DESC")
    List<DailyMacros> getAll();

    @Query("SELECT * FROM DailyMacros ORDER BY id DESC LIMIT 1")
    DailyMacros getLatest();
    @Query("SELECT * FROM DailyMacros WHERE date > :currentDate ORDER BY date ASC LIMIT 1")
    DailyMacros getNextByDate(String currentDate);

    @Query("SELECT * FROM DailyMacros WHERE date < :currentDate ORDER BY date DESC LIMIT 1")
    DailyMacros getPrevByDate(String currentDate);
}
