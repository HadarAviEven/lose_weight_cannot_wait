package com.hadar.loseweightcantwait.ui.main.adapters;

import android.view.View;
import android.widget.Button;

import com.hadar.loseweightcantwait.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MuscleViewHolder extends RecyclerView.ViewHolder {
    protected Button muscleButton;

    public MuscleViewHolder(@NonNull View itemView) {
        super(itemView);

        findViews();
    }

    private void findViews() {
        muscleButton = itemView.findViewById(R.id.muscleButton);
    }
}
