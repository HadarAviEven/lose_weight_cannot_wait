package com.hadar.loseweightcantwait;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

public class TrainingViewModel extends AndroidViewModel {
    private TrainingRepository trainingRepository;
    private LiveData<List<Training>> allTrainings;

    public TrainingViewModel(Application application) {
        super(application);

        trainingRepository = new TrainingRepository(application);
        allTrainings = trainingRepository.getAll();
    }

    public LiveData<List<Training>> getAll() {
        return allTrainings;
    }

    public void insert(Training training) {
        trainingRepository.insert(training);
    }

    public void deleteAll() {
        trainingRepository.deleteAll();
    }

    public void delete(Training training) {
        trainingRepository.delete(training);
    }

    public void update(Training training) {
        trainingRepository.update(training);
    }
}
