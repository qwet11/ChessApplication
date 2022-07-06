package chess; 

// A custom exception made for the package
public class ChessException extends RuntimeException {
    ChessException(String s) {
        super(s);
    }
}