package com.hadar.loseweightcantwait;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.LiveData;

import com.hadar.loseweightcantwait.data.db.TrainingDao;
import com.hadar.loseweightcantwait.data.db.TrainingDatabase;
import com.hadar.loseweightcantwait.ui.addtraining.enums.ActionEnum;
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
        new NewAsyncTask().executeAsync(new TrainingTask(ActionEnum.Insert, trainingDao, training));
    }

    public void deleteAll() {
        new NewAsyncTask().executeAsync(new TrainingTask(ActionEnum.DeleteAll, trainingDao));
    }

    public void delete(Training training) {
        new NewAsyncTask().executeAsync(new TrainingTask(ActionEnum.Delete, trainingDao, training));
    }

    public void update(Training training) {
        new NewAsyncTask().executeAsync(new TrainingTask(ActionEnum.Update, trainingDao, training));
    }
}
