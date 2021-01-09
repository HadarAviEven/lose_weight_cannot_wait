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
import android.widget.TextView;
import android.widget.Toast;

import com.hadar.loseweightcantwait.ui.main.adapters.DayAdapter;
import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;
import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;
import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddTrainingActivity extends AppCompatActivity {
    private TextView name;
    private ArrayList<DayEnum> selectedDays;
    private ArrayList<MuscleEnum> selectedMuscles;
    private RecyclerView daysRecyclerView;
    private DayAdapter dayAdapter;
    private TextView selectedMusclesTextView;
    private List<String> musclesOptionsList;
    private MuscleEnum[] musclesOptions;
    private String[] stringMusclesOptions;
    private boolean[] checkedMuscles;
    private boolean[] confirmedCheckedMuscles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training);

        this.setTitle(getString(R.string.add_new_training));

        findViews();
        initRecyclerView();
        initArrays();
    }

    private void findViews() {
        name = findViewById(R.id.trainingNameEditText);
        daysRecyclerView = findViewById(R.id.daysRecyclerView);
        selectedMusclesTextView = findViewById(R.id.selectedMusclesTextView);
    }

    private void initRecyclerView() {
        daysRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        daysRecyclerView.setLayoutManager(layoutManager);
        ArrayList<DayEnum> daysButtonsList = new ArrayList<>(Arrays.asList(DayEnum.values()));
        dayAdapter = new DayAdapter(this, daysButtonsList);
        daysRecyclerView.setAdapter(dayAdapter);
    }

    private void initArrays() {
        musclesOptions = MuscleEnum.values();
        stringMusclesOptions = new String[musclesOptions.length];

        for (int i = 0; i < musclesOptions.length; i++) {
            stringMusclesOptions[i] = musclesOptions[i].name();
        }

        checkedMuscles = new boolean[stringMusclesOptions.length];
        Arrays.fill(checkedMuscles, false);

        confirmedCheckedMuscles = new boolean[stringMusclesOptions.length];
        Arrays.fill(confirmedCheckedMuscles, false);
    }

    public void onClickSaveButton(View view) {
        getLists();
        if (!allFieldsCompleted()) return;

        String trainingName = name.getText().toString();
        Training training = new Training(trainingName, selectedDays, selectedMuscles);
        returnIntent(training);
    }

    private void getLists() {
        getSelectedDaysList();
        getSelectedMusclesList();
    }

    private void getSelectedDaysList() {
        selectedDays = new ArrayList<>(dayAdapter.getSelectedDaysEnum());
    }

    private void getSelectedMusclesList() {
        selectedMuscles = new ArrayList<>();

        for (int i = 0; i < musclesOptions.length; i++) {
            if (confirmedCheckedMuscles[i]) {
                selectedMuscles.add(musclesOptions[i]);
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
        return !selectedDays.isEmpty();
    }

    private boolean musclesCompleted() {
        return !selectedMuscles.isEmpty();
    }

    private void returnIntent(Training newTraining) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.result_training), newTraining);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onClickSelectMusclesButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        musclesOptionsList = Arrays.asList(stringMusclesOptions);

        builder.setMultiChoiceItems(stringMusclesOptions, checkedMuscles, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                checkedMuscles[i] = isChecked;
            }
        });

        builder.setCancelable(false);
        builder.setTitle(R.string.select_muscles_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                clickedDialogPositiveButton();
                updateSelectedMuscles(checkedMuscles, confirmedCheckedMuscles);
            }
        });

        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                updateSelectedMuscles(confirmedCheckedMuscles, checkedMuscles);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clickedDialogPositiveButton() {
        StringBuilder textStringBuilder = new StringBuilder();

        for (int i = 0; i < checkedMuscles.length; i++) {
            boolean checked = checkedMuscles[i];
            if (checked) {
                textStringBuilder.append(musclesOptionsList.get(i)).append("\n");
            }
        }
        selectedMusclesTextView.setText(textStringBuilder.toString());
    }

    private void updateSelectedMuscles(boolean[] src, boolean[] des) {
        System.arraycopy(src, 0, des, 0, src.length);
    }
}