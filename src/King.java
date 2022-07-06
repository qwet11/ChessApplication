package chess;

// The King class implements a king
class King extends ChessPiece {
    King(Color color) {
        super(color, "King", 'K');
        moved = false;
    }

    King(King piece) {
        super(piece);
    }

    @Override
    public King clone() {
        return new King(this);
    }

    // Static check for kingside castling
    static boolean pseudoKingsideCastling(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) {
        ChessPiece piece = board[sRow][sCol];

        if (piece == null || piece.getSymbol() != 'K') {
            return false;
        }

        // Check if piece has moved
        if (piece.moved) {
            return false;
        }

        switch (piece.getColor()) {
            case WHITE:
                // When King is White
                // Check if starting position and ending position is correct
                if (sRow != 7 || sCol != 4 || eRow != 7 || eCol != 6) {
                    return false;
                }

                // Check if there is a Rook on h1 and that it hasn't moved
                if (board[7][7] == null || board[7][7].getSymbol() != 'R' || board[7][7].moved) {
                    return false;
                }

                // Checks if there is a piece in the way
                if (board[7][5] != null || board[7][6] != null) {
                    return false;
                }
                break;
            case BLACK:
                // When King is Black
                // Check if starting position and ending position is correct
                if (sRow != 0 || sCol != 4 || eRow != 0 || eCol != 6) {
                    return false;
                }

                // Check if there is a Rook on h7 and that it hasn't moved
                if (board[0][7] == null || board[0][7].getSymbol() != 'R' || board[0][7].moved) {
                    return false;
                }

                // Checks if there is a piece in the way
                if (board[0][5] != null || board[0][6] != null) {
                    return false;
                }
                break;
        }

        return true;
    }

    // Return true if kingside castling is legal. False otherwise (does not check
    // for checks)
    boolean legalKingsideCastling(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) {
        return pseudoKingsideCastling(board, sRow, sCol, eRow, eCol);
    }

    // Return true if kingside castling is legal. False otherwise (does not check
    // for checks)
    static boolean pseudoQueensideCastling(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) {
        ChessPiece piece = board[sRow][sCol];

        if (piece == null || piece.getSymbol() != 'K') {
            return false;
        }

        // Check if piece has moved
        if (piece.moved) {
            return false;
        }

        switch (piece.getColor()) {
            case WHITE:
                // When King is White
                // Check if starting position and ending position is correct
                if (sRow != 7 || sCol != 4 || eRow != 7 || eCol != 2) {
                    return false;
                }

                // Check if there is a Rook on a1 and that it hasn't moved
                if (board[7][0] == null || board[7][0].getSymbol() != 'R' || board[7][0].moved) {
                    return false;
                }

                // Checks if there is a piece in the way
                if (board[7][3] != null || board[7][2] != null) {
                    return false;
                }
                break;
            case BLACK:
                // When King is Black
                // Check if starting position and ending position is correct
                if (sRow != 0 || sCol != 4 || eRow != 0 || eCol != 2) {
                    return false;
                }

                // Check if there is a Rook on a8 and that it hasn't moved
                if (board[0][0] == null || board[0][0].getSymbol() != 'R' || board[0][0].moved) {
                    return false;
                }

                // Checks if there is a piece in the way
                if (board[0][3] != null || board[0][2] != null) {
                    return false;
                }
                break;
        }

        return true;
    }

    boolean legalQueensideCastling(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) {
        return pseudoQueensideCastling(board, sRow, sCol, eRow, eCol);
    }

    @Override
    boolean isLegal(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
        checkStartAndEnd(board, sRow, sCol, eRow, eCol);

        // Able to move to any square 1 away
        return (Math.abs(eRow - sRow) <= 1 && Math.abs(eCol - sCol) <= 1) ||
                legalKingsideCastling(board, sRow, sCol, eRow, eCol) ||
                legalQueensideCastling(board, sRow, sCol, eRow, eCol);
    }
}
