import chess.ChessGame;
import chess.ChessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Test {
    // Returns true if the game is valid 
    public static boolean validateGame(String gameMoves) {
        ChessGame game = new ChessGame();

        String[] moves = gameMoves.split(" ");

        for(String move : moves) {
            if(move.equals("0-1") || move.equals("1-0") || move.trim().equals(".")) {
                continue;
            }

            try {
                if(move.indexOf(".") != -1) {
                    move = move.substring(move.indexOf(".")+1);
                }

                game.makeMove(move);  
            } catch (ChessException e) {
                return false;
            } catch(Exception e) {
                System.err.println(e);
            }
            
        }

        return true;
    }

    public static List<String> getGames(String filename) {
        File file = new File(filename);
        Scanner reader;
        List<String> list = new LinkedList<>();
        try {
            reader = new Scanner(file);
            boolean start = false;
            StringBuilder game = new StringBuilder();


            while(reader.hasNextLine()) {
                String line = reader.nextLine();

                if(line.indexOf("1.") != -1) {
                    start = true;
                }

                if(line.trim().equals("")) {
                    start = false;
                    list.add(game.toString());
                    game = new StringBuilder();
                }

                if(start) {
                    game.append(line.replace("+", ""));
                    game.append(" ");
                }
            }

            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    public static List<Integer> checkApplication(String fileName) {
        List<String> games = getGames(fileName);
        List<Integer> list = new LinkedList<>();
        int index = 1;

        for(String game : games) {
            if(!validateGame(game)) {
                list.add(index);
            }
            index++;
        }

        return list;
    }

    public static void main(String[] args) {
        System.out.println(checkApplication("Korchnoi.txt"));
    }
}
