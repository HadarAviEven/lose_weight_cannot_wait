package com.hadar.loseweightcantwait.ui.main.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.data.db.TrainingDatabase;
import com.hadar.loseweightcantwait.utilities.listeners.OnStartDragListener;
import com.hadar.loseweightcantwait.ui.addtraining.enums.EventType;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;
import com.hadar.loseweightcantwait.ui.main.events.TrainingEvent;
import com.hadar.loseweightcantwait.utilities.ItemTouchHelperAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingViewHolder>
        implements ItemTouchHelperAdapter {
    private ArrayList<Training> trainingsArrayList;
    private Context context;
    private TrainingDatabase mDb;
    private OnStartDragListener mDragStartListener;

    public TrainingAdapter(Context context,
                           OnStartDragListener dragListener) {
        this.context = context;
        mDb = TrainingDatabase.getDatabase(context);
        mDragStartListener = dragListener;
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

    public void updateTraining(Training training) {
//        trainingsArrayList.remove(training);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrainingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.training_item, parent, false);
        return new TrainingViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
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

    @SuppressLint("ClickableViewAccessibility")
    private void setListener(final TrainingViewHolder holder, final int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startDeleteTrainingDialog(position);
                return true;
            }
        });

        holder.handleViewButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trainingsArrayList == null)
            return 0;
        return trainingsArrayList.size();
    }

    private void startDeleteTrainingDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.delete_alarm_dialog);
        alertDialogBuilder
                .setPositiveButton(R.string.positive_answer, new DialogInterface.OnClickListener() {
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
        Log.e("trainingToDelete: ", " " + trainingToDelete.getName() + " id: " +
                trainingToDelete.getId());
        deleteDataFromAdapter(trainingToDelete);
        deleteDataFromDB(trainingToDelete);
    }

    private void deleteDataFromAdapter(Training training) {
        EventBus.getDefault()
                .post(new TrainingEvent(new EventType(EventType.Type.DELETE), training));
    }

    private void deleteDataFromDB(final Training training) {
        TrainingDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.trainingDao().deleteTraining(training);
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        Log.e("TrainingAdapter", "onItemDismiss");
//        removeTraining(trainingsArrayList.get(position));
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < trainingsArrayList.size() && toPosition < trainingsArrayList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    updateMovement(i, 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    updateMovement(i, -1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    private void updateMovement(int i, int addOrSub) {
        updateMovementInAdapter(i, addOrSub);
        updateMovementInDB(i, addOrSub);
    }

    private void swapTrainingsId(int i, int addOrSub) {
        int order1 = trainingsArrayList.get(i).getId();
        int order2 = trainingsArrayList.get(i + addOrSub).getId();
        trainingsArrayList.get(i).setId(order2);
        trainingsArrayList.get(i + addOrSub).setId(order1);
    }

    private void updateMovementInAdapter(int i, int addOrSub) {
        Collections.swap(trainingsArrayList, i, i + addOrSub);
        swapTrainingsId(i, addOrSub);
    }

    private void updateMovementInDB(final int i, final int addOrSub) {
        TrainingDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDb.trainingDao().updateTraining(trainingsArrayList.get(i));
                mDb.trainingDao()
                        .updateTraining(trainingsArrayList.get(i + addOrSub));
            }
        });
    }
}
