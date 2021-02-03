package com.hadar.loseweightcantwait.ui.addtraining.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;
import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Muscle;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;
import com.hadar.loseweightcantwait.ui.addtraining.viewmodels.AddTrainingViewModel;
import com.hadar.loseweightcantwait.ui.main.adapters.DayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class AddTrainingFragment extends Fragment {
    private EditText name;
    private ArrayList<DayEnum> selectedDays;
    private ArrayList<MuscleEnum> selectedMuscles;
    private RecyclerView daysRecyclerView;
    private DayAdapter dayAdapter;
    private ArrayList<Muscle> muscles;
    private TextView selectedMusclesTextView;
    private AddTrainingViewModel addTrainingViewModel;
    private Button musclesSelectionButton;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle();
        findViews(view);
        setOnClickButtons();
        initViews();
        initTrainingViewModel();
        checkForUpdate();
    }

    private void setTitle() {
        this.requireActivity().setTitle(getString(R.string.add_new_training));
    }

    private void findViews(View view) {
        name = view.findViewById(R.id.trainingNameEditText);
        daysRecyclerView = view.findViewById(R.id.daysRecyclerView);
        selectedMusclesTextView = view.findViewById(R.id.selectedMusclesTextView);
        musclesSelectionButton = view.findViewById(R.id.musclesSelectionButton);
        saveButton = view.findViewById(R.id.saveButton);
    }

    private void setOnClickButtons() {
        setOnClickMusclesSelectionButton();
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

    private void setOnClickMusclesSelectionButton() {
        musclesSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMusclesAlertDialog();
            }
        });
    }

    private void createMusclesAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        String[] options = new String[muscles.size()];
        final boolean[] checked = new boolean[muscles.size()];

        for (int i = 0; i < muscles.size(); i++) {
            options[i] = muscles.get(i).getMuscleEnum().name();
            checked[i] = muscles.get(i).isSelected();
        }

        builder.setMultiChoiceItems(options, checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                        checked[i] = isChecked;
                    }
                });

        builder.setCancelable(false);
        builder.setTitle(R.string.select_muscles_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                exitDialogByOk(checked);
                setMusclesTextView();
            }
        });

        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                exitDialogByCancel(checked);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exitDialogByOk(boolean[] checked) {
        for (int i = 0; i < muscles.size(); i++) {
            muscles.get(i).setSelected(checked[i]);
        }
    }

    private void exitDialogByCancel(boolean[] checked) {
        for (int i = 0; i < muscles.size(); i++) {
            checked[i] = muscles.get(i).isSelected();
        }
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
        ArrayList<MuscleEnum> musclesEnum = new ArrayList<>(Arrays.asList(MuscleEnum.values()));
        muscles = new ArrayList<>();
        for (MuscleEnum muscleEnum : musclesEnum) {
            muscles.add(new Muscle(muscleEnum));
        }
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

    private void checkForUpdate() {
        Bundle bundle = this.getArguments();
        if (bundle == null) return;

        int id = bundle.getInt(getString(R.string.training_id), -1);
        if (id == -1) return;

        addTrainingViewModel.initTrainingId(id);
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
        setMusclesArray(training);
        setMusclesTextView();
    }

    private void setMusclesArray(Training training) {
        selectedMuscles = training.getMuscles();
        for (int i = 0; i < selectedMuscles.size(); i++) {
            for (int j = i; j < muscles.size(); j++) {
                if (selectedMuscles.get(i).equals(muscles.get(j).getMuscleEnum())) {
                    muscles.get(j).setSelected(true);
                    break;
                }
            }
        }
    }

    private void setMusclesTextView() {
        StringBuilder textStringBuilder = new StringBuilder();

        for (Muscle muscle : muscles) {
            if (muscle.isSelected()) {
                textStringBuilder.append(muscle.getMuscleEnum()).append("\n");
            }
        }
        selectedMusclesTextView.setText(textStringBuilder.toString());
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

    private void makeToast(int whatIsMiss) {
        Toast.makeText(this.getActivity(), getString(whatIsMiss), Toast.LENGTH_SHORT).show();
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
        selectedMuscles = new ArrayList<>();

        for (Muscle muscle : muscles) {
            if (muscle.isSelected()) {
                selectedMuscles.add(muscle.getMuscleEnum());
            }
        }
    }
}