import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    // ATTRIBUTES
    private char[][] grid;
    private List<PuzzlePiece> pieces;
    private String config;
    private int validGrids;

    static final String[] ANSI_COLORS = {
        "\u001B[30m", // Hitam
        "\u001B[31m", // Merah
        "\u001B[32m", // Hijau
        "\u001B[33m", // Kuning
        "\u001B[34m", // Biru
        "\u001B[35m", // Ungu
        "\u001B[36m", // Cyan
        "\u001B[37m", // Putih
        "\u001B[90m", // Hitam terang
        "\u001B[91m", // Merah terang
        "\u001B[92m", // Hijau terang
        "\u001B[93m", // Kuning terang
        "\u001B[94m", // Biru terang
        "\u001B[95m", // Ungu terang
        "\u001B[96m", // Cyan terang
        "\u001B[97m", // Putih terang
        "\u001B[40m", // Background Hitam
        "\u001B[41m", // Background Merah
        "\u001B[42m", // Background Hijau
        "\u001B[43m", // Background Kuning
        "\u001B[44m", // Background Biru
        "\u001B[45m", // Background Ungu
        "\u001B[46m", // Background Cyan
        "\u001B[47m", // Background Putih
        "\u001B[100m", // Background Hitam terang
        "\u001B[107m"  // Background Putih terang
    };
    
    static final String ANSI_RESET = "\u001B[0m";
    

    // CONSTRUCTOR
    public Board(int rows, int cols, String config, List<PuzzlePiece> pieces) {
        this.grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(grid[i], '.');
        }
        this.pieces = pieces;
        this.config = config;
        this.validGrids = rows * cols;
    }

    // SELECTOR
    public String getConfig() {
        return config;
    }

    public int getValidGrids() {
        return validGrids;
    }

    public List<PuzzlePiece> getPieces() { 
        return pieces; 
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

    // Setter
    public void setGrid(char[][] grid) {
        this.grid = grid;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setPieces(List<PuzzlePiece> pieces) {
        this.pieces = pieces;
    }

    public void setValidGrids(int validGrids) {
        this.validGrids = validGrids;
    }

    // FUNCTION
    public int[] findEmpty(){
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '.') {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public boolean canPlace(char[][] pieceMat, int r, int c) {
        int pr = pieceMat.length;
        int pc = pieceMat[0].length;
        int br = grid.length;
        int bc = grid[0].length;
        if (r + pr > br || c + pc > bc) return false;
        for (int i = 0; i < pr; i++) {
            for (int j = 0; j < pc; j++) {
                if (pieceMat[i][j] != '.' && grid[r+i][c+j] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    public void placePiece(char[][] piece, int r, int c, int puzzlePieceIdx) {
        int puzzleHeight = piece.length;
        int puzzleWidth = piece[0].length;
        for (int i = r; i < r + puzzleHeight; i++) {
            for (int j = c; j < c + puzzleWidth; j++) {
                if (grid[i][j] != '.') continue;
                grid[i][j] = piece[i-r][j-c];
            }
        }
    }

    public void removePiece(char[][] piece, int r, int c, char id) {
        int puzzleHeight = piece.length;
        int puzzleWidth = piece[0].length;
        for (int i = r; i < r + puzzleHeight; i++) {
            for (int j = c; j < c + puzzleWidth; j++) {
                if (grid[i][j] == id) grid[i][j] = '.';
            }
        }
    }
    public void printColored() {
        Map<Character, String> colorMap = new HashMap<>();
        int rows = grid.length;
        int cols = grid[0].length;
        int colorIndex = 0;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                char ch = grid[i][j];
                if(ch != '.' && !colorMap.containsKey(ch)){
                    colorMap.put(ch, ANSI_COLORS[colorIndex % ANSI_COLORS.length]);
                    colorIndex++;
                }
            }
        }
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                char ch = grid[i][j];
                if(ch == '.')
                    System.out.print(ch);
                else
                    System.out.print(colorMap.get(ch) + ch + ANSI_RESET);
            }
            System.out.println();
        }
    }
}
