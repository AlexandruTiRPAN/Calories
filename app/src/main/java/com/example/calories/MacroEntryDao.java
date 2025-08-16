package com.example.calories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MacroEntryDao {
    @Insert
    void insert(MacroEntry entry);

    @Query("SELECT * FROM MacroEntry WHERE day_id = :dayId")
    List<MacroEntry> getEntriesForDay(int dayId);

    @Query("DELETE FROM MacroEntry WHERE entryId = :entryId")
    void deleteById(int entryId);
}
