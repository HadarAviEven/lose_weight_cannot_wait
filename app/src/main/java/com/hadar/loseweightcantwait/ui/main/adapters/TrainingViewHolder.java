package com.hadar.loseweightcantwait.ui.main.adapters;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
    protected ImageButton handleViewButton;
    protected boolean handleButtonPressed;
//    protected ImageView trashImageView;

    public TrainingViewHolder(@NonNull View itemView) {
        super(itemView);

        findViews();
        handleButtonPressed = false;
    }

    private void findViews() {
        name = itemView.findViewById(R.id.nameTextView);
        days = itemView.findViewById(R.id.daysTextView);
        muscles = itemView.findViewById(R.id.musclesTextView);
        handleViewButton = itemView.findViewById(R.id.dragHandleButton);
//        trashImageView = itemView.findViewById(R.id.trashImageView);
    }

    public void setHandleButtonPressed(boolean isPressed) {
        handleButtonPressed = isPressed;
    }

    @Override
    public void onItemSelected() {
        Log.e("TrainingViewHolder", "onItemSelected");
        if (handleButtonPressed) {
            itemView.setBackgroundResource(R.drawable.card_view_pressed);
        }
    }

    @Override
    public void onItemClear() {
        Log.e("TrainingViewHolder", "onItemClear");
        if (handleButtonPressed) {
            itemView.setBackgroundResource(R.drawable.card_view);
            setHandleButtonPressed(false);
        }
    }
}