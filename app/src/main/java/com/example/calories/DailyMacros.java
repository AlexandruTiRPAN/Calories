package com.example.calories;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DailyMacros {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date; // store as "yyyy-MM-dd"
    public int protein;
    public int carbs;
    public int fat;
    public int calories;
}
