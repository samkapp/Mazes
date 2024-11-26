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

public class CustomizeFragment extends Fragment {
    private FragmentCustomizeBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCustomizeBinding.inflate(inflater, container, false);

        // Initialize MainActivity reference to access playerColor
        mainActivity = (MainActivity) getActivity();

        // Set the initial color based on the current player color
        if (mainActivity != null) {
            binding.playerColorSquare.setBackgroundColor(mainActivity.playerColor);

            // Get the ARGB components from the current color
            int currentColor = mainActivity.playerColor;
            int alpha = Color.alpha(currentColor);
            int red = Color.red(currentColor);
            int green = Color.green(currentColor);
            int blue = Color.blue(currentColor);

            // Set the initial progress of the SeekBars
            binding.alphaSeekBar.setProgress(alpha);
            binding.redSeekBar.setProgress(red);
            binding.greenSeekBar.setProgress(green);
            binding.blueSeekBar.setProgress(blue);

            // Add listeners to the SeekBars to update the color dynamically
            addSeekBarListeners();
        }

        return binding.getRoot();
    }

    private void addSeekBarListeners() {
        // Listen to changes on the SeekBars and update the color in real-time
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

    private void updatePlayerColor() {
        // Get the current ARGB values from the SeekBars
        int alpha = binding.alphaSeekBar.getProgress();
        int red = binding.redSeekBar.getProgress();
        int green = binding.greenSeekBar.getProgress();
        int blue = binding.blueSeekBar.getProgress();

        // Combine them into a single ARGB color value
        int newColor = Color.argb(alpha, red, green, blue);

        // Update the player color square
        binding.playerColorSquare.setBackgroundColor(newColor);

        // Update the global color in MainActivity
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

