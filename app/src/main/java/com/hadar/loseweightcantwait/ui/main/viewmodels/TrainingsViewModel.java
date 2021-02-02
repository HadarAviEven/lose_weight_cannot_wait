package com.hadar.loseweightcantwait.ui.main.viewmodels;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hadar.loseweightcantwait.SingleLiveEvent;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;
import com.hadar.loseweightcantwait.data.repositories.TrainingRepository;

public class TrainingsViewModel extends AndroidViewModel {

    private TrainingRepository trainingRepository;
    private LiveData<List<Training>> allTrainings;
    public SingleLiveEvent<Integer> launchAddTrainingScreenLiveEvent = new SingleLiveEvent<>();

    public TrainingsViewModel(Application application) {
        super(application);

        trainingRepository = new TrainingRepository(application);
        allTrainings = trainingRepository.getAll();
    }

    public LiveData<List<Training>> getAll() {
        return allTrainings;
    }

    public void onClickDeleteAll() {
        deleteAll();
    }

    private void deleteAll() {
        trainingRepository.deleteAll();
    }

    public void onTrainingItemSwiped(Training training) {
        deleteTrainingItem(training);
    }

    private void deleteTrainingItem(Training training) {
        trainingRepository.delete(training);
    }

    public void onTrainingItemClicked(Training training) {
        launchAddTrainingScreen(training.getId());
    }

    private void launchAddTrainingScreen(int trainingId) {
        launchAddTrainingScreenLiveEvent.setValue(trainingId);
    }
}
