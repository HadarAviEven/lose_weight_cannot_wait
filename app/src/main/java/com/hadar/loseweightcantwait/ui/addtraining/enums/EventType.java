package com.hadar.loseweightcantwait.ui.addtraining.enums;

import java.lang.annotation.Retention;

import androidx.annotation.StringDef;

import static java.lang.annotation.RetentionPolicy.SOURCE;

// Using StringDef instead of enum
public class EventType {
    @Retention(SOURCE)
    @StringDef({Type.INSERT, Type.DELETE, Type.UPDATE})
    public @interface Type {
        String INSERT = "insert";
        String DELETE = "delete";
        String UPDATE = "update";
    }

    @Type
    String typeValue;

    public EventType(@Type String typeValue) {
        this.typeValue = typeValue;
    }

    public String getType() {
        String returnType = null;

        switch (typeValue) {
            case Type.INSERT:
                returnType = "insert";
                break;
            case Type.DELETE:
                returnType = "delete";
                break;
            case Type.UPDATE:
                returnType = "update";
                break;
            default:
                break;
        }
        return returnType;
    }
}