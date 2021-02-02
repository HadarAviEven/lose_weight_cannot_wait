package com.hadar.loseweightcantwait.ui.addtraining.viewmodels;

import android.app.Application;

import com.hadar.loseweightcantwait.SingleLiveEvent;
import com.hadar.loseweightcantwait.data.repositories.TrainingRepository;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class AddTrainingViewModel extends AndroidViewModel {

    private TrainingRepository trainingRepository;
    public MutableLiveData<Training> currUpdatingTrainingLiveData = new MutableLiveData<>();
    public SingleLiveEvent<Void> exitAddTrainingScreenLiveEvent = new SingleLiveEvent<>();
    private boolean isUpdatingTraining = false;
    private int currTrainingId = -1;

    public AddTrainingViewModel(Application application) {
        super(application);

        trainingRepository = new TrainingRepository(application);
    }

    public void initTrainingId(final int trainingId) {
        if (trainingId == -1) return;

        currTrainingId = trainingId;
        isUpdatingTraining = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                currUpdatingTrainingLiveData
                        .postValue(trainingRepository.getTrainingById(trainingId));
            }
        }).start();
    }

    public void onSaveTrainingButtonClicked(Training training) {
        saveTraining(training);
        exitAddTrainingScreenLiveEvent.call();
    }

    private void saveTraining(Training training) {
        if (isUpdatingTraining) {
            training.setId(currTrainingId);
            update(training);
        } else {
            insert(training);
        }
    }

    private void insert(Training training) {
        trainingRepository.insert(training);
    }

    private void update(Training training) {
        trainingRepository.update(training);
    }
}
