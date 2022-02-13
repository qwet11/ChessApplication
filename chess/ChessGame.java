package chess;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ChessGame {    
    private final ChessBoard chessBoard;
    private ChessPiece.Color currTurnColor; // Color for this turn
    private List<String> chessMoves; // List of moves

    /* 
    NOTE: 
    Having these variables static contradicts the logic of the game, however it is the only solution I could come up with
    Only one ChessGame could be initiated at once for these values to work
    */

    // Store position of White King
    int whiteKingRow;
    int whiteKingCol;

    // Store position of Black King
    int blackKingRow;
    int blackKingCol;

	// Needed to detect en passant
    static int lastSRow;
    static int lastERow;
    static int lastECol;

    /**
     * Initialize a new chess board
     */
    public ChessGame() {
        chessBoard = new ChessBoard();
        currTurnColor = ChessPiece.Color.WHITE;
        chessMoves = new LinkedList<>();

        // Initiates the king positions
        whiteKingRow = 7;
        whiteKingCol = 4;
        blackKingRow = 0;
        blackKingCol = 4;

        // Sets last coordinates to invalid number
        lastSRow = -1;
		lastERow = -1;
		lastECol = -1;
    }
    
    /**
     * Makes a move, updates the board, and changes turn to next player.
     * @param move the chess move you want to make (e.g. e4, Na4)
     */
    public void makeMove(String move) throws ChessException {
        // All chess notation formats
        String format1 = "^[a-h][1-8]$"; // e.g e5, h7
        String format2 = "^[RNBQK][a-h][1-8]$"; // e.g Nc3, Qh5
        String format3 = "^[RNBQKa-h]x[a-h][1-8]$"; // Bxc4, Kxd8
        String format4 = "^[RNBQK][a-h][a-h][1-8]$"; // Nad5, Rfd1
        String format5 = "^[RNBQK][a-h]x[a-h][1-8]$"; // Raxc7, Qcxh2
        String format6 = "^O-O$"; // O-O
        String format7 = "^O-O-O$"; // O-O-O
        String regex = String.format("%1$s|%2$s|%3$s|%4$s|%5$s|%6$s|%7$s", format1, format2, format3, format4, format5, format6, format7);

        if(!move.matches(regex)) {
            throw new ChessException("Invalid Move format. Please enter a valid move");
        }

        ChessPiece[][] board = chessBoard.getBoard();

        // If move is valid, no error is thrown
        // posCoords confirms that the move is valid
        int[] posCoords = getAllCoord(move);
        int sRow = posCoords[0], sCol = posCoords[1];
        int eRow = posCoords[2], eCol = posCoords[3];

        if(Pawn.enPassant(board, lastSRow, lastERow, lastECol, sRow, sCol, eRow, eCol)) {
            // Special case for en passant
            board[lastERow][lastECol] = null;
        } else if(King.pseudoKingsideCastling(board, sRow, sCol, eRow, eCol)) {
            // Special case for kingside castling
            int tempRow = (board[sRow][sCol].getColor() == ChessPiece.Color.WHITE) ? 7 : 0;

            board[tempRow][5] = board[tempRow][7];
            board[tempRow][7] = null;
        } else if(King.pseudoQueensideCastling(board, sRow, sCol, eRow, eCol)) {
            // Special case for kingside castling
            int tempRow = (board[sRow][sCol].getColor() == ChessPiece.Color.WHITE) ? 7 : 0;

            board[tempRow][3] = board[tempRow][0];
            board[tempRow][0] = null;
        }

        // Move piece to endPos
        board[eRow][eCol] = board[sRow][sCol];
        board[sRow][sCol] = null;

        // Check if King moved
        if(sRow == whiteKingRow && sCol == whiteKingCol) {
            whiteKingRow = eRow;
            whiteKingCol = eCol;
        } else if(sRow == blackKingRow && sCol == blackKingCol) {
            blackKingRow = eRow;
            blackKingCol = eCol;
        }

        lastSRow = sRow;
        lastERow = eRow;
        lastECol = eCol;

        // Switches color to the color of the next turn
        currTurnColor = currTurnColor.next();

        // Adds move to list
        chessMoves.add(move);

        // Update moved feature of piece
        if(board[eRow][eCol] != null) {
            board[eRow][eCol].moved = true;
        }
    }

    /**
     * Checks if the given color is in check
     * @param board the board analyzed for checks
     * @param color the color who is checked for checks
     * @param kingRow the row of the King (of the color)
     * @param kingCol the column of the King (of the color)
     * @return true if given color is in check; false otherwise
     */
    public boolean isCheck(ChessPiece[][] board, ChessPiece.Color color, int kingRow, int kingCol) {
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board[0].length; col++) {
                // If opponent piece can capture the king, then it is check
                try {
                    if(board[row][col] != null && board[row][col].getColor() != color && pseudoValidate(board, board[row][col].getColor(), row, col, kingRow, kingCol)) {
                        return true;
                    }
                } catch (ChessException e) {
                    continue;
                }
                
            }
        }
        return false;
    }
    
    /**
     * Checks if the color is checkmated on the board 
     * @param board the board checked for checkmate
     * @param color the color checked for checkmate
     * @return true if the color is checkmated on the board, false otherwise
     */
    public boolean isCheckmate(ChessPiece[][] board, ChessPiece.Color color) {
        List<int[]> list = getLegalMoves(board, color);
        return list.size() == 0;
    }

    /**
     * Checks if the current color is checkmated
     * @return true if the current color is checkmated, false otherwise
     */
    public boolean isCheckmate() {
        List<int[]> list = getLegalMoves(chessBoard.getBoard(), currTurnColor);
        return list.size() == 0;
    }

    /**
     * Returns a formatted string of the chess board
     * @return the current chess board
     */
    public String printBoard() {
        return chessBoard.printBoard();
    }

    public void exportGame(String fileName) {
        try {
            FileWriter writer = new FileWriter(String.format("%s.pgn", fileName), true);
            boolean isWhite = true;
            int moveIndex = 1;

            for(String move : chessMoves) {
                if(isWhite) {
                    writer.write(String.format("%s. %s ", moveIndex, move));
                } else {
                    writer.write(String.format("%s ", move));
                    moveIndex++;
                }

                isWhite = !isWhite;
            }

            writer.close();
        } catch (IOException e) {
            throw new ChessException("An error occurred!");
        }
    }
    
    /**
     * Returns the color of the player whose turn it is.
     * @return the currTurnColor object that contains the color of the current player
     */
    public ChessPiece.Color getTurnColor() {
        return currTurnColor;
    }

    // Returns a list of all possible moves given the color in the format [sRow, sCol, eRow, eCol]
    /**
     * Returns a list of all possible moves given the color in the format [sRow, sCol, eRow, eCol]
     * @param color the color for which the legal moves is for
     * @return a list of all possible moves in the format [sRow, sCol, eRow, eCol]
     */
    public List<int[]> getLegalMoves(ChessPiece.Color color) {
        List<int[]> list = new LinkedList<>();
        ChessPiece[][] board = chessBoard.getBoard();

        for(int sRow = 0; sRow < board.length; sRow++) {
            for(int sCol = 0; sCol < board[0].length; sCol++) {
                if(board[sRow][sCol] != null && board[sRow][sCol].getColor() == color) {
                    // Check if there is a valid move with this piece
                    for(int eRow = 0; eRow < board.length; eRow++) {
                        for(int eCol = 0; eCol < board[0].length; eCol++) {
                            try {
                                if(isValidMove(board, sRow, sCol, eRow, eCol)) {
                                    int[] temp = new int[] {sRow, sCol, eRow, eCol};
                                    list.add(temp);
                                }
                            } catch (ChessException e) {
                                continue;
                            }
                            
                        }
                    }
                }
            }
        }

        return list;
    }
    
    /**
     * Returns a list of all possible moves
     * @param board the board used to obtain the legal moves 
     * @param color the color for which the legal moves is for
     * @return a list of all possible moves in the format [sRow, sCol, eRow, eCol]
     */
    public List<int[]> getLegalMoves(ChessPiece[][] board, ChessPiece.Color color) {
        List<int[]> list = new LinkedList<>();

        for(int sRow = 0; sRow < board.length; sRow++) {
            for(int sCol = 0; sCol < board[0].length; sCol++) {
                if(board[sRow][sCol] != null && board[sRow][sCol].getColor() == color) {
                    // Check if there is a valid move with this piece
                    for(int eRow = 0; eRow < board.length; eRow++) {
                        for(int eCol = 0; eCol < board[0].length; eCol++) {
                            try {
                                if(isValidMove(board, sRow, sCol, eRow, eCol)) {
                                    int[] temp = new int[] {sRow, sCol, eRow, eCol};
                                    list.add(temp);
                                }
                            } catch (ChessException e) {
                                continue;
                            }
                        }
                    }
                }
            }
        }

        return list;
    }
    
    // Returns true if a move is valid. Returns false otherwise
    private boolean isValidMove(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) throws ChessException {
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
        if(piece.getColor() != currTurnColor) {
            throw new ChessException("Invalid Move: Wrong Piece Color");
        }

        // Check if there is a piece of the same color on the end square
        if(board[eRow][eCol] != null && board[eRow][eCol].getColor() == currTurnColor) {
            throw new ChessException("Invalid Move: Captured piece is same color");
        }

        // If in check, see if move would stop check 
        int kingRow = (currTurnColor == ChessPiece.Color.WHITE) ? whiteKingRow : blackKingRow;
        int kingCol = (currTurnColor == ChessPiece.Color.WHITE) ? whiteKingCol : blackKingCol;

        if(isCheck(board, currTurnColor, kingRow, kingCol)) {
            // Make a copy of the board 
            ChessPiece[][] tempBoard = getCopyBoard(board, sRow, sCol, eRow, eCol);

            // Check if King moved
            if(sRow == whiteKingRow && sCol == whiteKingCol) {
                kingRow = eRow;
                kingCol = eCol;
            } else if(sRow == blackKingRow && sCol == blackKingCol) {
                kingRow = eRow;
                kingCol = eCol;
            }

            // Check if the tempBoard is in check 
            if(isCheck(tempBoard, currTurnColor, kingRow, kingCol)) {
                throw new ChessException("Invalid Move: King is in check");
            }

        }

        // Check if move is legal
        return board[sRow][sCol].isLegal(board, sRow, sCol, eRow, eCol);
    }

    // Returns true if a move is valid. Returns false otherwise
    // Does not check if it is check
    private boolean pseudoValidate(ChessPiece[][] board, ChessPiece.Color color, int sRow, int sCol, int eRow, int eCol) throws ChessException {
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
        if(piece.getColor() != color) {
            throw new ChessException("Invalid Move: Wrong Piece Color");
        }

        // Check if there is a piece of the same color on the end square
        if(board[eRow][eCol] != null && board[eRow][eCol].getColor() == color) {
            throw new ChessException("Invalid Move: Captured piece is same color");
        }

        // Check if move is legal
        return board[sRow][sCol].isLegal(board, sRow, sCol, eRow, eCol);
    }

    
    // Returns an array containing the row and col index given the position in chess notation (e.g. e4)
    private int[] getCoord(String pos) {
        // (int) 'a' -> 97
        // (int) '1' -> 49
        // NOTE: Row 1 on a chessboard is row 7 in a 0-indexed matrix
        int col = pos.charAt(0) - 97;
        int row = chessBoard.getBoard().length-1 - (pos.charAt(1) - 49);

        return new int[] {row, col};
    }

    // Returns an array containing the sRow, sCol, eRow and eCol given the chess move (e.g e4 or Nc3)
    // NOTE: This is a very brute force method
    // TODO: Make this method more efficient in the future
    private int[] getAllCoord(String move) throws ChessException {
        List<int[]> list = new LinkedList<>();
        ChessPiece[][] board = chessBoard.getBoard();
        int eRow = -1, eCol = -1; // default

        switch(move) {
            case "O-O":
                // Kingside castling
                if(currTurnColor == ChessPiece.Color.WHITE && isValidMove(board, 7, 4, 7, 5) && isValidMove(board, 7, 4, 7, 6)) {
                    list.add(new int[]{7, 4});
                    eRow = 7;
                    eCol = 6;
                } else if(currTurnColor == ChessPiece.Color.BLACK && isValidMove(board, 0, 4, 0, 5) && isValidMove(board, 0, 4, 0, 6)) {
                    list.add(new int[]{0, 4});
                    eRow = 0;
                    eCol = 6;
                }
                break;
            case "O-O-O":
                // Queenside castling
                if(currTurnColor == ChessPiece.Color.WHITE && isValidMove(board, 7, 4, 7, 3) && isValidMove(board, 7, 4, 7, 2)) {
                    list.add(new int[]{7, 4});
                    eRow = 7;
                    eCol = 2;
                } else if(currTurnColor == ChessPiece.Color.BLACK && isValidMove(board, 0, 4, 0, 3) && isValidMove(board, 0, 4, 0, 2)) {
                    list.add(new int[]{0, 4});
                    eRow = 0;
                    eCol = 2;
                }
                break;
            default:
                String ePos = move.substring(move.length() - 2);
                eRow = getCoord(ePos)[0];
                eCol = getCoord(ePos)[1];

                char PIECE_SYMBOL = (move.matches("^[a-h][1-8]$") || move.matches("^[a-h]x[a-h][1-8]$")) ? 'p' : move.charAt(0);

                for(int row = 0; row < board.length; row++) {
                    for(int col = 0; col < board[0].length; col++) {
                        try {
                            if (board[row][col] != null && board[row][col].getSymbol() == PIECE_SYMBOL && isValidMove(board, row, col, eRow, eCol)) {
                                list.add(new int[]{row, col});
                            }
                        } catch(ChessException e) {
                            continue;
                        }
                    }
                }
                break;
        }

        if(list.size() == 0) {
            throw new ChessException("Invalid Move");
        } else if(list.size() == 1) {
            int[] temp = list.get(0);
            return new int[] {temp[0], temp[1], eRow, eCol};
        } else {
            if(move.matches("^[RNBQK][a-h][a-h][1-8]$|^[RNBQK][a-h]x[a-h][1-8]$")) {
                int sCol = move.charAt(1) - 97;
                int sRow = -1;
                for(int[] temp : list) {
                    if(temp[1] == sCol) {
                        sRow = temp[0];
                        break;
                    }
                }

                if(sRow == -1) {
                    throw new ChessException("Invalid Move");
                } else {
                    return new int[] {sRow, sCol, eRow, eCol};
                }
            } else {
                throw new ChessException("Invalid Move: Multiple options");
            }
        }
    }

    private ChessPiece[][] getCopyBoard(ChessPiece[][] board, int sRow, int sCol, int eRow, int eCol) {
        // Make a copy of the board 
        ChessPiece[][] tempBoard = new ChessPiece[board.length][board[0].length];
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board[0].length; col++) {
                if(board[row][col] == null) {
                    tempBoard[row][col] = null;
                } else {
                    tempBoard[row][col] = (ChessPiece) board[row][col].clone();
                }
            }
        }

        // Make the move (whether legal or not) 
        tempBoard[eRow][eCol] = tempBoard[sRow][sCol];
        tempBoard[sRow][sCol] = null;

        // Special case for en passant
        if(Pawn.enPassant(tempBoard, lastSRow, lastERow, lastECol, sRow, sCol, eRow, eCol)) {
            tempBoard[lastERow][lastECol] = null;
        }

        return tempBoard;
    }
}
