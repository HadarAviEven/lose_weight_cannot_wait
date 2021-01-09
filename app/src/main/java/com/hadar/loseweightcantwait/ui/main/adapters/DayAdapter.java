package com.hadar.loseweightcantwait.ui.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.enums.DayEnum;
import com.hadar.loseweightcantwait.ui.addtraining.models.Day;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DayAdapter extends RecyclerView.Adapter<DayViewHolder> {
    private Context context;
    private ArrayList<DayEnum> daysEnumArrayList;
    private ArrayList<Day> daysArrayList;

    public DayAdapter(Context context, ArrayList<DayEnum> days) {
        this.context = context;
        this.daysEnumArrayList = days;

        daysArrayList = new ArrayList<>();
        for (DayEnum dayEnum : daysEnumArrayList) {
            daysArrayList.add(new Day(dayEnum));
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DayViewHolder holder, final int position) {
        DayEnum currentItem = daysEnumArrayList.get(position);
        holder.dayButton.setText(currentItem.toString());
        setListener(holder, position);
    }

    @Override
    public int getItemCount() {
        if (daysEnumArrayList == null)
            return 0;
        return daysEnumArrayList.size();
    }

    private void setListener(DayViewHolder holder, final int position) {
        holder.dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button dayButton = (Button) v;

                if (!daysArrayList.get(position).isPressed()) {
                    daysArrayList.get(position).setPressed(true);
                    dayButtonTurnOn(dayButton);
                } else {
                    daysArrayList.get(position).setPressed(false);
                    dayButtonTurnOff(dayButton);
                }
            }
        });
    }

    private void dayButtonTurnOn(Button dayButton) {
        dayButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        dayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        dayButton.setBackground(ContextCompat.getDrawable(context, R.drawable.selected_round_button));
    }

    private void dayButtonTurnOff(Button dayButton) {
        dayButton.setTextColor(ContextCompat.getColor(context, R.color.black));
        dayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
        dayButton.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button));
    }

    public ArrayList<DayEnum> getSelectedDaysEnum() {
        ArrayList<DayEnum> selectedDaysEnum = new ArrayList<>();
        for (Day day : this.daysArrayList) {
            if (day.isPressed()) {
                selectedDaysEnum.add(day.getDayEnum());
            }
        }
        return selectedDaysEnum;
    }
}