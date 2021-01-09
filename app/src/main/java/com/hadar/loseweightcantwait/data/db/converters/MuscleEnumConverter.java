package com.hadar.loseweightcantwait.data.db.converters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class MuscleEnumConverter {
    @TypeConverter
    public static ArrayList<MuscleEnum> fromString(String value) {
        Type listType = new TypeToken<ArrayList<MuscleEnum>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<MuscleEnum> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
