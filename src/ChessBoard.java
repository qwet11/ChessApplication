package chess;

// The ChessBoard class implements a chess board that holds all the chess pieces
class ChessBoard {
    private final ChessPiece[][] board;
    private final int BOARD_SIZE = 8; // DO NOT CHANGE. If it changes, everything breaks

    // Creates and prepares the chess board for a new game
    ChessBoard() {
        board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        resetBoard();
    }

    // Refreshes the board to the starting position
    void resetBoard() {
        for(int i = 0; i < BOARD_SIZE; i++) {
            if(i > 1 && i < 6) {
                // Empty space
                continue;
            }

            ChessPiece.Color color = (i == 0 || i == 1) ? ChessPiece.Color.BLACK : ChessPiece.Color.WHITE;
            for(int j = 0; j < BOARD_SIZE; j++) {
                if(i == 1 || i == 6) {
                    // Pawn
                    board[i][j] = new Pawn(color);
                } else if(j == 0 || j == 7) {
                    // Rook
                    board[i][j] = new Rook(color);
                } else if(j == 1 || j == 6) {
                    // Knight
                    board[i][j] = new Knight(color);
                } else if(j == 2 || j == 5) {
                    // Bishop
                    board[i][j] = new Bishop(color);
                } else if(j == 3) {
                    // Queen
                    board[i][j] = new Queen(color);
                } else {
                    // King
                    board[i][j] = new King(color);
                }
            }
        }
    }

    // Returns a string of the current state of the chess board
    String printBoard() {
        StringBuilder s = new StringBuilder();

        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++) {
                if(board[i][j] == null) {
                    s.append("-  ");
                } else {
                    s.append(board[i][j]).append(" ");
                }
            }

            s.append("\n");
        }
        return s.toString();
    }


    // Returns a ChessBoard object for the current game
    ChessPiece[][] getBoard() {
        return board;
    }
}
