package com.example.mazefinal;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mazefinal.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // Values for maze game
    private Maze maze = new Maze(5); // sets maze to default size
    private int rows = maze.getRows();
    private int cols = maze.getCols();
    private int[] curPos = maze.getCurrentPos();
    private int score = 0;
    public boolean customized_player = false;
    public int playerColor = (int) R.color.player_background;

    // Layout values
    private TextView scoreTextView;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        scoreTextView = findViewById(R.id.scoreTextView);
        updateScoreText();

        if (savedInstanceState != null) {
            loadGameState(savedInstanceState);
        } else {
            // ViewModel for the Maze
            MazeViewModel viewModel = new ViewModelProvider(this).get(MazeViewModel.class);
            viewModel.setMaze(maze.getMaze(), rows, cols);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.achievements) {
            // Navigate to AchievementsFragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.AchievementFragment);
            return true;
        } else if (id == R.id.customize) {
            // Navigate to CustomizeFragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.CustomizeFragment);
            return true;
        } else if (id == R.id.main_menu) {
            // Navigate to MainMenuFragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.MainMenuFragment);
            return true;
        } else if (id == R.id.maze) {
            // Navigate to MainMenuFragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.MazeFragment);
        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Shows a custom toast
     */
    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                findViewById(R.id.toast_layout_root));

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, 0, 60);
        toast.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveGameState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loadGameState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScoreText();
    }



    /**
     * Saves the gameState
     */
    private void saveGameState(Bundle bundle) {
        // Save size
        bundle.putInt("Size", rows);

        // Save location
        bundle.putInt("PosRow", curPos[0]);
        bundle.putInt("PosCol", curPos[1]);

        // save entrance and exit
        int[] ent = new int[2];
        System.arraycopy(maze.getEntrance(), 0, ent, 0, 2);

        int[] ex = new int[2];
        System.arraycopy(maze.getExit(), 0, ex, 0, 2);

        bundle.putInt("EntRow", ent[0]);
        bundle.putInt("EntCol", ent[1]);
        bundle.putInt("ExRow", ex[0]);
        bundle.putInt("ExCol", ex[1]);

        // Save score
        bundle.putInt("Score", score);

        // Save the player color
        bundle.putInt("Color", playerColor);

        // Save Maze
        bundle.putSerializable("Maze", maze.copyGrid());

    }

    /**
     * Loads the gameState
     */
    private void loadGameState(Bundle bundle) {
        // retrieve size
        rows = bundle.getInt("Size");
        cols = rows;
        maze.setSize(rows);

        // retrieve location
        curPos[0] = bundle.getInt("PosRow");
        curPos[1] = bundle.getInt("PosCol");

        // retrieve maze
        maze.setMaze((int[][]) bundle.getSerializable("Maze"));
        maze.printMaze();

        // retrieve and set entrance and exit
        int[] ent = new int[2];
        ent[0] = bundle.getInt("EntRow");
        ent[1] = bundle.getInt("EntCol");

        int[] ex = new int[2];
        ex[0] = bundle.getInt("ExRow");
        ex[1] = bundle.getInt("ExCol");

        maze.setEntrance(ent);
        maze.setExit(ex);

        // retrieve score
        score = bundle.getInt("Score");
        updateScoreText();

        // retrieve playerColor
        playerColor = bundle.getInt("Color");
    }

    /**
     * Starts a new game
     */
    private void start(int size) {
        maze = new Maze(size);
    }


    public void movePlayer(int direction) {
        maze.move(direction);
        curPos = maze.getCurrentPos();

        if (maze.isOver()) {
            showCustomToast("Level Completed!");

            // update score
            score += rows * cols * 100;
            updateScoreText();

            // Add any achievements gotten
            achievementsCheck();

            rows += 2;
            cols += 2;

            start(rows); // Start a new level
        }

        // Update the ViewModel
        MazeViewModel viewModel = new ViewModelProvider(this).get(MazeViewModel.class);
        viewModel.setMaze(maze.getMaze(), rows, cols);
    }

    public void updateScoreText() {
        String str = "score: " + score;
        scoreTextView = findViewById(R.id.scoreTextView);
        if (scoreTextView != null)
            scoreTextView.setText(str);
    }

    public void achievementsCheck() {
        // Check GRID achievements based on rows and columns
        if (rows == 5 && cols == 5) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.GRID_5X5)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.GRID_5X5, true);
            }
        }
        if (rows == 7 && cols == 7) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.GRID_7X7)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.GRID_7X7, true);
            }
        }
        if (rows == 9 && cols == 9) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.GRID_9X9)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.GRID_9X9, true);
            }
        }
        if (rows == 11 && cols == 11) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.GRID_11X11)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.GRID_11X11, true);
            }
        }
        if (rows == 13 && cols == 13) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.GRID_13X13)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.GRID_13X13, true);
            }
        }

        // Check POINTS achievements based on score
        if (score >= 5000) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.POINTS_5000)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.POINTS_5000, true);
            }
        }
        if (score >= 10000) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.POINTS_10000)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.POINTS_10000, true);
            }
        }
        if (score >= 50000) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.POINTS_50000)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.POINTS_50000, true);
            }
        }
        if (score >= 100000) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.POINTS_100000)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.POINTS_100000, true);
            }
        }
        if (score >= 1000000) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.POINTS_1000000)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.POINTS_1000000, true);
            }
        }

        // Check customization achievement
        if (customized_player) {
            if (!AchievementFragment.isAchievementsCompleted(this, AchievementsEnum.CUSTOMIZE_PLAYER)) {
                AchievementFragment.setAchievementsCompleted(this, AchievementsEnum.CUSTOMIZE_PLAYER, true);
            }
        }
    }
}