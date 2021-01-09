package com.hadar.loseweightcantwait.ui.main.events;

import com.hadar.loseweightcantwait.ui.addtraining.enums.EventType;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

public class TrainingEvent {
    private EventType eventType;
    private Training training;

    public TrainingEvent(EventType eventType, Training training) {
        this.eventType = eventType;
        this.training = training;
    }

    public String getEventType() {
        return eventType.getType();
    }

    public Training getTraining() {
        return training;
    }
}
