package com.hadar.loseweightcantwait.ui.addtraining.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hadar.loseweightcantwait.ui.addtraining.models.Muscle;
import com.hadar.loseweightcantwait.ui.main.adapters.DayAdapter;
import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;
import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;
import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.ArrayList;
import java.util.Arrays;

public class AddTrainingActivity extends AppCompatActivity {
    private EditText name;
    private ArrayList<DayEnum> selectedDays;
    private ArrayList<MuscleEnum> selectedMuscles;
    private RecyclerView daysRecyclerView;
    private DayAdapter dayAdapter;
    private ArrayList<Muscle> muscles;
    private TextView selectedMusclesTextView;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training);

        setActivityTitle();
        findViews();
        initDays();
        initMuscles();
        checkForUpdate();
    }

    private void setActivityTitle() {
        this.setTitle(getString(R.string.add_new_training));
    }

    private void findViews() {
        name = findViewById(R.id.trainingNameEditText);
        daysRecyclerView = findViewById(R.id.daysRecyclerView);
        selectedMusclesTextView = findViewById(R.id.selectedMusclesTextView);
    }

    private void initDays() {
        initDayRecyclerView();
        initDayAdapter();
    }

    private void initDayRecyclerView() {
        daysRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        daysRecyclerView.setLayoutManager(layoutManager);
    }

    private void initDayAdapter() {
        dayAdapter = new DayAdapter(this);
        daysRecyclerView.setAdapter(dayAdapter);
    }

    private void initMuscles() {
        ArrayList<MuscleEnum> musclesEnum = new ArrayList<>(Arrays.asList(MuscleEnum.values()));
        muscles = new ArrayList<>();
        for (MuscleEnum muscleEnum : musclesEnum) {
            muscles.add(new Muscle(muscleEnum));
        }
    }

    private void checkForUpdate() {
        extras = getIntent().getExtras();
        if (extras != null) {
            Training trainingForUpdate =
                    getIntent().getParcelableExtra(getString(R.string.training_for_update));
            if (trainingForUpdate != null) {
                setData(trainingForUpdate);
            }
        }
    }

    private void setData(Training training) {
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

    public void onClickSelectMusclesButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
                updatePositive(checked);
                setMusclesTextView();
            }
        });

        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                updateNegative(checked);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updatePositive(boolean[] checked) {
        for (int i = 0; i < muscles.size(); i++) {
            muscles.get(i).setSelected(checked[i]);
        }
    }

    private void updateNegative(boolean[] checked) {
        for (int i = 0; i < muscles.size(); i++) {
            checked[i] = muscles.get(i).isSelected();
        }
    }

    public void onClickSaveButton(View view) {
        if (!allFieldsCompleted()) return;

        String trainingName = name.getText().toString();
        Training training = new Training(trainingName, selectedDays, selectedMuscles);
        returnIntent(training);
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
        Toast.makeText(this, getString(whatIsMiss), Toast.LENGTH_SHORT).show();
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

    private void returnIntent(Training newTraining) {
        Intent returnIntent = new Intent();

        if (extras != null && extras.containsKey(getString(R.string.training_id))) {
            returnIntent.putExtra(getString(R.string.training_for_update), newTraining);
            int id = extras.getInt(getString(R.string.training_id), -1);
            if (id != -1) {
                returnIntent.putExtra(getString(R.string.training_id), id);
            }
        } else {
            returnIntent.putExtra(getString(R.string.result_training), newTraining);
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}