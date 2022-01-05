import chess.ChessGame;
import chess.ChessException;
import java.util.Scanner;

/**
 * Main class for chess application
 */
public class Main {

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        Scanner input = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("Welcome to chess");
        do {
            System.out.printf("\n%1$s TURN! \n%2$s%n", game.getTurnColor(), game.printBoard());
            System.out.print("Enter Move: ");
            String move = input.nextLine();

            // All chess notation formats
            String format1 = "^[a-h][1-8]$"; // e.g e5, h7
            String format2 = "^[RNBQK][a-h][1-8]$"; // e.g Nc3, Qh5
            String format3 = "^[RNBQKa-h]x[a-h][1-8]$"; // Bxc4, Kxd8
            String format4 = "^[RNBQK][a-h][a-h][1-8]$"; // Nad5, Rfd1
            String format5 = "^[RNBQK][a-h]x[a-h][1-8]$"; // Raxc7, Qcxh2
            String regex = String.format("%1$s|%2$s|%3$s|%4$s|%5$s", format1, format2, format3, format4, format5);

            if(move.matches(regex)) {
                try {
                    game.makeMove(move);
                } catch(ChessException e) {
                    System.err.println(e.getMessage());
                }
            } else {
                System.err.println("Please enter a valid move");
            }


        } while(isRunning);
    }
}
