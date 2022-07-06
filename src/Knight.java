package chess;

// The Knight class implements a knight
class Knight extends ChessPiece {
    Knight(Color color) {
        super(color, "Knight", 'N');
    }

    Knight(Knight piece) {
        super(piece);
    }

    @Override
    public Object clone() {
        return new Knight(this);
    }

    @Override
    boolean isLegal(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
        checkStartAndEnd(board, sRow, sCol, eRow, eCol);
        
        return (Math.abs(eRow - sRow) == 1 && Math.abs(eCol - sCol) == 2)
                        || (Math.abs(eRow - sRow) == 2 && Math.abs(eCol - sCol) == 1);
    }
}
