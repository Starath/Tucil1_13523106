import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOPuzzlerFile {
    public static final Scanner sc = new Scanner(System.in);
    
    public static Board readInputFile() {
        inputLoop:
        while (true){
            PuzzlePiece.resetCount();
            Path filePath = readFileName("Enter the test case file name (e.g., test.txt): ");
            while (!Files.exists(filePath)) {
                filePath = readFileName("Enter the test case file name (e.g., test.txt): ");
            }
        
            List<String> allLines;
            try {
                allLines = Files.readAllLines(filePath);
            } catch (IOException e) {
                System.err.printf("Error reading file: %s%n", e.getMessage());
                continue;
            }
        
            List<String> nonEmpty = new ArrayList<>();
            for (String line : allLines) {
                if (!line.trim().isEmpty()) nonEmpty.add(line);
            }
            if (nonEmpty.size() < 2) {
                System.err.println("File format is incorrect.");
                continue;
            }
            
            String[] firstLine = nonEmpty.get(0).trim().split("\\s+");
            if (firstLine.length < 3) {
                System.err.println("Invalid format for the first line.");
                continue;
            }
        
            int rows, cols, nPieces;
            try {
                rows = Integer.parseInt(firstLine[0]);
                cols = Integer.parseInt(firstLine[1]);
                nPieces = Integer.parseInt(firstLine[2]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid file format: N, M, and nPieces must be numbers.");
                continue;
            }
            Board board = new Board(rows, cols, "DEFAULT", null);

            String configType = nonEmpty.get(1).trim().toUpperCase();
            int idx = 2;
            if (configType.equals("CUSTOM")) {
                char[][] grid = new char[rows][cols];
                int validGrids = 0;
                int temp = idx;
                while (idx < temp + rows) {
                    String trimmed = nonEmpty.get(idx).trim();
                    char dot = '.';
                    char tag = 'X';
                    int countDot = (int) trimmed.chars().filter(ch -> ch == dot).count();
                    int countWhiteSpaces = (int) trimmed.chars().filter(ch -> ch == tag).count();
                    if (countDot + countWhiteSpaces != (long) (cols)) {
                        System.err.println("Grid format is incorrect");
                        continue inputLoop;
                    }
                    trimmed = trimmed.replace('.', ' ').replace('X', '.');
                    grid[idx - 2] = trimmed.toCharArray();
                    validGrids += countWhiteSpaces;
                    idx++;
                }
                board.setGrid(grid);
                board.setConfig(configType);
                board.setValidGrids(validGrids);
            }
            else if (configType.equals("PYRAMID")) {
                System.out.println("PYRAMID configuration is not implemented.");
                continue;
            }
            else if (!configType.equals("DEFAULT")) {
                System.out.println("Only PYRAMID, DEFAULT, or CUSTOM configurations are accepted.");
                continue;
            }
            
            List<PuzzlePiece> pieces = new ArrayList<>();
            while (idx < nonEmpty.size() && pieces.size() < nPieces) {
                String line = nonEmpty.get(idx);
                String trimmed = line.trim();
                char letter = trimmed.charAt(0);
                if (!Character.isUpperCase(letter)) {
                    System.err.println("Invalid puzzle input: not an uppercase letter.");
                    continue inputLoop;
                }
                List<String> pieceLines = new ArrayList<>();
                while (idx < nonEmpty.size() && nonEmpty.get(idx).trim().charAt(0) == letter) {
                    pieceLines.add(nonEmpty.get(idx));
                    PuzzlePiece.addCount((int) nonEmpty.get(idx).chars().filter(ch -> ch == letter).count());
                    idx++;
                }
                int puzzlePieceRows = pieceLines.size();
                int puzzlePieceCols = 0;
                for (String str : pieceLines) {
                    if (str.length() > puzzlePieceCols) {
                        puzzlePieceCols = str.length();
                    }
                }
                char[][] shape = shapifyList(pieceLines, puzzlePieceRows, puzzlePieceCols);
                if (shape == null) { 
                    System.out.println("Invalid shape for puzzle piece '" + letter + "'. Please check the input file.");
                    continue inputLoop;
                }
                pieces.add(new PuzzlePiece(letter, shape));
                board.setPieces(pieces);
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
            
            if (pieces.size() != nPieces) {
                System.out.println("The number of puzzle pieces does not match nPieces.");
                continue;
            }

            return board;
        }
    }
    
    private static char[][] shapifyList(List<String> shape, int rows, int cols) {
        List<String> processedLines = new ArrayList<>();
        for (String line : shape) {
            String processed = line.replace(' ', '.');
            processedLines.add(processed);
        }
        char[][] result = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j < processedLines.get(i).length()) {
                    result[i][j] = processedLines.get(i).charAt(j);
                } else {
                    result[i][j] = '.';
                }
            }
        }
        int minRow = rows, maxRow = -1, minCol = cols, maxCol = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (result[i][j] != '.') {
                    if (i < minRow) minRow = i;
                    if (i > maxRow) maxRow = i;
                    if (j < minCol) minCol = j;
                    if (j > maxCol) maxCol = j;
                }
            }
        }
        int newRows = maxRow - minRow + 1;
        int newCols = maxCol - minCol + 1;
        char[][] shrunk = new char[newRows][newCols];
        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                shrunk[i][j] = result[minRow + i][minCol + j];
            }
        }
        Character ref = null;
        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                if (shrunk[i][j] != '.') {
                    if (ref == null) {
                        ref = shrunk[i][j];
                    } else if (shrunk[i][j] != ref) {
                        System.err.println("Error: Karakter tidak konsisten pada puzzle piece. Ditemukan '" 
                                + shrunk[i][j] + "', diharapkan '" + ref + "'.");
                        return null;
                    }
                }
            }
        }
        return shrunk;
    }

    private static Path readFileName(String s) {
        System.out.print(s);
        String fileName = sc.nextLine();
        Path path = Paths.get("..", "test", fileName);
        return path;
    }

    public static void saveSolutionFile(Solver solver, long endTime) {
        Path filePath = readFileName("Enter the solution file name (e.g., solution.txt): ");
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath.toFile()))) {
            for (char[] string : solver.getBoard().getGrid()) {
                out.println(new String(string));
            }
            out.println("Number of cases examined: " + solver.getIterationCount() + " cases");
            out.println("Execution time (ms): " + endTime + " ms");
            System.out.println("Solution successfully saved to " + filePath);
        } catch (IOException e) {
            System.err.printf("Error reading file: %s%n", e.getMessage());
        }
    }

    public static void promptSaveSolution(Solver solver, long endTime) {
        System.out.print("Do you want to save the solution? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("yes")) {
            saveSolutionFile(solver, endTime);
        }
    }

    public static void endConfirmation() {
        System.out.print("Do you want to close the program? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("yes")) {
            System.exit(0);
        }
    }
}
