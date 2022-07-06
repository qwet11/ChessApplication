package chess;

// The Rook class implements a rooks
class Rook extends ChessPiece {
    Rook(Color color) {
        super(color, "Rook", 'R');
    }

    Rook(Rook piece) {
        super(piece);
    }

    @Override
    public Object clone() {
        return new Rook(this);
    }

    @Override
    boolean isLegal(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
        checkStartAndEnd(board, sRow, sCol, eRow, eCol);
        
        // Check if there is a piece in the way
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
        } else {
            return false;
        }
    }
}
