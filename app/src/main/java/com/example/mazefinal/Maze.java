package com.example.mazefinal;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Uses Kruskal's algorithm to create a random maze
 * refs used:
 *  https://www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/
 *
 *
 * @author Sam Kapp
 */
public class Maze {
    // Values for the Maze
    private int rows;
    private int cols;

    int[][] maze;
    private final List<Edge> edges = new ArrayList<>();

    private static final int[] entrance = new int[2];
    private static final int[] exit = new int[2];

    // Values for the Game
    private final Stack<int[]> path = new Stack<>();
    private int[] currentPos = new int[2];

    /**
     * Constructor
     *
     * @param size the size of the rows/cols
     */
    public Maze(int size) {
        // need the rows/cols to be odd
        if (size % 2 == 0) size++;

        rows = size;
        cols = size;

        this.maze = new int[this.rows][this.cols];

        // init maze with all walls
        for (int[] ints : this.maze) {
            Arrays.fill(ints, 1);
        }

        // set the cells for the path
        for (int i = 1; i < rows; i+=2) {
            for (int j = 1; j < cols; j+=2) {
                this.maze[i][j] = 0;
            }
        }

        // Generate edges, run kruskal's algo, and set the entrance/exit
        this.generateEdges();
        this.kruskal();
        this.addEntranceAndExit();
        this.initGame();
    }

    /**
     * Initializes the components for the game
     */
    private void initGame() {
        currentPos[0] = entrance[0];
        currentPos[1] = entrance[1];
        setTile(entrance[0], entrance[1], 5);
        path.add(currentPos);
    }

    /**
     * Generates all possible edges between adajcent cells in the maze, where
     * edges are spots where walls are able to be removed
     */
    private void generateEdges() {
        for (int i = 0; i < rows / 2; i++) {
            for (int j = 0; j < cols / 2; j++) {
                // horizontal edges
                if (j < cols / 2 - 1) {
                    edges.add(new Edge(i, j, i, j+1));
                }

                // vertical edges
                if (i < rows / 2 - 1) {
                    edges.add(new Edge(i, j, i+1, j));
                }
            }
        }
        // randomize edges
        Collections.shuffle(edges);
    }

    /**
     * Identifies the root of a cell
     */
    private int find(int[] parent, int v) {
        if (parent[v] != v) {
            parent[v] = find(parent, parent[v]);
        }
        return parent[v];
    }

    /**
     * Combines two sets if they are not already combined
     */
    private void union(int[] parent, int[] rank, int u, int v) {
        int rootU = find(parent, u);
        int rootV = find(parent, v);

        if (rootU != rootV) {
            parent[rootV] = rootU;
        } else if (rank[rootU] < rank[rootV]) {
            parent[rootU] = rootV;
        } else {
            parent[rootV] = rootU;
            rank[rootU]++;
        }
    }

    /**
     * Performs Kruskal's algorithm on the edges and modifies the maze
     * so that there are valid paths
     */
    private void kruskal() {
        int[] parent = new int[this.rows * this.cols];
        int[] rank = new int[this.rows * this.cols];

        for (int i = 0; i < parent.length; ++i) {
            parent[i] = i;
            rank[i] = 0;
        }

        for (Edge edge : this.edges) {
            int u = edge.x1 * this.cols + edge.y1;
            int v = edge.x2 * this.cols + edge.y2;
            if (this.find(parent, u) != this.find(parent, v)) {
                int mazeX1 = edge.x1 * 2 + 1;
                int mazeY1 = edge.y1 * 2 + 1;
                int mazeX2 = edge.x2 * 2 + 1;
                int mazeY2 = edge.y2 * 2 + 1;
                this.maze[(mazeX1 + mazeX2) / 2][(mazeY1 + mazeY2) / 2] = 0;
                this.union(parent, rank, u, v);
            }
        }
    }

    /**
     * Adds an entrance and exit randomly on the top and bottom rows of the maze
     */
    private void addEntranceAndExit() {
        Random random = new Random();

        int startX = random.nextInt(cols - 1) + 1;
        int endX = random.nextInt(cols - 1) + 1;

        // Make sure chosen numbers are valid starting and ending points
        while (maze[1][startX] == 1 || maze[rows - 2][endX] == 1) {
            startX = random.nextInt(cols - 1) + 1;
            endX = random.nextInt(cols - 1) + 1;
        }

        maze[0][startX] = 2; // Start point
        maze[rows - 1][endX] = 3; // End point

        // Set starting and ending positions
        entrance[0] = 0;
        entrance[1] = startX;
        exit[0] = rows - 1;
        exit[1] = endX;
    }

