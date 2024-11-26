package com.example.mazefinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mazefinal.databinding.FragmentAchievementBinding;

public class AchievementFragment extends Fragment {
    private FragmentAchievementBinding binding;
    private static final String ACHIEVEMENTS_PREF = "achievements_prefs";


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAchievementBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateAchievements();
        Context context = getContext();
        if (context != null) {
            binding.resetAchievements.setOnClickListener(v -> resetAllAchievements(context));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void setAchievementsCompleted(Context context, AchievementsEnum achievements, boolean isCompleted) {
        SharedPreferences prefs = context.getSharedPreferences(ACHIEVEMENTS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(achievements.name(), isCompleted);
        editor.apply();
    }

    public static boolean isAchievementsCompleted(Context context, AchievementsEnum achievements) {
        SharedPreferences prefs = context.getSharedPreferences(ACHIEVEMENTS_PREF, Context.MODE_PRIVATE);
        return prefs.getBoolean(achievements.name(), false);
    }

    public static void resetAllAchievements(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ACHIEVEMENTS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void populateAchievements() {
        LinearLayout achievementsContainer = getView().findViewById(R.id.achievements_container);
        // Ensure that achievementsContainer is non-null before accessing it
        if (achievementsContainer != null) {
            achievementsContainer.removeAllViews();

            // Loop through all achievements in the enum
            for (AchievementsEnum achievement : AchievementsEnum.values()) {
                // Create a horizontal layout to hold the TextView and ImageView
                LinearLayout achievementLayout = new LinearLayout(getContext());
                achievementLayout.setOrientation(LinearLayout.HORIZONTAL);
                achievementLayout.setPadding(16, 16, 16, 16);

                // Create a TextView for the achievement name
                TextView achievementText = new TextView(getContext());
                achievementText.setText(achievement.name());
                achievementText.setTextSize(18f);
                achievementText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                // Create an ImageView for the checkmark or blank box
                ImageView achievementIcon = new ImageView(getContext());
                boolean isCompleted = AchievementFragment.isAchievementsCompleted(getContext(), achievement);

                if (isCompleted) {
                    achievementIcon.setImageResource(R.drawable.checkmark);  // Completed achievement
                } else {
                    achievementIcon.setImageResource(R.drawable.blank_box);  // Uncompleted achievement
                }

                // Set LayoutParams for the ImageView
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(48, 48); // You can adjust the size
                iconParams.setMargins(16, 0, 0, 0);  // Add margin to separate the text and icon
                achievementIcon.setLayoutParams(iconParams);

                // Add TextView and ImageView to the layout
                achievementLayout.addView(achievementText);
                achievementLayout.addView(achievementIcon);

                // Add the individual layout to the container
                achievementsContainer.addView(achievementLayout);
            }
        }
    }
}
