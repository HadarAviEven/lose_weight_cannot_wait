package com.hadar.loseweightcantwait.ui.main.adapters;

import android.view.View;
import android.widget.Button;

import com.hadar.loseweightcantwait.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DayViewHolder extends RecyclerView.ViewHolder {
    protected Button dayButton;

    public DayViewHolder(@NonNull View itemView) {
        super(itemView);

        findViews();
    }

    private void findViews() {
        dayButton = itemView.findViewById(R.id.dayButton);
    }
}