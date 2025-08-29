package com.example.calories;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = DailyMacros.class,
                parentColumns = "id",
                childColumns = "day_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class MacroEntry {
    @PrimaryKey(autoGenerate = true)
    public int entryId;

    public int day_id; // foreign key to DailyMacros.id

    public int protein;
    public int carbs;
    public int fat;
    public int calories;
    String name;
    String image;

    public MacroEntry(Meal meal, int day_id, float multiplier) {
        this.name = meal.name;
        this.image = meal.image;
        this.protein = (int) (meal.protein*multiplier);
        this.carbs = (int) (meal.carbs*multiplier);
        this.fat = (int) (meal.fat*multiplier);
        this.calories = (int) (meal.calories*multiplier);
        this.day_id=day_id;
    }

    public MacroEntry(Meal meal, int day_id) {
        this.name = meal.name;
        this.image = meal.image;
        this.protein = meal.protein;
        this.carbs = meal.carbs;
        this.fat = meal.fat;
        this.calories = meal.calories;
        this.day_id=day_id;
    }

    public MacroEntry() {}
}
