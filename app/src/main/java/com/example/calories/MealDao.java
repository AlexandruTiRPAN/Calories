package com.example.calories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MealDao {

    @Insert
    void insert(Meal meal);

    @Query("DELETE FROM Meal WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM Meal WHERE category = :category")
    List<Meal> selectByCategory(String category);

    @Query("SELECT * FROM DailyMacros WHERE id = :id LIMIT 1")
    Meal getById(int id);
}
