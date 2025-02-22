public class Main {

    public static void main(String[] args) {
        while (true) {
            Board board = IOPuzzlerFile.readInputFile();
            Solver solver = new Solver(board);
            long startTime = System.currentTimeMillis();
            if (solver.getBoard().getValidGrids() != PuzzlePiece.getCount()) {
                System.out.println("No solution");
            }
            boolean result = solver.solvePuzzle(0);
            long endTime = System.currentTimeMillis() - startTime;
            if (result) {
                solver.getBoard().printColored();
            } else {
                System.out.println("No solution.");
            }
            System.out.println("Number of cases examined: " + solver.getIterationCount() + " cases");
            System.out.println("Execution time (ms): " + endTime + " ms");
            IOPuzzlerFile.promptSaveSolution(solver, endTime);
            IOPuzzlerFile.endConfirmation();
        }
    }
}

