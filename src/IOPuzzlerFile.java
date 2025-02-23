import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class IOPuzzlerFile {
    public static final Scanner sc = new Scanner(System.in);
    
    public static Board readInputFile() {
        inputLoop:
        while (true){
            PuzzlePiece.resetCount();
            Path filePath = readFileName("Enter the test case file name (e.g., test.txt): ");
            while (!Files.exists(filePath)) {
                System.out.println("File does not exist.");
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

            if (board.getValidGrids() != PuzzlePiece.getCount()) {
                System.err.println("Unsolvable: the total number of puzzle pieces' required cells does not match the number of valid grids.");
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
                        System.err.println("Error: Inconsistent character on a puzzle piece, found '" 
                                + shrunk[i][j] + "', expected '" + ref + "'.");
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
            if (solver.getBoard().isEmpty()) {
                out.println("No solution");
            } else {
                for (char[] string : solver.getBoard().getGrid()) {
                    out.println(new String(string));
                }
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
    public static void promptSaveImageSolution(Board board) {
        System.out.print("Do you want to save the solution as a PNG image with circular tiles? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if(ans.equals("yes")) {
            saveSolutionImage(board);
        }
    }
    
    public static void saveSolutionImage(Board board) {
        Path filePath = readFileName("Enter the solution image file name (e.g., puzzle_solution.png): ");
        
        int cellSize = 50;
        int rows = board.getRows();
        int cols = board.getCols();
        int imageWidth = cols * cellSize;
        int imageHeight = rows * cellSize;
        
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        
        List<Color> pieceColors = Arrays.asList(
            new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255), new Color(255, 255, 0),
            new Color(255, 165, 0), new Color(75, 0, 130), new Color(238, 130, 238), new Color(0, 255, 255),
            new Color(255, 20, 147), new Color(139, 69, 19), new Color(255, 140, 0), new Color(46, 139, 87),
            new Color(70, 130, 180), new Color(148, 0, 211), new Color(255, 182, 193), new Color(0, 128, 128),
            new Color(220, 20, 60), new Color(0, 191, 255), new Color(154, 205, 50), new Color(255, 99, 71),
            new Color(50, 205, 50), new Color(123, 104, 238), new Color(0, 255, 127), new Color(184, 134, 11),
            new Color(72, 61, 139), new Color(255, 215, 0)
        );
    
        Map<Character, Color> pieceColorMap = new HashMap<>();
        char[][] grid = board.getGrid();
        int colorIndex = 0;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                char ch = grid[i][j];
                if(ch != '.' && !pieceColorMap.containsKey(ch)){
                    pieceColorMap.put(ch, pieceColors.get(colorIndex % pieceColors.size()));
                    colorIndex++;
                }
            }
        }
        
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                int x = j * cellSize;
                int y = i * cellSize;
                char ch = grid[i][j];
                if(ch == '.'){
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(x, y, cellSize, cellSize);
                } else {
                    g2d.setColor(pieceColorMap.get(ch));
                    g2d.fillOval(x, y, cellSize, cellSize);
                    g2d.setColor(Color.BLACK);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(String.valueOf(ch));
                    int textHeight = fm.getAscent();
                    int textX = x + (cellSize - textWidth) / 2;
                    int textY = y + (cellSize + textHeight) / 2;
                    g2d.drawString(String.valueOf(ch), textX, textY);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawOval(x, y, cellSize, cellSize);
            }
        }
        
        g2d.dispose();
        
        try {
            ImageIO.write(image, "png", filePath.toFile());
            System.out.println("Solution image saved to " + filePath.toString());
        } catch (IOException e) {
            System.err.println("Error saving PNG file: " + e.getMessage());
        }
    }

}
