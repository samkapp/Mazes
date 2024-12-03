package com.example.mazefinal;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mazefinal.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    // Values for maze game
    private Maze maze = new Maze(5); // sets maze to default size
    private int rows = maze.getRows();
    private int cols = maze.getCols();
    private int[] curPos = maze.getCurrentPos();
    private long score = 0;
    private int wins = 0;

    // player colors
    public boolean customized_player = false;
    public int playerColor =  R.color.player_background;

    // Layout values
    private TextView scoreTextView;
    private TextView mazeInfoTextView;
    private TextView mazeTimeTextView;

    // Timer values
    private CountDownTimer countDownTimer;
    private long timeRemaining = 10000; //90000;
    private final long GAME_TIME = 10000;
    private boolean isGameOver = false;

    // Sound Manager
    SoundManager soundManager;


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

        mazeInfoTextView = findViewById(R.id.mazeInfoTextView);
        updateMazeInfoText();

        mazeTimeTextView = findViewById(R.id.mazeTimeText);
        updateTimeText();

        soundManager = new SoundManager(this);

        if (savedInstanceState != null) {
            loadGameState();
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
        soundManager.playClickSound();

        if (id == R.id.achievements) {
            // Navigate to AchievementsFragment
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            achievementsCheck();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.AchievementFragment);
            return true;
        } else if (id == R.id.customize) {
            // Navigate to CustomizeFragment
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.CustomizeFragment);
            return true;
        } else if (id == R.id.main_menu) {
            // Navigate to MainMenuFragment
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.MainMenuFragment);
            return true;
        } else if (id == R.id.maze) {
            // Navigate to MazeFragment
            newGame();
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
        saveGameState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loadGameState();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScoreText();
    }


    private void saveGameState() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Save maze as a JSON string
        String mazeJson = gson.toJson(maze);
        editor.putString("MazeJson", mazeJson);

        // Save size
        editor.putInt("Size", rows);

        // Save location
        editor.putInt("PosRow", curPos[0]);
        editor.putInt("PosCol", curPos[1]);

        // Save entrance and exit
        int[] ent = new int[2];
        System.arraycopy(maze.getEntrance(), 0, ent, 0, 2);

        int[] ex = new int[2];
        System.arraycopy(maze.getExit(), 0, ex, 0, 2);

        editor.putInt("EntRow", ent[0]);
        editor.putInt("EntCol", ent[1]);
        editor.putInt("ExRow", ex[0]);
        editor.putInt("ExCol", ex[1]);

        // Save score
        editor.putLong("Score", score);

        // Save level
        editor.putInt("Level", wins);

        // Save the player color
        editor.putInt("Color", playerColor);

        // Commit changes
        editor.apply();
    }

    private void loadGameState() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        // Retrieve maze JSON string and deserialize it back into a Maze object
        String mazeJson = sharedPreferences.getString("MazeJson", "");
        Gson gson = new Gson();
        maze = gson.fromJson(mazeJson, Maze.class);

        // Retrieve size
        rows = sharedPreferences.getInt("Size", 0);
        cols = rows;
        maze.setSize(rows);

        // Retrieve location
        curPos[0] = sharedPreferences.getInt("PosRow", 0);
        curPos[1] = sharedPreferences.getInt("PosCol", 0);

        maze.printMaze(); // This would show the loaded maze

        // Retrieve and set entrance and exit
        int[] ent = new int[2];
        ent[0] = sharedPreferences.getInt("EntRow", 0);
        ent[1] = sharedPreferences.getInt("EntCol", 0);

        int[] ex = new int[2];
        ex[0] = sharedPreferences.getInt("ExRow", 0);
        ex[1] = sharedPreferences.getInt("ExCol", 0);

        maze.setEntrance(ent);
        maze.setExit(ex);

        // Retrieve score
        score = sharedPreferences.getLong("Score", 0);
        updateScoreText();

        // Retrieve level
        wins = sharedPreferences.getInt("Level", 0);
        updateMazeInfoText();

        // Retrieve player color
        playerColor = sharedPreferences.getInt("Color", 0);
    }



    /**
     * Starts a new maze
     */
    private void start(int size) {
        soundManager.playNewGameSound();
        maze = new Maze(size);
    }

    /**
     * Starts a new game, resets progress from last game
     */
    private void newGame() {
        wins = 0;
        score = 0;
        rows = 5;
        cols = 5;
        start(rows);
        timeRemaining = GAME_TIME;
        startTimer();
        isGameOver = false;
        updateMazeInfoText();
        updateScoreText();

        // Update the ViewModel
        MazeViewModel viewModel = new ViewModelProvider(this).get(MazeViewModel.class);
        viewModel.setMaze(maze.getMaze(), rows, cols);
    }



    /**
     * Moves the player in the given direction, checks for wins,
     */
    public void movePlayer(int direction) {
        if (!maze.move(direction)) {
            soundManager.playErrorSound();
        }
        curPos = maze.getCurrentPos();

        if (maze.isOver()) {
            showCustomToast("Level Completed!");

            // Calculate the time bonus based on the remaining time
            long timeBonus =  (timeRemaining / 1000); // Convert time remaining to seconds

            // Update the score: rows * cols * wins * 100 + time bonus
            score += (rows * cols * (wins+1) * (100 - timeBonus));
            updateScoreText();

            // Check if we need to increase maze size every 5 wins
            wins++;
            if (wins % 5 == 0) {
                rows += 2;
                cols += 2;
            }
            start(rows);

            updateMazeInfoText();  // Update the maze size and attempt display
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

    public void updateMazeInfoText() {
        String str = "Level: " + wins;
        mazeInfoTextView = findViewById(R.id.mazeInfoTextView);
        if (mazeInfoTextView != null)
            mazeInfoTextView.setText(str);
    }

    public void updateTimeText() {
        String str = (timeRemaining / 1000) + "s";
        mazeTimeTextView = findViewById(R.id.mazeTimeText);
        if (mazeTimeTextView != null)
            mazeTimeTextView.setText(str);

    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimeText();

                if (timeRemaining  <= 3000)
                    soundManager.playTimerSound();

            }

            @Override
            public void onFinish() {
                if (!isGameOver) {
                    endGame();
                }
            }
        };
        countDownTimer.start();
    }

    public void endGame() {
        soundManager.playGameOverSound();
        isGameOver = true;
        saveScore();

    }

    private void showGameOverDialog(boolean isScoreInTop10) {
        // Create a custom dialog or popup to show the score and the top 10
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_game_over, null);

        // Set the final score
        TextView scoreTextView = dialogView.findViewById(R.id.final_score);
        scoreTextView.setText("Your Score: " + score);

        // Set the top 10 scores
        String topScores = getTopScores();
        TextView topScoresTextView = dialogView.findViewById(R.id.top_scores);
        topScoresTextView.setText(topScores);

        // Create a dialog to show the scores
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false); // Make the dialog non-dismissible by tapping outside

        // Set the action for New Game button
        builder.setPositiveButton("New Game", (dialog, which) -> {
            newGame();

            // Update the ViewModel
            MazeViewModel viewModel = new ViewModelProvider(this).get(MazeViewModel.class);
            viewModel.setMaze(maze.getMaze(), rows, cols);
            dialog.dismiss();
        });

        // Set the action for Main Menu button
        builder.setNegativeButton("Main Menu", (dialog, which) -> {
            // Navigate to MainMenuFragment
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.MainMenuFragment);
            dialog.dismiss();
        });

        builder.create().show();
    }


    /**
     * Retrieves the top 10 scores from SharedPreferences
     */
    private String getTopScores() {
        SharedPreferences prefs = getSharedPreferences("game_scores", MODE_PRIVATE);
        Set<String> topScores = prefs.getStringSet("top_scores", new HashSet<>());

        // Sort the scores in descending order
        List<Integer> sortedScores = new ArrayList<>();
        for (String scoreStr : topScores) {
            sortedScores.add(Integer.parseInt(scoreStr));
        }
        Collections.sort(sortedScores, Collections.reverseOrder()); // Sort descending

        // Format the top scores as a string
        StringBuilder topScoresString = new StringBuilder();
        int position = 1;
        for (Integer score : sortedScores) {
            topScoresString.append(position).append(". ").append(score).append("\n");
            position++;
        }

        return topScoresString.toString();
    }


    /**
     * Saves the current score to the list of top scores in SharedPreferences
     */
    private void saveScore() {
        // Save the current score to the list of top scores
        SharedPreferences prefs = getSharedPreferences("game_scores", MODE_PRIVATE);
        Set<String> topScores = prefs.getStringSet("top_scores", new HashSet<>());

        // Add the new score
        topScores.add(String.valueOf(score));

        // Sort the scores and keep the top 10
        List<Integer> sortedScores = new ArrayList<>();
        for (String scoreStr : topScores) {
            sortedScores.add(Integer.parseInt(scoreStr));
        }
        Collections.sort(sortedScores, Collections.reverseOrder()); // Sort descending

        // Keep only the top 10 scores
        if (sortedScores.size() > 10) {
            sortedScores = sortedScores.subList(0, 10);
        }

        // Save back the sorted top 10 scores
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("top_scores", new HashSet<>(sortedScores.stream()
                .map(String::valueOf)
                .collect(Collectors.toList())));
        editor.apply();

        // Now, check if the player's score is in the top 10
        if (sortedScores.contains((int) score)) {
            showGameOverDialog(true);
        } else {
            showGameOverDialog(false);
        }
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