package com.hadar.loseweightcantwait.ui.main.viewmodel;

import android.app.Application;

import com.hadar.loseweightcantwait.ui.addtraining.models.Day;
import com.hadar.loseweightcantwait.ui.main.repository.DayRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DayViewModel extends AndroidViewModel {
    private DayRepository dayRepository;
    private LiveData<List<Day>> allDays;

    public DayViewModel(Application application) {
        super(application);

        dayRepository = new DayRepository(application);
        allDays = dayRepository.getAll();
    }

    public LiveData<List<Day>> getAll() {
        return allDays;
    }

    public void insert(Day day) {
        dayRepository.insert(day);
    }

    public void deleteAll() {
        dayRepository.deleteAll();
    }

    public void delete(Day day) {
        dayRepository.delete(day);
    }

    public void update(Day day) {
        dayRepository.update(day);
    }
}