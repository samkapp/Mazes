package com.example.mazefinal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MazeViewModel extends ViewModel {
    private final MutableLiveData<int[][]> mazeData = new MutableLiveData<>();
    private final MutableLiveData<Integer> rows = new MutableLiveData<>();
    private final MutableLiveData<Integer> cols = new MutableLiveData<>();

    public void setMaze(int[][] maze, int rows, int cols) {
        this.rows.setValue(rows);
        this.cols.setValue(cols);
        mazeData.setValue(maze);
    }

    public LiveData<int[][]> getMazeData() { return mazeData; }
    public LiveData<Integer> getRows() { return rows; }
    public LiveData<Integer> getCols() { return cols; }

}
