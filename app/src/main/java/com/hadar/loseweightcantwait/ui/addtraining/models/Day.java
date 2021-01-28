package com.hadar.loseweightcantwait.ui.addtraining.models;

import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;

public class Day {
    private DayEnum dayEnum;
    private boolean selected;

    public Day(DayEnum dayEnum) {
        this.dayEnum = dayEnum;
        this.selected = false;
    }

    public DayEnum getDayEnum() {
        return dayEnum;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setDayEnum(DayEnum dayEnum) {
        this.dayEnum = dayEnum;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