    /**
     * Moves the player in the given direction if the move is valid.
     *
     * @param direction an integer representing the direction of movement:
     *                  0 = North, 1 = East, 2 = South, 3 = West
     */
    public boolean move(int direction) {
        // Make sure the player can move in the given direction
        if (!isValidMove(direction)) {
            return false;
        }

        Log.d("POSITION: ", "ROW: " + currentPos[0] + " COL: " + currentPos[1]);
        int curRow = currentPos[0];
        int curCol = currentPos[1];

        // Calculate the new position based on the direction
        switch (direction) {
            case 0: // North
                curRow -= 1;
                break;
            case 1: // East
                curCol += 1;
                break;
            case 2: // South
                curRow += 1;
                break;
            case 3: // West
                curCol -= 1;
                break;
        }

        // Mark the old player's tile
        setTile(currentPos[0], currentPos[1], 4); // 4 represents players path

        // Update the player's location
        currentPos[0] = curRow;
        currentPos[1] = curCol;

        // Mark the player's new position on the maze
        setTile(currentPos[0], currentPos[1], 5);  // 5 represents the player's position


        // Add the new position to the path
        path.add(currentPos.clone());

        return true;

    }

    /**
     * Checks if the move is valid by seeing if the tile directly next to the player in the given direction is not a wall.
     *
     * @param direction an integer representing the direction of movement:
     *                  0 = North, 1 = East, 2 = South, 3 = West
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int direction) {
        int curRow = currentPos[0];
        int curCol = currentPos[1];

        // Calculate the next tile based on the direction
        int newRow = curRow;
        int newCol = curCol;

        switch (direction) {
            case 0: // North
                newRow -= 1;
                break;
            case 1: // East
                newCol += 1;
                break;
            case 2: // South
                newRow += 1;
                break;
            case 3: // West
                newCol -= 1;
                break;
            default:
                return false;  // Invalid direction
        }

        // check that newRow and newCol are in bounds
        if (newRow < 0 || newRow > rows || newCol < 0 || newCol > cols)
            return false;

        // Check that the new position is not a wall
        return maze[newRow][newCol] != 1;
    }

    /**
     * @return true if the game is over (player standing on exit tile) false otherwise
     */
    public boolean isOver() {
        return currentPos[0] == exit[0] && currentPos[1] == exit[1];
    }

    /**
     * Prints out the maze
     */
    public void printMaze() {
        for (int[] row : this.maze) {
            for (int cell : row) {
                if (cell == 0) {
                    System.out.print("  ");
                } else if (cell == 1) {
                    System.out.print("# ");
                } else if (cell == 5) {
                    System.out.print("! ");
                } else {
                    System.out.print(cell + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /*  returns a copy of a maze */
    public int[][] copyGrid() {
        int[][] copy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(maze[i], 0, copy[i], 0, cols);
        }
        return copy;
    }

    /* row getter */
    public int getRows() { return this.rows; }

    /* col getter */
    public int getCols() { return this.cols; }

    /* maze getter */
    public int[][] getMaze() { return this.maze; }

    /* gets the current position */
    public int[] getCurrentPos() {return currentPos; }

    /* entrance getter */
    public int[] getEntrance() { return entrance; }

    /* exit getter */
    public int[] getExit() { return exit; }

    /* sets the current position */
    public void setCurrentPos(int[] pos) {currentPos[0] = pos[0]; currentPos[1] = pos[1]; }

    /* sets the entrance */
    public void setEntrance(int[] ent) {entrance[0] = ent[0]; entrance[1] = ent[1]; }

    /* sets the exit */
    public void setExit(int[] ex) {exit[0] = ex[0]; exit[1] = ex[1];}

    /* sets maze */
    public void setMaze(int[][] maze) {this.maze = maze; }

    /* sets the given tile to the given value */
    private void setTile(int row, int col, int val) { maze[row][col] = val; }

    /* sets the size of the maze */
    public void setSize(int size) {rows = size; cols = size;}

    /**
     * Edge class fo ruse in the Maze Class above
     */
    private static class Edge {
        int x1;
        int x2;
        int y1;
        int y2;

        Edge(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}
