package com.hadar.loseweightcantwait.ui.addtraining.models;

import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;

public class Day {
    private DayEnum dayEnum;
    private boolean pressed;

    public Day(DayEnum dayEnum) {
        this.dayEnum = dayEnum;
        this.pressed = false;
    }

    public DayEnum getDayEnum() {
        return dayEnum;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setDayEnum(DayEnum dayEnum) {
        this.dayEnum = dayEnum;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
