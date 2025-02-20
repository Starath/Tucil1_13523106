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
            System.out.print("\033[H\033[2J");
            System.out.flush();
            Path filePath = readFileName("Masukkan nama file test case (misal: test.txt): ");
            while (!Files.exists(filePath)) {
                filePath = readFileName("Masukkan nama file test case (misal: test.txt): ");
            }
        
            List<String> allLines;
            try {
                allLines = Files.readAllLines(filePath);
            } catch (IOException e) {
                System.err.printf("Error membaca file: %s%n", e.getMessage());
                continue;
            }
        
            List<String> nonEmpty = new ArrayList<>();
            for (String line : allLines) {
                if (!line.trim().isEmpty()) nonEmpty.add(line);
            }
            if (nonEmpty.size() < 2) {
                System.err.println("File tidak sesuai format.");
                continue;
            }
            
            String[] firstLine = nonEmpty.get(0).trim().split("\\s+");
            if (firstLine.length < 3) {
                System.err.println("Format baris pertama salah.");
                continue;
            }
        
            int rows, cols, nPieces;
            try {
                rows = Integer.parseInt(firstLine[0]);
                cols = Integer.parseInt(firstLine[1]);
                nPieces = Integer.parseInt(firstLine[2]);
            } catch (NumberFormatException e) {
                System.err.println("File format tidak valid: N, M, dan nPieces harus berupa angka.");
                continue;
            }
        
            String configType = nonEmpty.get(1).trim().toUpperCase();
            if (configType.equals("CUSTOM")) {
                System.out.println("Konfigurasi CUSTOM tidak diimplementasikan.");
                continue;
            }
            if (configType.equals("PYRAMID")) {
                System.out.println("Konfigurasi PYRAMID tidak diimplementasikan.");
                continue;
            }
            if (!configType.equals("DEFAULT")) {
                System.out.println("Hanya menerima konfigurasi PYRAMID, DEFAULT, atau CUSTOM");
                continue;
            }
        
            List<PuzzlePiece> pieces = new ArrayList<>();
            int idx = 2;
            while (idx < nonEmpty.size() && pieces.size() < nPieces) {
                String line = nonEmpty.get(idx);
                String trimmed = line.trim();
                char letter = trimmed.charAt(0);
                List<String> pieceLines = new ArrayList<>();
                while (idx < nonEmpty.size() && nonEmpty.get(idx).trim().charAt(0) == letter) {
                    pieceLines.add(nonEmpty.get(idx));
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
                    System.out.println("Shape untuk puzzle piece '" + letter + "' tidak valid. Silahkan periksa file input.");
                    continue inputLoop;
                }
                pieces.add(new PuzzlePiece(letter, shape));
            }
            
            if (pieces.size() != nPieces) {
                System.out.println("Jumlah puzzle pieces tidak sesuai dengan nPieces.");
                continue;
            }
            return new Board(rows, cols, pieces);
        }
    }
    
    private static Path readFileName(String s) {
        System.out.print(s);
        String fileName = sc.nextLine();
        Path path = Paths.get("..", "test", fileName);
        return path;
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

    public static void saveSolutionFile(Solver solver, long endTime) {
        Path filePath = readFileName("Masukkan nama file solusi (misal: solusi.txt): ");
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath.toFile()))) {
            for (char[] string : solver.getBoard().getGrid()) {
                out.println(new String(string));
            }
            out.println("Banyak kasus yang ditinjau: " + solver.getIterationCount());
            out.println("Waktu eksekusi (ms): " + endTime);
            System.out.println("Solusi berhasil disimpan di " + filePath);
        } catch (IOException e) {
            System.err.printf("Error membaca file: %s%n", e.getMessage());
        }
    }

    public static void promptSaveSolution(Solver solver, long endTime) {
        System.out.println("Apakah anda ingin menyimpan solusi? (ya/tidak): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("ya")) {
            saveSolutionFile(solver, endTime);
        }
    }

    public static void endConfirmation() {
        System.out.println("Apakah anda ingin menutup program? (ya/tidak): ");
        String ans = sc.nextLine().trim().toLowerCase();
        if (ans.equals("ya")) {
            System.exit(0);
        }
    }
}
