package com.example.mazefinal;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

public class MazeFragment extends Fragment implements SensorEventListener {

    private FragmentMazeBinding binding;

    // Values for gyroscope
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private static final float TILT_THRESHOLD = 0.5f;
    private long lastMoveTime = 0; // Timestamp of last movement
    private static final long MOVE_DELAY = 200; // Minimum time between moves in milliseconds (200ms = 5 moves per second)


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

        // Init SensorManager
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }

        // Init ViewModel
        MazeViewModel viewModel = new ViewModelProvider(requireActivity()).get(MazeViewModel.class);
        viewModel.getMazeData().observe(getViewLifecycleOwner(), maze -> {
            if (maze != null) {
                update(requireContext(), view, maze, viewModel.getRows().getValue(), viewModel.getCols().getValue());
            }
        });

        setupMovementButtons();
        updateScoreText();
        updateMazeInfoText();
        startTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Lock the orientation when this fragment is visible
        lockOrientation();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unlock orientation when this fragment is no longer visible
        unlockOrientation();
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
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.movePlayer(direction);
        }
    }

    private void updateScoreText() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.updateScoreText();
        }
    }

    private void updateMazeInfoText() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.updateMazeInfoText();
        }
    }

    private void startTimer() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
                activity.startTimer();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            long currentTime = System.currentTimeMillis();

            // Only process movement if enough time has passed
            if (currentTime - lastMoveTime > MOVE_DELAY) {
                // Get the gyroscope data
                float x = event.values[0]; // Rotation around X-axis (forward/backward tilt)
                float y = event.values[1]; // Rotation around Y-axis (left/right tilt)

                // Check if the tilt exceeds the threshold and move player accordingly
                if (Math.abs(x) > TILT_THRESHOLD) {
                    if (x > 0) {
                        movePlayer(2); // Move up (forward tilt)
                    } else {
                        movePlayer(0); // Move down (backward tilt)
                    }
                }
                if (Math.abs(y) > TILT_THRESHOLD) {
                    if (y > 0) {
                        movePlayer(1); // Move right (right tilt)
                    } else {
                        movePlayer(3); // Move left (left tilt)
                    }
                }

                // Update the last movement time
                lastMoveTime = currentTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this functionality
    }

    /**
     * Lock the orientation to portrait when the MazeFragment is active.
     */
    private void lockOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    /**
     * Unlock the orientation when the MazeFragment is no longer active.
     */
    private void unlockOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
