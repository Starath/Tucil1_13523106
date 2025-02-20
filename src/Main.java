public class Main {

    public static void main(String[] args) {
        while (true) {
            Board board = IOPuzzlerFile.readInputFile();
            Solver solver = new Solver(board);
            long startTime = System.currentTimeMillis();
            boolean result = solver.solvePuzzle(0);
            long endTime = System.currentTimeMillis() - startTime;
            if (result) {
                solver.getBoard().printColored();
            } else {
                System.out.println("Tidak ada solusi.");
            }
            System.out.println("jumlah iterasi: " + solver.getIterationCount());
            System.out.println("Waktu eksekusi: " + endTime + " ms");
            IOPuzzlerFile.promptSaveSolution(solver, endTime);
            IOPuzzlerFile.endConfirmation();
        }
    }
}

