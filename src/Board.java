import java.util.Arrays;

public class Board {
    // ATTRIBUTES
    private char[][] grid;

    // CONSTRUCTOR
    public Board(int rows, int cols) {
        this.grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(grid[i], '.');
        }
    }

    public int getCols() {
        return grid[0].length;
    }

    public int getRows() {
        return grid.length;
    }

    public char[][] getGrid() {
        return grid;
    }
}
