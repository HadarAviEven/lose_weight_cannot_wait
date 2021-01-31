package com.hadar.loseweightcantwait.ui.main.repository;

import android.app.Application;

import com.hadar.loseweightcantwait.data.db.enums.ActionEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Day;

import java.util.List;

import androidx.lifecycle.LiveData;

public class DayRepository {
//    private DayDao dayDao;
//    private LiveData<List<Day>> allDays;
//
//    public DayRepository(Application application) {
//        DayDatabase db = DayDatabase.getDatabase(application);
//        dayDao = db.dayDao();
//        allDays = dayDao.getAll();
//    }
//
//    public LiveData<List<Day>> getAll() {
//        return allDays;
//    }
//
//    public void insert(Day day) {
//        new Thread(new DayTask(ActionEnum.Insert, dayDao, day)).start();
//    }
//
//    public void deleteAll() {
//        new Thread(new DayTask(ActionEnum.DeleteAll, dayDao)).start();
//    }
//
//    public void delete(Day day) {
//        new Thread(new DayTask(ActionEnum.Delete, dayDao, day)).start();
//    }
//
//    public void update(Day day) {
//        new Thread(new DayTask(ActionEnum.Update, dayDao, day)).start();
//    }
}
