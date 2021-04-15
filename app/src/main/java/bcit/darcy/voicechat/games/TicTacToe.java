package bcit.darcy.voicechat.games;


import java.util.ArrayList;
import java.util.Arrays;

import bcit.darcy.voicechat.ByteCodes;
import bcit.darcy.voicechat.GameClient;
import bcit.darcy.voicechat.Packet;
import bcit.darcy.voicechat.Packet.Request;
import bcit.darcy.voicechat.Packet.Response;

public class TicTacToe extends Game {
    private enum GameResult { WIN, LOSS, TIE, UNKNOWN };
    private final String[] actions = new String[] {
            "Cell 1x1", "Cell 1x2", "Cell 1x3",
            "Cell 2x1", "Cell 2x2", "Cell 2x3",
            "Cell 3x1", "Cell 3x2", "Cell 3x3"
    };

    private static final char[] SYMBOLS = {'X', 'O'};
    private final char[] board = {'-', '-', '-', '-', '-', '-', '-', '-', '-'};
    private int playerId;
    private boolean myTurn;

    public TicTacToe(GameClient client, int uuid) {
        super(client, uuid);
    }

    @Override
    public String[] getActions() {
        ArrayList<String> availableActions = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == '-') {
                availableActions.add(actions[i]);
            }
        }
        String[] out = new String[availableActions.size()];
        out = availableActions.toArray(out);
        return out;
    }

    @Override
    public boolean hasEnded() {
        return hasEnded;
    }

    @Override
    public void handleAction(String action) {
        if (!hasStarted) {
            client.printMessage("TTT: Game not started yet");
            return;
        }

        if (hasEnded) {
            client.printMessage("TTT: Game already ended");
            return;
        }

        if (!myTurn) {
            client.printMessage("TTT: Not your turn");
            return;
        }

        byte actionCode = (byte) Arrays.asList(actions).indexOf(action);
        myTurn = false;
        board[actionCode] = SYMBOLS[playerId];

        byte[] payload = {actionCode};
        Request r = Packet.getRequest(
                uuid,
                ByteCodes.GAME_ACTION,
                ByteCodes.MAKE_MOVE,
                payload);

        client.sendRequest(r);
        client.printMessage("Client: Sending " + Arrays.toString(Packet.getBytes(r)));
        client.printMessage("TTT: You played " + action);
        printBoard();
    }

    @Override
    public void applyUpdate(Response update) {
        if (update.context == ByteCodes.START_GAME) {
            startVoiceChat();
            hasStarted = true;
            playerId = parsePlayerId(update) - 1;
            myTurn = playerId == 0;
            client.printMessage("TTT: Game started");
            client.printMessage("TTT: You are assigned " + SYMBOLS[playerId]);
        }

        if (update.context == ByteCodes.MOVE_MADE) {
            int cell = parseCell(update, false);
            int opponentId = (playerId + 1) % 2;
            board[cell] = SYMBOLS[opponentId];
            client.printMessage("TTT: Opponent moves on " + actions[cell]);
            printBoard();
            myTurn = true;
        }

        if (update.context == ByteCodes.GAME_END) {
            int cell = parseCell(update, true);
            int opponentId = (playerId + 1) % 2;

            if (board[cell] == '-') {
                board[cell] = SYMBOLS[opponentId];
                client.printMessage("TTT: Opponent moves on " + actions[cell]);
                printBoard();
            }

            GameResult result = parseGameResult(update);
            client.printMessage("TTT: Game outcome is " + result.toString());
        }

        if (update.context == ByteCodes.DISCONNECTED) {
            client.printMessage("TTT: Opponent disconnected");
        }
    }

    private void printBoard() {
        client.printMessage("TTT: Current board");
        client.printMessage("     " + board[0] + " " + board[1] + " " + board[2]);
        client.printMessage("     " + board[3] + " " + board[4] + " " + board[5]);
        client.printMessage("     " + board[6] + " " + board[7] + " " + board[8]);
    }

    private int parsePlayerId(Response update) {
        if (update.payloadSize != 1) {
            return -1;
        }

        return update.payload[0];
    }

    private GameResult parseGameResult(Response update) {
        if (update.payloadSize != 2)
            return GameResult.UNKNOWN;

        byte gameResult = update.payload[0];

        if (gameResult == 1) return GameResult.WIN;
        if (gameResult == 2) return GameResult.LOSS;
        if (gameResult == 3) return GameResult.TIE;

        return GameResult.UNKNOWN;
    }

    private int parseCell(Response update, boolean isGameEnd) {
        int expectedPayloadSize = isGameEnd ? 2 : 1;
        int cellIndex = isGameEnd ? 1 : 0;

        if (update.payloadSize != expectedPayloadSize) {
            return -1;
        }

        byte cell = update.payload[cellIndex];

        if (cell < 0 || cell > 8) {
            return -1;
        }

        return cell;
    }
}
