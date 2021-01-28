package com.hadar.loseweightcantwait;

import com.hadar.loseweightcantwait.data.db.TrainingDao;
import com.hadar.loseweightcantwait.ui.addtraining.enums.ActionEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

public class TrainingTask extends BaseTask {
    private ActionEnum action;
    private final TrainingDao mDao;
    private Training mTraining;

    public TrainingTask(ActionEnum actionEnum, TrainingDao trainingDao) {
        this.action = actionEnum;
        this.mDao = trainingDao;
    }

    public TrainingTask(ActionEnum actionEnum, TrainingDao trainingDao, Training training) {
        this.action = actionEnum;
        this.mDao = trainingDao;
        this.mTraining = training;
    }

    // like doInBackground()
    @Override
    public Object call() {
        switch (this.action) {
            case Initialize:
                mDao.getAll();
                break;
            case Insert:
                mDao.insert(this.mTraining);
                break;
            case Delete:
                mDao.delete(this.mTraining);
                break;
            case DeleteAll:
                mDao.deleteAll();
                break;
            case Update:
                mDao.update(this.mTraining);
                break;
            default:
                break;
        }
        return null;
    }
}
