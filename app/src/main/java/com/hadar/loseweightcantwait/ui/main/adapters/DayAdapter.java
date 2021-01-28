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
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DayAdapter extends RecyclerView.Adapter<DayViewHolder> {
    private Context context;
    private ArrayList<Day> days;

    public DayAdapter(Context context) {
        this.context = context;

        ArrayList<DayEnum> daysEnum = new ArrayList<>(Arrays.asList(DayEnum.values()));
        days = new ArrayList<>();
        for (DayEnum dayEnum : daysEnum) {
            days.add(new Day(dayEnum));
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DayViewHolder holder, final int position) {
        Day currentItem = days.get(position);

        setText(holder, currentItem);
        setColor(holder, currentItem);
        setListener(holder, position);
    }

    private void setText(DayViewHolder holder, Day currentItem) {
        holder.dayButton.setText(currentItem.getDayEnum().toString());
    }

    private void setColor(DayViewHolder holder, Day currentItem) {
        if (currentItem.isSelected()) {
            dayButtonTurnOn(holder.dayButton);
        } else {
            dayButtonTurnOff(holder.dayButton);
        }
    }

    private void setListener(DayViewHolder holder, final int position) {
        holder.dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button dayButton = (Button) v;

                if (!days.get(position).isSelected()) {
                    days.get(position).setSelected(true);
                    dayButtonTurnOn(dayButton);
                } else {
                    days.get(position).setSelected(false);
                    dayButtonTurnOff(dayButton);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (days == null)
            return 0;
        return days.size();
    }

    private void dayButtonTurnOn(Button dayButton) {
        dayButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        dayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        dayButton.setBackground(
                ContextCompat.getDrawable(context, R.drawable.selected_round_button));
    }

    private void dayButtonTurnOff(Button dayButton) {
        dayButton.setTextColor(ContextCompat.getColor(context, R.color.black));
        dayButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
        dayButton.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button));
    }

    public ArrayList<DayEnum> getSelectedDays() {
        ArrayList<DayEnum> selectedDays = new ArrayList<>();
        for (Day day : this.days) {
            if (day.isSelected()) {
                selectedDays.add(day.getDayEnum());
            }
        }
        return selectedDays;
    }

    public void setSelectedDays(ArrayList<DayEnum> selectedDays) {
        for (int i = 0; i < selectedDays.size(); i++) {
            for (int j = i; j < days.size(); j++) {
                if (selectedDays.get(i).equals(days.get(j).getDayEnum())) {
                    days.get(j).setSelected(true);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }
}