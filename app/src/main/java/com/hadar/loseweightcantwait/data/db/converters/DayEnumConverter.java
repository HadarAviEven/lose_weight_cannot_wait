package com.hadar.loseweightcantwait.data.db.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class DayEnumConverter {
    @TypeConverter
    public static ArrayList<DayEnum> fromString(String value) {
        Type listType = new TypeToken<ArrayList<DayEnum>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<DayEnum> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
