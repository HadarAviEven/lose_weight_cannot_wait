package com.hadar.loseweightcantwait.ui.main.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.utilities.ItemTouchHelperViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrainingViewHolder extends RecyclerView.ViewHolder
        implements ItemTouchHelperViewHolder {
    protected TextView name;
    protected TextView days;
    protected TextView muscles;
    protected Button handleViewButton;

    public TrainingViewHolder(@NonNull View itemView) {
        super(itemView);

        findViews();
    }

    private void findViews() {
        name = itemView.findViewById(R.id.nameTextView);
        days = itemView.findViewById(R.id.daysTextView);
        muscles = itemView.findViewById(R.id.musclesTextView);
        handleViewButton = itemView.findViewById(R.id.dragHandleButton);
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundResource(R.drawable.card_view_pressed);
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundResource(R.drawable.card_view);
    }
}