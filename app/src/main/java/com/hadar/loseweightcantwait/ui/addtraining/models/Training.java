package com.hadar.loseweightcantwait.ui.addtraining.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hadar.loseweightcantwait.data.db.converters.DayEnumConverter;
import com.hadar.loseweightcantwait.data.db.converters.MuscleEnumConverter;
import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;
import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;

import java.util.ArrayList;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "training")
public class Training implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @TypeConverters(DayEnumConverter.class)
    private ArrayList<DayEnum> days;

    @TypeConverters(MuscleEnumConverter.class)
    private ArrayList<MuscleEnum> muscles;

    public Training() {

    }

    public Training(String name, ArrayList<DayEnum> days, ArrayList<MuscleEnum> muscles) {
        this.name = name;
        this.days = days;
        this.muscles = muscles;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<DayEnum> getDays() {
        return days;
    }

    public ArrayList<MuscleEnum> getMuscles() {
        return muscles;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDays(ArrayList<DayEnum> days) {
        this.days = days;
    }

    public void setMuscles(ArrayList<MuscleEnum> muscles) {
        this.muscles = muscles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeList(this.days);
        dest.writeList(this.muscles);
    }

    protected Training(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.days = new ArrayList<>();
        in.readList(this.days, DayEnum.class.getClassLoader());
        this.muscles = new ArrayList<>();
        in.readList(this.muscles, MuscleEnum.class.getClassLoader());
    }

    public static final Creator<Training> CREATOR = new Creator<Training>() {
        @Override
        public Training createFromParcel(Parcel source) {
            return new Training(source);
        }

        @Override
        public Training[] newArray(int size) {
            return new Training[size];
        }
    };
}
