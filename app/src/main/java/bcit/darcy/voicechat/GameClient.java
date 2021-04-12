package bcit.darcy.voicechat;


import android.widget.TextView;

interface Game {
    String[] getActions();
    void handleAction(String action);
//    String get
}

class RPSGame implements Game {

    @Override
    public String[] getActions() {
        return new String[] {"Rock", "Paper", "Scissors"};
    }

    @Override
    public void handleAction(String action) {
        //
    }
}

public class GameClient {
    private boolean inGame = false;
    private boolean isConnected = false;
    private final TextView gameTerminal;

    public GameClient(TextView gameTerminal) {
        this.gameTerminal = gameTerminal;
    }

    private void connect() {
        gameTerminal.append("\nClient: Connecting to server...");
        gameTerminal.append("\nClient: Connected to server");
        isConnected = true;
    }

    private void requestGame(String game) {
        switch (game) {
            case "RPS":
                gameTerminal.append("\nClient: Requesting to play RPS...");
                gameTerminal.append("\nClient: Joined game");
                inGame = true;
                break;
            case "TTT":
                gameTerminal.append("\nClient: Requesting to play TTT...");
                gameTerminal.append("\nClient: Joined game");
                inGame = true;
                break;
            default:
                //
        }
    }

    public String[] getCurrentActions() {
        if (!isConnected) {
            return new String[]{"Connect"};
        }

        if (!inGame) {
            return new String[]{"Play TicTacToe", "Play RPS"};
        }

        return new String[]{"Rock", "Paper", "Scissors"};
    }

    public void handleAction(String action) {
        switch (action) {
            case "Connect":
                connect();
                break;
            case "Play TicTacToe":
                break;
            case "Play RPS":
                //
                break;
            default:
                //
        }
        System.out.println(action);
    }
}
