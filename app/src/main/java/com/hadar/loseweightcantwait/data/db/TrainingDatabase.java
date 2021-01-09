package com.hadar.loseweightcantwait.data.db;

import android.content.Context;

import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Training.class}, version = 1)
public abstract class TrainingDatabase extends RoomDatabase {
    public abstract TrainingDao trainingDao();

    private static volatile TrainingDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static TrainingDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrainingDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrainingDatabase.class, "user_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
