package com.hadar.loseweightcantwait.ui.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.enums.MuscleEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Muscle;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MuscleAdapter extends RecyclerView.Adapter<MuscleViewHolder> {
    private Context context;
    private ArrayList<Muscle> muscles;

    public MuscleAdapter(Context context) {
        this.context = context;

        ArrayList<MuscleEnum> musclesEnum = new ArrayList<>(Arrays.asList(MuscleEnum.values()));
        muscles = new ArrayList<>();
        for (MuscleEnum muscleEnum : musclesEnum) {
            muscles.add(new Muscle(muscleEnum));
        }
    }

    @NonNull
    @Override
    public MuscleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.muscle_item, parent, false);
        return new MuscleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MuscleViewHolder holder, final int position) {
        Muscle currentItem = muscles.get(position);

        setText(holder, currentItem);
        setColor(holder, currentItem);
        setListener(holder, position);
    }

    private void setText(MuscleViewHolder holder, Muscle currentItem) {
        holder.muscleButton.setText(currentItem.getMuscleEnum().toString());
    }

    private void setColor(MuscleViewHolder holder, Muscle currentItem) {
        if (currentItem.isSelected()) {
            muscleButtonTurnOn(holder.muscleButton);
        } else {
            muscleButtonTurnOff(holder.muscleButton);
        }
    }

    private void setListener(MuscleViewHolder holder, final int position) {
        holder.muscleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button muscleButton = (Button) v;

                if (!muscles.get(position).isSelected()) {
                    muscles.get(position).setSelected(true);
                    muscleButtonTurnOn(muscleButton);
                } else {
                    muscles.get(position).setSelected(false);
                    muscleButtonTurnOff(muscleButton);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (muscles == null)
            return 0;
        return muscles.size();
    }

    private void muscleButtonTurnOn(Button muscleButton) {
        muscleButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        muscleButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
    }

    private void muscleButtonTurnOff(Button muscleButton) {
        muscleButton.setTextColor(ContextCompat.getColor(context, R.color.black));
        muscleButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
    }

    public ArrayList<MuscleEnum> getSelectedMuscles() {
        ArrayList<MuscleEnum> selectedMuscles = new ArrayList<>();
        for (Muscle muscle : this.muscles) {
            if (muscle.isSelected()) {
                selectedMuscles.add(muscle.getMuscleEnum());
            }
        }
        return selectedMuscles;
    }

    public void setSelectedMuscles(ArrayList<MuscleEnum> selectedMuscles) {
        for (int i = 0; i < selectedMuscles.size(); i++) {
            for (int j = i; j < muscles.size(); j++) {
                if (selectedMuscles.get(i).equals(muscles.get(j).getMuscleEnum())) {
                    muscles.get(j).setSelected(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }
}
