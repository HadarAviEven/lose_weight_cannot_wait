package com.hadar.loseweightcantwait.ui.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hadar.loseweightcantwait.R;
import com.hadar.loseweightcantwait.ui.addtraining.models.Training;
import com.hadar.loseweightcantwait.ui.main.adapters.TrainingAdapter;
import com.hadar.loseweightcantwait.ui.main.recyclerviews.EmptyRecyclerView;
import com.hadar.loseweightcantwait.ui.main.viewmodels.TrainingsViewModel;

import java.util.List;

public class TrainingsFragment extends Fragment {

    private EmptyRecyclerView recyclerView;
    private TextView emptyView;
    private Button addButton;

    private TrainingAdapter trainingAdapter;
    private TrainingsViewModel trainingsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle();
        findViews(view);
        setOnClickAddButton();
        initTrainingRecyclerView();
        initTrainingAdapter();
        initTrainingViewModel();
        initItemTouchHelper();
    }

    private void setTitle() {
        this.requireActivity().setTitle(getString(R.string.app_name));
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        addButton = view.findViewById(R.id.addButton);
    }

    private void setOnClickAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAddTrainingFragment(v);
            }
        });
    }

    private void initTrainingRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(emptyView);
        recyclerView.initEmptyView();
    }

    private void initTrainingAdapter() {
        trainingAdapter = new TrainingAdapter(this.getActivity());
        recyclerView.setAdapter(trainingAdapter);

        trainingAdapter.setOnItemClickListener(new TrainingAdapter.ClickListener() {
            @Override
            public void onItemClick(Training training) {
                trainingsViewModel.onTrainingItemClicked(training);
            }
        });
    }

    private void initTrainingViewModel() {
        trainingsViewModel = ViewModelProviders.of(this).get(TrainingsViewModel.class);

        observe();
    }

    private void observe() {
        trainingsViewModel.getAll().observe(this.requireActivity(), new Observer<List<Training>>() {
            @Override
            public void onChanged(@Nullable final List<Training> trainings) {
                trainingAdapter.setData(trainings);
            }
        });

        trainingsViewModel.launchAddTrainingScreenLiveEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer trainingId) {
                launchUpdateTrainingFragment(trainingId);
            }
        });
    }

    private void initItemTouchHelper() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Training training = trainingAdapter.getTrainingAtPosition(position);
                trainingsViewModel.onTrainingItemSwiped(training);
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    private void launchAddTrainingFragment(View v) {
        Navigation.findNavController(v).navigate(R.id.trainings_to_add_training);
    }

    private void launchUpdateTrainingFragment(int trainingId) {
        final Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.training_id), trainingId);

        Navigation.findNavController(requireView())
                .navigate(R.id.trainings_to_add_training, bundle);
    }
}