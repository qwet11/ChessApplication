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

			try {
				game.makeMove(move);
				
				if(game.isCheckmate()) {
					System.out.printf("\n%s was checkmated!", game.getTurnColor());
					isRunning = false;
				}
			} catch(ChessException e) {
				System.err.println(e.getMessage());
			}

		} while(isRunning);
		game.exportGame("testGame");

		

		input.close();
	}
}
