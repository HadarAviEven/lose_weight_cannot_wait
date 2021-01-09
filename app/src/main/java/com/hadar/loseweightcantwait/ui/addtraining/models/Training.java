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
    private int trainingID;

    @ColumnInfo(name = "trainingname")
    private String trainingName;

    @TypeConverters(DayEnumConverter.class)
    private ArrayList<DayEnum> trainingDays;

    @TypeConverters(MuscleEnumConverter.class)
    private ArrayList<MuscleEnum> trainingMuscles;

    public Training() {

    }

    public Training(String name, ArrayList<DayEnum> days, ArrayList<MuscleEnum> muscles) {
        this.trainingName = name;
        this.trainingDays = days;
        this.trainingMuscles = muscles;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public ArrayList<DayEnum> getTrainingDays() {
        return trainingDays;
    }

    public ArrayList<MuscleEnum> getTrainingMuscles() {
        return trainingMuscles;
    }

    public int getTrainingID() {
        return trainingID;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public void setTrainingDays(ArrayList<DayEnum> trainingDays) {
        this.trainingDays = trainingDays;
    }

    public void setTrainingMuscles(ArrayList<MuscleEnum> trainingMuscles) {
        this.trainingMuscles = trainingMuscles;
    }

    public void setTrainingID(int trainingID) {
        this.trainingID = trainingID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.trainingID);
        dest.writeString(this.trainingName);
        dest.writeList(this.trainingDays);
        dest.writeList(this.trainingMuscles);
    }

    protected Training(Parcel in) {
        this.trainingID = in.readInt();
        this.trainingName = in.readString();
        this.trainingDays = new ArrayList<DayEnum>();
        in.readList(this.trainingDays, DayEnum.class.getClassLoader());
        this.trainingMuscles = new ArrayList<MuscleEnum>();
        in.readList(this.trainingMuscles, MuscleEnum.class.getClassLoader());
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
