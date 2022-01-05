package chess;


import java.util.LinkedList;
import java.util.List;

public class ChessGame {
    private final ChessBoard chessBoard;
    private ChessPiece.Color currTurnColor; // Color for this turn

	// Need to detect en passant
    private int lastSRow;
    private int lastERow;
    private int lastECol;
    private boolean enPassant;

    /**
     * Initialize a new chess board
     */
    public ChessGame() {
        chessBoard = new ChessBoard();
        currTurnColor = ChessPiece.Color.WHITE;

        // Sets last coordinates to invalid number
        lastSRow = -1;
		lastERow = -1;
		lastECol = -1;
    }

    /**
     * Makes a move and updates the board.
     * @param move the chess move you want to make (e.g. e4, Na4)
     */
    public void makeMove(String move) throws ChessException {
        ChessPiece[][] board = chessBoard.getBoard();

        int[] posCoords = getAllCoord(move);
        int sRow = posCoords[0], sCol = posCoords[1];
        int eRow = posCoords[2], eCol = posCoords[3];

        if(isValidMove(board, sRow, sCol, eRow, eCol)) {
            // If move is valid, move piece to endPos
            board[eRow][eCol] = board[sRow][sCol];
            board[sRow][sCol] = null;

            if(enPassant) {
                board[lastERow][lastECol] = null;
            }

            lastSRow = sRow;
			lastERow = eRow;
			lastECol = eCol;
            enPassant = false;

            // Switches color to the color of the next turn
            currTurnColor = currTurnColor.next();
        } else {
            throw new ChessException("Invalid move");
        }
    }

    /**
     * Returns a formatted string of the chess board
     * @return the current chess board
     */
    public String printBoard() {
        return chessBoard.printBoard();
    }

    /**
     * Returns the color of the player whose turn it is.
     * @return the currTurnColor object that contains the color of the current player
     */
    public ChessPiece.Color getTurnColor() {
        return currTurnColor;
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
            return false;
        }

        switch(piece.getSymbol()) {
            case('K'):
                // King
                // Able to move to any square 1 away
                return Math.abs(eRow - sRow) <= 1 && Math.abs(eCol - sCol) <= 1;
            case 'N':
                // Knight
                return (Math.abs(eRow - sRow) == 1 && Math.abs(eCol - sCol) == 2)
                        || (Math.abs(eRow - sRow) == 2 && Math.abs(eCol - sCol) == 1);
            case 'p':
                // Pawn
                if(currTurnColor == ChessPiece.Color.WHITE) {
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
                    } else if(sRow == 3 && Math.abs(eCol - sCol) == 1 && lastSRow != -1) {
                        // En passant

                        // Checks if (1) there is a pawn next to it, (2) the pawn moved 2 squares
                        if(board[lastERow][lastECol].getSymbol() == 'p' && lastECol == eCol && lastERow - lastSRow == 2) {
                            enPassant = true;
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
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
                    } else if(sRow == 4 && Math.abs(eCol - sCol) == 1 && lastSRow != -1) {
                        // En passant
                        // Checks if (1) there is a pawn next to it, (2) the pawn moved 2 squares
                        if(board[lastERow][lastECol].getSymbol() == 'p' && lastECol == eCol && lastSRow - lastERow == 2) {
                            enPassant = true;
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            case 'R':
                // Rook

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
            case 'B':
                // Bishop 
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
            case 'Q':
                //Rook and Bishop
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
        return false;
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
    private int[] getAllCoord(String move) {

        String ePos = move.substring(move.length() - 2);
        int eRow = getCoord(ePos)[0];
        int eCol = getCoord(ePos)[1];

        char pieceSymbol = (move.matches("^[a-h][1-8]$") || move.matches("^[a-h]x[a-h][1-8]$")) ? 'p' : move.charAt(0);
        List<int[]> list = new LinkedList<>();
        ChessPiece[][] board = chessBoard.getBoard();

        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board.length; col++) {
                try {
                    if (board[row][col] != null && board[row][col].getSymbol() == pieceSymbol && isValidMove(board, row, col, eRow, eCol)) {
                        list.add(new int[]{row, col});
                    }
                } catch(ChessException e) {
					continue;
                }
            }
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
}
