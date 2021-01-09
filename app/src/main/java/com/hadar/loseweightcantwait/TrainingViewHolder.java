package com.hadar.loseweightcantwait;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrainingViewHolder extends RecyclerView.ViewHolder {
    protected TextView name;
    protected TextView days;
    protected TextView muscles;

    public TrainingViewHolder(@NonNull View itemView) {
        super(itemView);

        findViews();
    }

    private void findViews() {
        name = itemView.findViewById(R.id.nameTextView);
        days = itemView.findViewById(R.id.daysTextView);
        muscles = itemView.findViewById(R.id.musclesTextView);
    }
}