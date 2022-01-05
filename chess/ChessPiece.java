package chess;

// Base class for every chess piece
class ChessPiece {
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

    private final String pieceName;
    private final Color pieceColor;
    private final char pieceSymbol;

    ChessPiece(Color color, String name, char symbol) {
        pieceColor = color;
        pieceName = name;
        pieceSymbol = symbol;
    }

    String getName() { return pieceName; }
    Color getColor() { return pieceColor; }
    char getSymbol() { return pieceSymbol; }
    
    @Override
    public String toString() {
        if(pieceColor == Color.WHITE) {
            return "W" + pieceSymbol;
        } else {
            return "B" + pieceSymbol;
        }
    }
}
