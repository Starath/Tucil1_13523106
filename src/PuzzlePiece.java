import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PuzzlePiece {
    // ATTRIBUTES
    private char id;
    private char[][] shape;
    private List<char[][]> orientations;
    // private static int count = 0;
    
    // CONSTRUCTORS
    public PuzzlePiece(char id, char[][] shape) {
        this.id = id;
        this.shape = shape;
        this.orientations = generateAllOrientations();
    }

    private List<char[][]> generateAllOrientations() {
        List<char[][]> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        
        char[][] current = shape;
        for (int i = 0; i < 4; i++) {
            addIfUnique(current, result, seen);
            char[][] flipped = flipHorizontally(current);
            addIfUnique(flipped, result, seen);
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
    
    // Converts Puzzle Piece into a String. (for HashSet's Keys)
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
    public char getId() {
        return id;
    }

    public char[][] getShape() {
        return shape;
    }

    public List<char[][]> getOrientations() {
        return orientations;
    }

}
