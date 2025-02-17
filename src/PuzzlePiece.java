import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PuzzlePiece {
    // ATTRIBUTES
    private char id;
    private char[][] shape;
    private int rows;
    private int cols;
    private List<char[][]> orientations;
    
    // CONSTRUCTORS
    public PuzzlePiece(char id, List<String> shapeLines) {
        this.id = id;
        this.rows = shapeLines.size();
        this.cols = shapeLines.get(0).length();
        this.shape = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            this.shape[i] = shapeLines.get(i).toCharArray();
        }
        this.orientations = generateAllOrientations();
    }

    private List<char[][]> generateAllOrientations() {
        List<char[][]> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        
        char[][] current = shape;
        for (int i = 0; i < 4; i++) {
            // Add the current rotation.
            addIfUnique(current, result, seen);
            // Add its horizontally flipped version.
            char[][] flipped = flipHorizontally(current);
            addIfUnique(flipped, result, seen);
            // Rotate for the next iteration.
            current = rotate90(current);
        }
        
        return result;
    }
    
    // NOTE: generateAllOrientations() HELPER
    // Add Puzzle Piece orientation to the list if it is unique.
    private void addIfUnique(char[][] mat, List<char[][]> list, Set<String> seen) {
        String key = matrixToString(mat);
        if (!seen.contains(key)) {
            seen.add(key);
            list.add(copyMatrix(mat));
        }
    }
    
    // Converts Puzzle Piece into a String representation. (for HashSet's Keys)
    private String matrixToString(char[][] mat) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : mat) {
            sb.append(new String(row));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    // Creates a deep copy of a Puzzle Piece.
    private char[][] copyMatrix(char[][] mat) {
        int r = mat.length;
        int c = mat[0].length;
        char[][] copy = new char[r][c];
        for (int i = 0; i < r; i++) {
            System.arraycopy(mat[i], 0, copy[i], 0, c);
        }
        return copy;
    }
    
    // Rotates Puzzle Piece 90 degrees clockwise.
    private char[][] rotate90(char[][] mat) {
        int r = mat.length;
        int c = mat[0].length;
        char[][] rotated = new char[c][r];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                rotated[j][r - 1 - i] = mat[i][j];
            }
        }
        return rotated;
    }
    
    // Flips Puzzle Piece horizontally.
    private char[][] flipHorizontally(char[][] mat) {
        int r = mat.length;
        int c = mat[0].length;
        char[][] flipped = new char[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                flipped[i][c - 1 - j] = mat[i][j];
            }
        }
        return flipped;
    }

    //SELECTOR
    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public char getId() {
        return id;
    }

    public char[][] getShape() {
        return shape;
    }


}
