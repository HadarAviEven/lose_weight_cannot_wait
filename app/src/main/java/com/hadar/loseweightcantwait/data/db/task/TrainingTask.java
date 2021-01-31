package com.hadar.loseweightcantwait.data.db.task;

import com.hadar.loseweightcantwait.data.db.TrainingDao;
import com.hadar.loseweightcantwait.data.db.enums.ActionEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

public class TrainingTask implements Runnable {
    private ActionEnum action;
    private final TrainingDao dao;
    private Training training;

    public TrainingTask(ActionEnum actionEnum, TrainingDao trainingDao) {
        this.action = actionEnum;
        this.dao = trainingDao;
    }

    public TrainingTask(ActionEnum actionEnum, TrainingDao trainingDao, Training training) {
        this.action = actionEnum;
        this.dao = trainingDao;
        this.training = training;
    }

    @Override
    public void run() {
        switch (this.action) {
            case Initialize:
                dao.getAll();
                break;
            case Insert:
                dao.insert(this.training);
                break;
            case Delete:
                dao.delete(this.training);
                break;
            case DeleteAll:
                dao.deleteAll();
                break;
            case Update:
                dao.update(this.training);
                break;
            default:
                break;
        }
    }
}
