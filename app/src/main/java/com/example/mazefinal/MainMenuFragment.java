package com.example.mazefinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mazefinal.databinding.FragmentMainMenuBinding;

public class MainMenuFragment extends Fragment {
    private FragmentMainMenuBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonAchievements.setOnClickListener(v ->
                NavHostFragment.findNavController(MainMenuFragment.this)
                        .navigate(R.id.action_MainMenuFragment_to_AchievementFragment)
        );
        binding.buttonCustomization.setOnClickListener(v ->
                NavHostFragment.findNavController(MainMenuFragment.this)
                        .navigate(R.id.action_MainMenuFragment_to_CustomizationFragment)
        );
        binding.buttonStart.setOnClickListener(v ->
                NavHostFragment.findNavController(MainMenuFragment.this)
                        .navigate(R.id.action_MainMenuFragment_to_MazeFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}