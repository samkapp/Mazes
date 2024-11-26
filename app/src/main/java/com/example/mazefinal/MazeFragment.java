package com.example.mazefinal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.content.ContextCompat;

import com.example.mazefinal.databinding.FragmentMazeBinding;

public class MazeFragment extends Fragment {

    private FragmentMazeBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMazeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MazeViewModel viewModel = new ViewModelProvider(requireActivity()).get(MazeViewModel.class);
        viewModel.getMazeData().observe(getViewLifecycleOwner(), maze -> {
            if (maze != null) {
                update(requireContext(), view, maze, viewModel.getRows().getValue(), viewModel.getCols().getValue());
            }
        });
        setupMovementButtons();
        updateScoreText();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Sets up the maze using GridLayout and View elements
     */
    public static void setImageViews(Context context, View view, int[][] maze, int rows, int cols) {
        GridLayout gridLayout = view.findViewById(R.id.maze_grid_layout);

        if (gridLayout != null) {
            gridLayout.removeAllViews();
        }

        // Set up the GridLayout
        gridLayout.setRowCount(rows);
        gridLayout.setColumnCount(cols);

        // Calculate size of each cell as a percentage of screen size
        int displayWidth = context.getResources().getDisplayMetrics().widthPixels;
        int displayHeight = context.getResources().getDisplayMetrics().heightPixels;
        int cellSize = Math.min((5 * displayWidth / 10) / cols, (5 * displayHeight / 10) / rows);

        // Get the current player color from MainActivity
        MainActivity activity = (MainActivity) context;
        final int playerColor = activity.playerColor;

        // Loop through each cell and create a View for each
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                View cellView = new View(context);

                // Set up the layout parameters
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                cellView.setLayoutParams(params);

                // Set the background color for the cell based on maze value
                int cellValue = maze[i][j];
                if (cellValue == 5) { // Cell value 5 represents the player
                    cellView.setBackgroundColor(playerColor); // Use the selected player color
                } else {
                    cellView.setBackgroundColor(getColorForCellValue(cellValue, context));
                }

                // Add the cell to the GridLayout
                gridLayout.addView(cellView);
            }
        }
    }

    /**
     * Helper function to get the color for a cell based on its value
     */
    private static int getColorForCellValue(int cellValue, Context context) {
        switch (cellValue) {
            case 0: return ContextCompat.getColor(context, R.color.maze_background); // Empty
            case 1: return ContextCompat.getColor(context, R.color.wall_background); // Wall
            case 2: return ContextCompat.getColor(context, R.color.start_background); // Start
            case 3: return ContextCompat.getColor(context, R.color.end_background); // End
            case 4: return ContextCompat.getColor(context, R.color.path_background); // Path
            case 6: return ContextCompat.getColor(context, R.color.outside_background); // Outside
            default: return ContextCompat.getColor(context, R.color.error_background); // Default (error case)
        }
    }

    /**
     * Updates the maze view
     */
    public void update(Context context, View view, int[][] maze, int rows, int cols) {
        setImageViews(context, view, maze, rows, cols);
    }

    public void setupMovementButtons() {
        View view = getView(); // Get the fragment's root view
        if (view != null) {
            Button btnUp = view.findViewById(R.id.btnUp);
            Button btnDown = view.findViewById(R.id.btnDown);
            Button btnLeft = view.findViewById(R.id.btnLeft);
            Button btnRight = view.findViewById(R.id.btnRight);

            btnUp.setOnClickListener(v -> movePlayer(0));
            btnDown.setOnClickListener(v -> movePlayer(2));
            btnLeft.setOnClickListener(v -> movePlayer(3));
            btnRight.setOnClickListener(v -> movePlayer(1));
        }
    }

    private void movePlayer(int direction) {
        // Logic for movement
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.movePlayer(direction);
        }
    }

    private void updateScoreText() {
        // Logic for movement
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.updateScoreText();
        }
    }
}

