package com.hadar.loseweightcantwait.ui.main.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingViewHolder> {
    private ArrayList<Training> trainingsArrayList;
    private Context context;
    public ClickListener clickListener;

    public TrainingAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Training> trainings) {
        this.trainingsArrayList = new ArrayList<>(trainings);
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

        setTraining(holder, currentItem);
        setListener(holder, currentItem);
    }

    private void setTraining(TrainingViewHolder holder, Training currentItem) {
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

    private void setListener(TrainingViewHolder holder, final Training currentItem) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trainingsArrayList == null)
            return 0;
        return trainingsArrayList.size();
    }

    public Training getTrainingAtPosition(int position) {
        return trainingsArrayList.get(position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(Training training);
    }
}
