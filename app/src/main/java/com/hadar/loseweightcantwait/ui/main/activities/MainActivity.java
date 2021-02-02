package com.hadar.loseweightcantwait.ui.main.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hadar.loseweightcantwait.ui.main.viewmodels.TrainingsViewModel;
import com.hadar.loseweightcantwait.ui.main.recyclerviews.EmptyRecyclerView;
import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.main.adapters.TrainingAdapter;
import com.hadar.loseweightcantwait.ui.addtraining.activities.AddTrainingActivity;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EmptyRecyclerView recyclerView;
    private TextView emptyView;

    private TrainingAdapter trainingAdapter;
    private TrainingsViewModel trainingsViewModel;

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
            public void onItemClick(Training training) {
                trainingsViewModel.onTrainingItemClicked(training);
            }
        });
    }

    private void initTrainingViewModel() {
        trainingsViewModel = ViewModelProviders.of(this).get(TrainingsViewModel.class);

        observe();
    }

    private void observe() {
        trainingsViewModel.getAll().observe(this, new Observer<List<Training>>() {
            @Override
            public void onChanged(@Nullable final List<Training> trainings) {
                trainingAdapter.setData(trainings);
            }
        });

        trainingsViewModel.launchAddTrainingScreenLiveEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer trainingId) {
                launchUpdateTrainingActivity(trainingId);
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
                trainingsViewModel.onTrainingItemSwiped(training);
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    public void onClickAddTrainingButton(View view) {
        createNewTraining();
    }

    private void createNewTraining() {
        Intent intent = new Intent(this, AddTrainingActivity.class);
        startActivity(intent);
    }

    public void launchUpdateTrainingActivity(int trainingId) {
        Intent intent = new Intent(this, AddTrainingActivity.class);
        intent.putExtra(getString(R.string.training_id), trainingId);
        startActivity(intent);
    }
}