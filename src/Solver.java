public class Solver {
    private Board board;
    private long iterationCount = 0;
    
    public Solver(Board board) {
        this.board = board;
    }

    public boolean solvePuzzle(int puzzlePieceIndex) {
        if (puzzlePieceIndex == board.getPieces().size()) {
            if (board.isFull()) return true;
            return false;
        }
        
        PuzzlePiece puzzlePiece = board.getPieces().get(puzzlePieceIndex);
        for (char[][] shape : puzzlePiece.getOrientations()) {
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getCols(); j++) {
                    iterationCount++;
                    if (board.canPlace(shape, i, j)) {
                        board.placePiece(shape, i, j, puzzlePieceIndex);
                        if (solvePuzzle(puzzlePieceIndex + 1)) return true;
                        board.removePiece(shape, i, j, puzzlePiece.getId());
                    }
                }
            }
        }
        return false; 
    }

    public Board getBoard() {
        return board;
    }

    public long getIterationCount() {
        return iterationCount;
    }

}

