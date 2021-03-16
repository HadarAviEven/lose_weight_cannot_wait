package com.hadar.loseweightcantwait.ui.addtraining.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;
import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;
import com.hadar.loseweightcantwait.ui.addtraining.viewmodels.AddTrainingViewModel;
import com.hadar.loseweightcantwait.ui.main.adapters.DayAdapter;
import com.hadar.loseweightcantwait.ui.main.adapters.MuscleAdapter;

import java.util.ArrayList;

public class AddTrainingFragment extends Fragment {
    private EditText name;
    private ArrayList<DayEnum> selectedDays;
    private ArrayList<MuscleEnum> selectedMuscles;
    private RecyclerView daysRecyclerView;
    private DayAdapter dayAdapter;
    private RecyclerView musclesRecyclerView;
    private MuscleAdapter muscleAdapter;
    private AddTrainingViewModel addTrainingViewModel;
    private Button saveButton;
    private boolean isUpdateTraining;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        setOnClickButtons();
        initViews();
        initTrainingViewModel();
        isUpdateTraining = checkForUpdate();
        setTitle();
    }

    private void findViews(View view) {
        name = view.findViewById(R.id.trainingNameEditText);
        daysRecyclerView = view.findViewById(R.id.daysRecyclerView);
        musclesRecyclerView = view.findViewById(R.id.musclesRecyclerView);
        saveButton = view.findViewById(R.id.saveButton);
    }

    private void setOnClickButtons() {
        setOnClickSaveButton();
    }

    private void setOnClickSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allFieldsCompleted()) return;

                String trainingName = name.getText().toString();
                Training training = new Training(trainingName, selectedDays, selectedMuscles);

                addTrainingViewModel.onSaveTrainingButtonClicked(training);
            }
        });
    }

    private void initViews() {
        initDays();
        initMuscles();
    }

    private void initDays() {
        initDayRecyclerView();
        initDayAdapter();
    }

    private void initDayRecyclerView() {
        daysRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        daysRecyclerView.setLayoutManager(layoutManager);
    }

    private void initDayAdapter() {
        dayAdapter = new DayAdapter(this.getActivity());
        daysRecyclerView.setAdapter(dayAdapter);
    }

    private void initMuscles() {
        initMuscleRecyclerView();
        initMuscleAdapter();
    }

    private void initMuscleRecyclerView() {
        musclesRecyclerView.setHasFixedSize(true);
        musclesRecyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 4));
    }

    private void initMuscleAdapter() {
        muscleAdapter = new MuscleAdapter(this.getActivity());
        musclesRecyclerView.setAdapter(muscleAdapter);
    }

    private void initTrainingViewModel() {
        addTrainingViewModel = ViewModelProviders.of(this).get(AddTrainingViewModel.class);

        observe();
    }

    private void observe() {
        addTrainingViewModel.currUpdatingTrainingLiveData.observe(
                this.requireActivity(), new Observer<Training>() {
                    @Override
                    public void onChanged(Training training) {
                        setCurrTraining(training);
                    }
                });

        addTrainingViewModel.exitAddTrainingScreenLiveEvent.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                exitFragment();
            }
        });
    }

    private void exitFragment() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    private boolean checkForUpdate() {
        Bundle bundle = this.getArguments();
        if (bundle == null) return false;

        int id = bundle.getInt(getString(R.string.training_id), -1);
        if (id == -1) return false;

        addTrainingViewModel.initTrainingId(id);
        return true;
    }

    private void setTitle() {
        if (isUpdateTraining) {
            this.requireActivity().setTitle(getString(R.string.edit_training));
        } else {
            this.requireActivity().setTitle(getString(R.string.add_new_training));
        }
    }

    private void setCurrTraining(Training training) {
        setName(training);
        setDays(training);
        setMuscles(training);
    }

    private void setName(Training training) {
        name.setText(training.getName());
        name.setSelection(training.getName().length());
        name.requestFocus();
    }

    private void setDays(Training training) {
        dayAdapter.setSelectedDays(training.getDays());
    }

    private void setMuscles(Training training) {
        muscleAdapter.setSelectedMuscles(training.getMuscles());
    }

    private boolean allFieldsCompleted() {
        if (!nameCompleted()) {
            makeToast(R.string.miss_name);
            return false;
        }
        if (!daysCompleted()) {
            makeToast(R.string.miss_days);
            return false;
        }
        if (!musclesCompleted()) {
            makeToast(R.string.miss_muscles);
            return false;
        }
        return true;
    }

    private void makeToast(int stringId) {
        Toast.makeText(this.getActivity(), getString(stringId), Toast.LENGTH_SHORT).show();
    }

    private boolean nameCompleted() {
        return !name.getText().toString().isEmpty();
    }

    private boolean daysCompleted() {
        getSelectedDaysList();
        return !selectedDays.isEmpty();
    }

    private boolean musclesCompleted() {
        getSelectedMusclesList();
        return !selectedMuscles.isEmpty();
    }

    private void getSelectedDaysList() {
        selectedDays = dayAdapter.getSelectedDays();
    }

    private void getSelectedMusclesList() {
        selectedMuscles = muscleAdapter.getSelectedMuscles();
    }
}