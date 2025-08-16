package com.example.calories;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Meal {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String category;
    public String image;
    public int protein;
    public int carbs;
    public int fat;
    public int calories;
}
