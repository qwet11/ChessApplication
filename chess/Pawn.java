package chess;

// The Pawn class implements a pawn
class Pawn extends ChessPiece {
    Pawn(Color color) {
        super(color, "Pawn", 'p');
    }

    Pawn(Pawn piece) {
        super(piece);
    }

    @Override
    public Object clone() {
        return new Pawn(this);
    }

    @Override
    boolean isLegal(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
        checkStartAndEnd(board, sRow, sCol, eRow, eCol);
        
        if(PIECE_COLOR == ChessPiece.Color.WHITE) {
            // White
        
            if(sRow - eRow == 1 && sCol == eCol && board[eRow][eCol] == null) {
                // Move one square in front (if empty)
                return true;
            } else if(sRow == 6 && sRow - eRow == 2 && sCol == eCol && board[eRow][eCol] == null) {
                // Move 2 squares in front
                return true;
            } else if(Math.abs(eCol - sCol) == 1 && sRow - 1 == eRow && board[eRow][eCol] != null) {
                //Capture piece 
                return true;
            } else {
                // En passant
                return enPassant(board, ChessGame.lastSRow, ChessGame.lastERow, ChessGame.lastECol, sRow, sCol, eRow, eCol);
            }
        } else {
            // Black
            if(eRow - sRow == 1 && sCol == eCol && board[eRow][eCol] == null) {
                // Move one square in front (if empty)
                return true;
            } else if(sRow == 1 && eRow - sRow == 2 && sCol == eCol && board[eRow][eCol] == null) {
                // Move 2 squares in front
                return true;
            } else if(Math.abs(eCol - sCol) == 1 && eRow - 1 == sRow && board[eRow][eCol] != null) {
                //Capture piece
                return true;
            } else {
                // En passant
                return enPassant(board, ChessGame.lastSRow, ChessGame.lastERow, ChessGame.lastECol, sRow, sCol, eRow, eCol);
            }
        }
    }

    // Returns true if move is enPassant, false otherwise
    static boolean enPassant(ChessPiece[][] board, int lastSRow, int lastERow, int lastECol, int sRow, int sCol, int eRow, int eCol) {
        // Record differences depending on color
        int diff = 0;
        if(sRow == 3 && Math.abs(eCol - sCol) == 1 && lastSRow != -1) {
            // Record for White
            diff = lastERow - lastSRow;
        } else if(sRow == 4 && Math.abs(eCol - sCol) == 1 && lastSRow != -1) {
            // Record for Black
            diff = lastSRow - lastERow;
        } else {
            return false;
        }
        
        // Checks if (1) there is a pawn next to it, (2) the pawn moved 2 squares
        if(board[lastERow][lastECol] != null && board[lastERow][lastECol].getSymbol() == 'p' && lastECol == eCol && diff == 2) {
            return true;
        } else {
            return false;
        }
    }
}
