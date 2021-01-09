package com.hadar.loseweightcantwait.ui.main.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.data.db.TrainingDatabase;
import com.hadar.loseweightcantwait.ui.addtraining.enums.EventType;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;
import com.hadar.loseweightcantwait.ui.main.events.TrainingEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingViewHolder> {
    private ArrayList<Training> trainingsArrayList;
    private Context context;
    private TrainingDatabase mDb;

    public TrainingAdapter(Context context) {
        this.context = context;
        mDb = TrainingDatabase.getDatabase(context);
    }

    public void setData(ArrayList<Training> newTrainingsArrayList) {
        this.trainingsArrayList = newTrainingsArrayList;
        notifyDataSetChanged();
    }

    public void addTraining(Training training) {
        trainingsArrayList.add(training);
        notifyDataSetChanged();
    }

    public void removeTraining(Training training) {
        trainingsArrayList.remove(training);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_item, parent, false);
        return new TrainingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrainingViewHolder holder, final int position) {
        Training currentItem = trainingsArrayList.get(position);
        setTraining(holder, position, currentItem);
        setListener(holder, position);
    }

    private void setTraining(TrainingViewHolder holder, int position, Training currentItem) {
        setTrainingName(holder, currentItem);
        setTrainingDays(holder, currentItem);
        setTrainingMuscles(holder, currentItem);
    }

    private void setTrainingName(TrainingViewHolder holder, Training currentItem) {
        holder.name.setText(currentItem.getName());
    }

    private void setTrainingDays(TrainingViewHolder holder, Training currentItem) {
        if (currentItem.getDays().isEmpty()) return;

        StringBuilder daysStringBuilder = new StringBuilder();
        int indexOfLastItemInTheList = currentItem.getDays().size() - 1;
        for (int i = 0; i < indexOfLastItemInTheList; i++) {
            daysStringBuilder.append(currentItem.getDays().get(i)).append(", ");
        }
        daysStringBuilder.append(currentItem.getDays().get(indexOfLastItemInTheList));
        holder.days.setText(daysStringBuilder.toString());
    }

    private void setTrainingMuscles(TrainingViewHolder holder, Training currentItem) {
        if (currentItem.getMuscles().isEmpty()) return;

        StringBuilder musclesStringBuilder = new StringBuilder();
        int indexOfLastItemInTheList = currentItem.getMuscles().size() - 1;
        for (int i = 0; i < indexOfLastItemInTheList; i++) {
            musclesStringBuilder.append(currentItem.getMuscles().get(i)).append(", ");
        }
        musclesStringBuilder.append(currentItem.getMuscles().get(indexOfLastItemInTheList));
        holder.muscles.setText(musclesStringBuilder.toString());
    }

    private void setListener(final TrainingViewHolder holder, final int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startDeleteAlarmDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trainingsArrayList == null)
            return 0;
        return trainingsArrayList.size();
    }

    private void startDeleteAlarmDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.delete_alarm_dialog);
        alertDialogBuilder.setPositiveButton(R.string.positive_answer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteData(trainingsArrayList.get(position));
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.negative_answer, null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteData(Training trainingToDelete) {
        deleteDataFromAdapter(trainingToDelete);
        deleteDataFromDB(trainingToDelete);
    }

    private void deleteDataFromAdapter(Training training) {
        EventBus.getDefault().post(new TrainingEvent(new EventType(EventType.Type.DELETE), training));
    }

    private void deleteDataFromDB(final Training training) {
        TrainingDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.trainingDao().deleteTraining(training);
                Log.e("deleteDataFromDB", "loadAllTrainings:");
                final List<Training> trainingsList = mDb.trainingDao().loadAllTrainings();
                for (int i = 0; i < trainingsList.size(); i++) {
                    Log.e("MainActivity", "" + trainingsList.get(i).getName());
                }
            }
        });
    }
}
