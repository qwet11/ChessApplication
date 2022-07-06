package chess;

import java.util.LinkedList;
import java.util.List;

public class MoveValidator {
    /**
     * Returns a list of all possible moves given the color in the format [sRow,
     * sCol, eRow, eCol]
     * 
     * @param color the color for which the legal moves is for
     * @return a list of all possible moves in the format [sRow, sCol, eRow, eCol]
     */
    static List<int[]> getLegalMoves(ChessPiece[][] board, ChessPiece.Color color) {
        List<int[]> list = new LinkedList<>();

        for (int sRow = 0; sRow < board.length; sRow++) {
            for (int sCol = 0; sCol < board[0].length; sCol++) {
                if (board[sRow][sCol] != null && board[sRow][sCol].getColor() == color) {
                    // Check if there is a valid move with this piece
                    for (int eRow = 0; eRow < board.length; eRow++) {
                        for (int eCol = 0; eCol < board[0].length; eCol++) {
                            try {
                                if (isValidMove(board, color, sRow, sCol, eRow, eCol)) {
                                    int[] temp = new int[] { sRow, sCol, eRow, eCol };
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
    static boolean isValidMove(ChessPiece[][] board, ChessPiece.Color currTurnColor, int sRow, int sCol, int eRow,
            int eCol) throws ChessException {
        // Check if move (end square) is out of bounds
        if (eRow < 0 || eRow >= board.length || eCol < 0 || eCol >= board.length) {
            throw new ChessException("Invalid Move: Out of bounds");
        }

        // Check if start square is equal to end square
        if (sRow == eRow && sCol == eCol) {
            throw new ChessException("Invalid Move: Same square");
        }

        // Check if there is a piece on starting square
        ChessPiece piece = board[sRow][sCol];
        if (piece == null) {
            throw new ChessException("Invalid Move: No piece");
        }

        // Check if starting piece is of the right color
        if (piece.getColor() != currTurnColor) {
            throw new ChessException("Invalid Move: Wrong Piece Color");
        }

        // Check if there is a piece of the same color on the end square
        if (board[eRow][eCol] != null && board[eRow][eCol].getColor() == currTurnColor) {
            throw new ChessException("Invalid Move: Captured piece is same color");
        }

        // If in check, see if move would stop check
        int kingRow = (currTurnColor == ChessPiece.Color.WHITE) ? ChessGame.whiteKingRow : ChessGame.blackKingRow;
        int kingCol = (currTurnColor == ChessPiece.Color.WHITE) ? ChessGame.whiteKingCol : ChessGame.blackKingCol;

        if (ChessGame.isCheck(board, currTurnColor, kingRow, kingCol)) {
            // Make a copy of the board
            ChessPiece[][] tempBoard = ChessGame.getCopyBoard(board, sRow, sCol, eRow, eCol);

            // Check if King moved
            if (sRow == ChessGame.whiteKingRow && sCol == ChessGame.whiteKingCol) {
                kingRow = eRow;
                kingCol = eCol;
            } else if (sRow == ChessGame.blackKingRow && sCol == ChessGame.blackKingCol) {
                kingRow = eRow;
                kingCol = eCol;
            }

            // Check if the tempBoard is in check
            if (ChessGame.isCheck(tempBoard, currTurnColor, kingRow, kingCol)) {
                throw new ChessException("Invalid Move: King is in check");
            }

        }

        // Check if move is legal
        return board[sRow][sCol].isLegal(board, sRow, sCol, eRow, eCol);
    }

    // Returns true if a move is valid. Returns false otherwise
    // Does not check if it is check
    static boolean pseudoValidate(ChessPiece[][] board, ChessPiece.Color color, int sRow, int sCol, int eRow, int eCol)
            throws ChessException {
        // Check if move (end square) is out of bounds
        if (eRow < 0 || eRow >= board.length || eCol < 0 || eCol >= board.length) {
            throw new ChessException("Invalid Move: Out of bounds");
        }

        // Check if start square is equal to end square
        if (sRow == eRow && sCol == eCol) {
            throw new ChessException("Invalid Move: Same square");
        }

        // Check if there is a piece on starting square
        ChessPiece piece = board[sRow][sCol];
        if (piece == null) {
            throw new ChessException("Invalid Move: No piece");
        }

        // Check if starting piece is of the right color
        if (piece.getColor() != color) {
            throw new ChessException("Invalid Move: Wrong Piece Color");
        }

        // Check if there is a piece of the same color on the end square
        if (board[eRow][eCol] != null && board[eRow][eCol].getColor() == color) {
            throw new ChessException("Invalid Move: Captured piece is same color");
        }

        // Check if move is legal
        return board[sRow][sCol].isLegal(board, sRow, sCol, eRow, eCol);
    }
}
