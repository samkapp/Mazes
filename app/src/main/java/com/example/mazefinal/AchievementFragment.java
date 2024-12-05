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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mazefinal.databinding.FragmentAchievementBinding;

/**
 * Achievement Fragment
 * Shows a list of the achievements of the game, and provides either a checked or unchecked
 *  box to show if they have been completed. Allows for achievements to be reset
 *
 * @author sam kapp
 */
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateAchievements();

        Context context = getContext();
        if (context != null) {
            binding.resetAchievements.setOnClickListener(v -> {
                resetAllAchievements(context);
                Toast.makeText(context, "Achievements Reset", Toast.LENGTH_SHORT).show();
                populateAchievements();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Sets a given achievement to true
     */
    public static void setAchievementsCompleted(Context context, AchievementsEnum achievements, boolean isCompleted) {
        SharedPreferences prefs = context.getSharedPreferences(ACHIEVEMENTS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(achievements.name(), isCompleted);
        editor.apply();
    }

    /**
     * Checks if an achievement has been completed or not
     * @return true if the achievement is complete, false otherwise
     */
    public static boolean isAchievementsCompleted(Context context, AchievementsEnum achievements) {
        SharedPreferences prefs = context.getSharedPreferences(ACHIEVEMENTS_PREF, Context.MODE_PRIVATE);
        return prefs.getBoolean(achievements.name(), false);
    }

    /**
     * Resets all the saved achievements
     */
    public static void resetAllAchievements(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ACHIEVEMENTS_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();
    }

    /**
     * Uses a LinearLayout and fills it with all of the achievements, setting their
     *  checkbox as well.
     */
    public void populateAchievements() {
        LinearLayout achievementsContainer = getView().findViewById(R.id.achievements_container);

        if (achievementsContainer != null) {
            achievementsContainer.removeAllViews();

            // Loop through all achievements
            for (AchievementsEnum achievement : AchievementsEnum.values()) {
                // layout to hold achievement and checkbox
                LinearLayout achievementLayout = new LinearLayout(getContext());
                achievementLayout.setOrientation(LinearLayout.HORIZONTAL);
                achievementLayout.setPadding(16, 16, 16, 16);

                // achievement name
                TextView achievementText = new TextView(getContext());
                achievementText.setText(achievement.name());
                achievementText.setTextSize(18f);
                achievementText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                // achievement checkbox
                ImageView achievementIcon = new ImageView(getContext());
                boolean isCompleted = AchievementFragment.isAchievementsCompleted(getContext(), achievement);

                if (isCompleted) {
                    achievementIcon.setImageResource(R.drawable.checkmark);
                } else {
                    achievementIcon.setImageResource(R.drawable.blank_box);
                }

                // params
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(48, 48);
                iconParams.setMargins(16, 0, 0, 0);
                achievementIcon.setLayoutParams(iconParams);

                // add views
                achievementLayout.addView(achievementText);
                achievementLayout.addView(achievementIcon);

                // add to container
                achievementsContainer.addView(achievementLayout);
            }
        }
    }
}
