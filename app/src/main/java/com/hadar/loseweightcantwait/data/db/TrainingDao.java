package com.hadar.loseweightcantwait.data.db;

import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TrainingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Training training);

    @Query("DELETE FROM training_table")
    void deleteAll();

    @Query("SELECT * from training_table ORDER BY id")
    LiveData<List<Training>> getAll();

    @Query("SELECT * from training_table LIMIT 1")
    Training[] getAny();

    @Delete
    void delete(Training training);

    @Update
    void update(Training... training);
}