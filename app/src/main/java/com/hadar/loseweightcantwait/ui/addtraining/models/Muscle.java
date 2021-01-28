package com.hadar.loseweightcantwait.ui.addtraining.models;

import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;

public class Muscle {
    private MuscleEnum muscleEnum;
    private boolean selected;

    public Muscle(MuscleEnum muscleEnum) {
        this.muscleEnum = muscleEnum;
        this.selected = false;
    }

    public MuscleEnum getMuscleEnum() {
        return muscleEnum;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setMuscleEnum(MuscleEnum muscleEnum) {
        this.muscleEnum = muscleEnum;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
