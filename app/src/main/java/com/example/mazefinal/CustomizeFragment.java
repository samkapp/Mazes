package com.example.mazefinal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mazefinal.databinding.FragmentCustomizeBinding;

/**
 * Fragment for user customization. Allows for users to change the color of the piece
 * they control in the maze.
 *
 * @author sam kapp
 */
public class CustomizeFragment extends Fragment {
    private FragmentCustomizeBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCustomizeBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

        // set to default color
        if (mainActivity != null) {
            binding.playerColorSquare.setBackgroundColor(mainActivity.playerColor);

            // Get the rgb of current color
            int currentColor = mainActivity.playerColor;
            int alpha = Color.alpha(currentColor);
            int red = Color.red(currentColor);
            int green = Color.green(currentColor);
            int blue = Color.blue(currentColor);

            // set seekbars
            binding.alphaSeekBar.setProgress(alpha);
            binding.redSeekBar.setProgress(red);
            binding.greenSeekBar.setProgress(green);
            binding.blueSeekBar.setProgress(blue);

            // listeners to update
            addSeekBarListeners();
        }

        return binding.getRoot();
    }

    /**
     * Adds a seekbar listener to update color view in real time
     */
    private void addSeekBarListeners() {
        binding.alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePlayerColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePlayerColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePlayerColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePlayerColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /**
     * Updates player color depending on the current seekbar values
     */
    private void updatePlayerColor() {
        int alpha = binding.alphaSeekBar.getProgress();
        int red = binding.redSeekBar.getProgress();
        int green = binding.greenSeekBar.getProgress();
        int blue = binding.blueSeekBar.getProgress();

        int newColor = Color.argb(alpha, red, green, blue);

        binding.playerColorSquare.setBackgroundColor(newColor);

        // update in main activity
        if (mainActivity != null) {
            mainActivity.playerColor = newColor;
            mainActivity.customized_player = true;
            mainActivity.achievementsCheck();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

