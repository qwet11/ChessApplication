package chess;

// The Bishop class implements a bishop
class Bishop extends ChessPiece {
    Bishop(Color color) {
        super(color, "Bishop", 'B');
    }

    Bishop(Bishop piece) {
        super(piece);
    }

    @Override
    public Object clone() {
        return new Bishop(this);
    }

    @Override
    boolean isLegal(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
        checkStartAndEnd(board, sRow, sCol, eRow, eCol);

        /*
        When a piece is on the same backward diagonal (\-diagonal), col-row difference is constant
        When a piece is on the same forward diagonal (/-diagonal), col+row sum is constant
        */
        if(sRow + sCol == eRow + eCol) {
            // Forward diagonal
            int start = Math.min(sCol, eCol);
            int end = Math.max(sCol, eCol);
            int sum = sRow + sCol;

            for(int i = start+1; i < end; i++) {
                if(board[sum-i][i] != null) {
                    return false;
                }
            }
            return true;
        } else if(sCol - sRow == eCol - eRow) {
            // Backward diagonal
            int start = Math.min(sRow, eRow);
            int end = Math.max(sRow, eRow);
            int dif = sCol - sRow;

            for(int i = start+1; i < end; i++) {
                if(board[i][i+dif] != null) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
