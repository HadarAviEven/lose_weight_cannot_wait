package com.hadar.loseweightcantwait.data.db;

import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TrainingDao {
    @Query("SELECT * FROM training ORDER BY trainingID")
    List<Training> loadAllTrainings();

    @Insert
    long insertTraining(Training training);

    @Update
    void updateTraining(Training training);

    @Delete
    void deleteTraining(Training training);

    @Query("SELECT * FROM training WHERE trainingID = :id")
    Training loadTrainingById(int id);
}