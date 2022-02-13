package chess;

// The Queen class implements a queen
class Queen extends ChessPiece {
    Queen(Color color) {
        super(color, "Queen", 'Q');
    }


    Queen(Queen piece) {
        super(piece);
    }

    @Override
    public Object clone() {
        return new Queen(this);
    }

    @Override
    boolean isLegal(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
        checkStartAndEnd(board, sRow, sCol, eRow, eCol);

        if(sRow == eRow) {
            // Check vertical
            int start = Math.min(sCol, eCol);
            int end = Math.max(sCol, eCol);

            for(int col = start+1; col < end; col++) {
                if(board[sRow][col] != null) {
                    return false;
                }
            }
            return true;
        } else if(sCol == eCol) {
            // Check horizontal
            int start = Math.min(sRow, eRow);
            int end = Math.max(sRow, eRow);

            for(int row = start+1; row < end; row++) {
                if(board[row][sCol] != null) {
                    return false;
                }
            }
            return true;
        } else if(sRow + sCol == eRow + eCol) {
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
