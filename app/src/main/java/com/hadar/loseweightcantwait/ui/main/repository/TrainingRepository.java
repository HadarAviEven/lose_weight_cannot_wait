package com.hadar.loseweightcantwait.ui.main.repository;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.LiveData;

import com.hadar.loseweightcantwait.data.db.task.TrainingTask;
import com.hadar.loseweightcantwait.data.db.TrainingDao;
import com.hadar.loseweightcantwait.data.db.TrainingDatabase;
import com.hadar.loseweightcantwait.data.db.enums.ActionEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

public class TrainingRepository {
    private TrainingDao trainingDao;
    private LiveData<List<Training>> allTrainings;

    public TrainingRepository(Application application) {
        TrainingDatabase db = TrainingDatabase.getDatabase(application);
        trainingDao = db.trainingDao();
        allTrainings = trainingDao.getAll();
    }

    public LiveData<List<Training>> getAll() {
        return allTrainings;
    }

    public void insert(Training training) {
        new Thread(new TrainingTask(ActionEnum.Insert, trainingDao, training)).start();
    }

    public void deleteAll() {
        new Thread(new TrainingTask(ActionEnum.DeleteAll, trainingDao)).start();
    }

    public void delete(Training training) {
        new Thread(new TrainingTask(ActionEnum.Delete, trainingDao, training)).start();
    }

    public void update(Training training) {
        new Thread(new TrainingTask(ActionEnum.Update, trainingDao, training)).start();
    }
}
