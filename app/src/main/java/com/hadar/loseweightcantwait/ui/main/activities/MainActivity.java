package com.hadar.loseweightcantwait.ui.main.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hadar.loseweightcantwait.ui.main.viewmodel.TrainingViewModel;
import com.hadar.loseweightcantwait.ui.main.recyclerview.EmptyRecyclerView;
import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.main.adapters.TrainingAdapter;
import com.hadar.loseweightcantwait.ui.addtraining.activities.AddTrainingActivity;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EmptyRecyclerView recyclerView;
    private TextView emptyView;
    private TrainingAdapter trainingAdapter;
    private TrainingViewModel trainingViewModel;
    private final int LAUNCH_SECOND_ACTIVITY = 1;
    public static final int LAUNCH_SECOND_ACTIVITY_FOR_UPDATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initTrainingRecyclerView();
        initTrainingAdapter();
        initTrainingViewModel();
        initItemTouchHelper();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
    }

    private void initTrainingRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(emptyView);
        recyclerView.initEmptyView();
    }

    private void initTrainingAdapter() {
        trainingAdapter = new TrainingAdapter(this);
        recyclerView.setAdapter(trainingAdapter);

        trainingAdapter.setOnItemClickListener(new TrainingAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Training training = trainingAdapter.getTrainingAtPosition(position);
                launchUpdateTrainingActivity(training);
            }
        });
    }

    private void initTrainingViewModel() {
        trainingViewModel = ViewModelProviders.of(this).get(TrainingViewModel.class);

        trainingViewModel.getAll().observe(this, new Observer<List<Training>>() {
            @Override
            public void onChanged(@Nullable final List<Training> trainings) {
                trainingAdapter.setData(trainings);
            }
        });
    }

    private void initItemTouchHelper() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Training training = trainingAdapter.getTrainingAtPosition(position);
                trainingViewModel.delete(training);
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    public void onClickAddTrainingButton(View view) {
        createNewTraining();
    }

    private void createNewTraining() {
        Intent intent = new Intent(this, AddTrainingActivity.class);
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY && resultCode == Activity.RESULT_OK) {
            Training resultTraining = data.getParcelableExtra(getString(R.string.result_training));
            if (resultTraining != null) {
                trainingViewModel.insert(resultTraining);
            }
        } else if (requestCode == LAUNCH_SECOND_ACTIVITY_FOR_UPDATE &&
                resultCode == Activity.RESULT_OK) {
            Training updatedTraining =
                    data.getParcelableExtra(getString(R.string.training_for_update));
            int id = data.getIntExtra(getString(R.string.training_id), -1);
            if (updatedTraining != null && id != -1) {
                Training training =
                        new Training(id, updatedTraining.getName(), updatedTraining.getDays(),
                                updatedTraining.getMuscles());
                trainingViewModel.update(training);
            }
        }
    }

    public void launchUpdateTrainingActivity(Training training) {
        Intent intent = new Intent(this, AddTrainingActivity.class);
        intent.putExtra(getString(R.string.training_for_update), training);
        intent.putExtra(getString(R.string.training_id), training.getId());
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY_FOR_UPDATE);
    }
}