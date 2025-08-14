package com.example.calories;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseExecutor {
    private static final Executor diskIO = Executors.newSingleThreadExecutor();

    public static Executor diskIO() {
        return diskIO;
    }
}
