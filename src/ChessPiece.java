package chess;

// Base class for every chess piece
abstract class ChessPiece {
    enum Color {
        WHITE,
        BLACK;

        Color next() {
            if(this == WHITE) {
                return BLACK;
            } else {
                return WHITE;
            }
        }
    }

    protected final String PIECE_NAME;
    protected final Color PIECE_COLOR;
    protected final char PIECE_SYMBOL;

    // Used to keep track of what pieces moved
    protected boolean moved;

    ChessPiece(Color color, String name, char symbol) {
        PIECE_COLOR = color;
        PIECE_NAME = name;
        PIECE_SYMBOL = symbol;
    }

    // Copy constructor (for deep copies)
    ChessPiece(ChessPiece piece) {
        this(piece.getColor(), piece.getName(), piece.getSymbol());
        this.moved = piece.moved;
    }

    String getName() { return PIECE_NAME; }
    Color getColor() { return PIECE_COLOR; }
    char getSymbol() { return PIECE_SYMBOL; }

    // Note: 
    // I debated implementing this method because it felt odd for a piece to see the board
    // In the end, I decided to implement it this way for the sake of simplicity and modularity

    // Returns true if a move is legally possible, false otherwise (does not check if the move is valid i.e invalid due to check)
    abstract boolean isLegal(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol);

    // Return true if rows and columns are legal. Returns custom error otherwise
    // Made for readability
    protected boolean checkStartAndEnd(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
        // Check if move (end square) is out of bounds
        if(eRow < 0 || eRow >= board.length || eCol < 0 || eCol >= board.length) {
            throw new ChessException("Invalid Move: Out of bounds");
        }

        // Check if start square is equal to end square
        if(sRow == eRow && sCol == eCol) {
            throw new ChessException("Invalid Move: Same square");
        }

        // Check if there is a piece on starting square
        ChessPiece piece = board[sRow][sCol];
        if(piece == null) {
            throw new ChessException("Invalid Move: No piece");
        }

        // Check if starting piece is of the right color
        if(piece.getColor() != PIECE_COLOR) {
            throw new ChessException("Invalid Move: Wrong Piece Color");
        }

        // Check if there is a piece of the same color on the end square
        if(board[eRow][eCol] != null && board[eRow][eCol].getColor() == PIECE_COLOR) {
            throw new ChessException("Invalid Move: Captured piece is same color");
        }

        return true;
    }
    
    @Override
    public String toString() {
        if(PIECE_COLOR == Color.WHITE) {
            return "W" + PIECE_SYMBOL;
        } else {
            return "B" + PIECE_SYMBOL;
        }
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
