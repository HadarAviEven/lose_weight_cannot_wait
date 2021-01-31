package com.hadar.loseweightcantwait.ui.main.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hadar.loseweightcantwait.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrainingViewHolder extends RecyclerView.ViewHolder {
    protected TextView name;
    protected TextView days;
    protected TextView muscles;
    protected ImageButton handleViewButton;

    public TrainingViewHolder(@NonNull View itemView) {
        super(itemView);

        findViews();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrainingAdapter.clickListener.onItemClick(view, getAdapterPosition());
            }
        });
    }

    private void findViews() {
        name = itemView.findViewById(R.id.nameTextView);
        days = itemView.findViewById(R.id.daysTextView);
        muscles = itemView.findViewById(R.id.musclesTextView);
        handleViewButton = itemView.findViewById(R.id.dragHandleButton);
    }
}