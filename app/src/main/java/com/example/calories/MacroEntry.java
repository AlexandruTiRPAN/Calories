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
}
