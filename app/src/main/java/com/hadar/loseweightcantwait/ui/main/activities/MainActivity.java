package com.hadar.loseweightcantwait.ui.main.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hadar.loseweightcantwait.ui.main.EmptyRecyclerView;
import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.main.adapters.TrainingAdapter;
import com.hadar.loseweightcantwait.ui.main.events.TrainingEvent;
import com.hadar.loseweightcantwait.data.db.TrainingDatabase;
import com.hadar.loseweightcantwait.ui.addtraining.activities.AddTrainingActivity;
import com.hadar.loseweightcantwait.ui.addtraining.enums.EventType;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EmptyRecyclerView recyclerView;
    private TextView emptyView;
    private TrainingAdapter trainingAdapter;
    private final int LAUNCH_SECOND_ACTIVITY = 1;
    private TrainingDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initRecyclerView();
        initDB();
        retrieveTasks();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        trainingAdapter = new TrainingAdapter(this);
        recyclerView.setAdapter(trainingAdapter);
        recyclerView.setEmptyView(emptyView);
        recyclerView.initEmptyView();
    }

    private void initDB() {
        mDb = TrainingDatabase.getDatabase(getApplicationContext());
    }

    private void retrieveTasks() {
        TrainingDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.e("retrieveTasks", "loadAllTrainings:");
                final List<Training> trainingsList = mDb.trainingDao().loadAllTrainings();
                for (int i = 0; i < trainingsList.size(); i++) {
                    Log.e("MainActivity", "" + trainingsList.get(i).getName());
                }
                final ArrayList<Training> trainingsArrayList = new ArrayList<>(trainingsList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        trainingAdapter.setData(trainingsArrayList);
                        recyclerView.initEmptyView();
                    }
                });
            }
        });
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

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                final Training resultTraining = data.getParcelableExtra(getString(R.string.result_training));
                if (resultTraining != null) {
                    insertData(data);
                }
            }
        }
    }

    private void insertData(Intent data) {
        Training resultTraining = data.getParcelableExtra(getString(R.string.result_training));

        insertDataToAdapter(resultTraining);
        insertDataToDB(resultTraining);
    }

    private void insertDataToAdapter(Training training) {
        EventBus.getDefault().post(new TrainingEvent(new EventType(EventType.Type.INSERT), training));
    }

    private void insertDataToDB(final Training training) {
        TrainingDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final long[] newId = new long[1];
                newId[0] = mDb.trainingDao().insertTraining(training);
                training.setId((int) newId[0]);

                Log.e("insertDataToDB", "loadAllTrainings:");
                final List<Training> trainingsList = mDb.trainingDao().loadAllTrainings();
                for (int i = 0; i < trainingsList.size(); i++) {
                    Log.e("MainActivity", "" + trainingsList.get(i).getName());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TrainingEvent trainingEvent) {
        String type = trainingEvent.getEventType();

        switch (type) {
            case "insert":
                trainingAdapter.addTraining(trainingEvent.getTraining());
                break;
            case "delete":
                trainingAdapter.removeTraining(trainingEvent.getTraining());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}