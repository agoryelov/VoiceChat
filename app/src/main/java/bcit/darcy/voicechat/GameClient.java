package bcit.darcy.voicechat;

import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import bcit.darcy.voicechat.games.Game;
import bcit.darcy.voicechat.Packet.Request;
import bcit.darcy.voicechat.Packet.Response;
import bcit.darcy.voicechat.games.RockPaperScissors;
import bcit.darcy.voicechat.games.TicTacToe;

public class GameClient {
    private Game game = null;
    private boolean isConnected = false;
    ArrayAdapter<String> actionsAdapter;

    private final TextView gameTerminal;
    private final TCPSocket tcpSocket;
    private final int randomId = (int) Math.round(Math.random() * 100);

    public GameClient(TextView gameTerminal, ArrayAdapter<String> adapter) {
        this.actionsAdapter = adapter;
        setActions(new String[]{"Connect"});
        this.gameTerminal = gameTerminal;
        this.tcpSocket = new TCPSocket(this);
    }

    public void setConnected(boolean connected) {
        if (!connected) game = null;

        if (!isConnected && connected) {
            printMessage("Client: Connected to server");
        }

        if (!isConnected && !connected) {
            printMessage("Client: Unable to connect");
        }

        if (isConnected && !connected) {
            printMessage("Client: Disconnected from server");
        }

        isConnected = connected;
        updateCurrentActions();
    }

    public void connect() {
        printMessage("Client: Connecting to server...");
        tcpSocket.connect();
    }

    public void disconnect() {
        if (!isConnected) {
            printMessage("Client: Not connected to server");
            return;
        }
        tcpSocket.disconnect();
    }

    public void sendRequest(Request r) {
        if (!isConnected) {
            printMessage("Client: Not connected to server");
            return;
        }

        tcpSocket.send(r);
    }

    private void requestGame(String game) {
        printMessage("Client: Requesting to play " + game + "...");

        byte[] payload = game.equals("TTT") ? ByteCodes.RPS.clone() : ByteCodes.RPS.clone();
        Request request = Packet.getRequest(0, ByteCodes.CONFIRM, ByteCodes.CONFIRM_RULES, payload);
        tcpSocket.send(request);
    }

    public void handleServerMessage(byte[] message) {
        System.out.println(randomId + ": handleServerMessage()");
        System.out.println(Arrays.toString(message));
        if (message.length == 0) return;
        Response r = Packet.getResponse(message);

        if (r.status == ByteCodes.CLIENT_ERROR) {
            printMessage("Server: Client error");
        }

        if (r.status == ByteCodes.SERVER_ERROR) {
            printMessage("Server: Server error");
        }

        if (r.status == ByteCodes.GAME_ERROR) {
            printMessage("Server: Game error");
        }

        if (r.status == ByteCodes.UPDATE) {
            printMessage("Server: Game update");
            game.applyUpdate(r);
        }

        if (r.status == ByteCodes.SUCCESS && game == null) {
            int uuid = ByteBuffer.wrap(r.payload).getInt();
            game = createGame(uuid);
            printMessage("Server: Joined game with uuid=" + uuid);
        }

        if (r.status == ByteCodes.SUCCESS) {
            printMessage("Server: Request was successful");
        }

        if (game != null && game.hasEnded()) {
            game = null;
        }

        updateCurrentActions();
    }

    public void printMessage(String message) {
        final Handler ui = new Handler(Looper.getMainLooper());
        ui.post(() -> gameTerminal.append("\n" + message));
    }

    private void clearActions() {
        final Handler ui = new Handler(Looper.getMainLooper());
        ui.post(() -> actionsAdapter.clear());
    }

    private void setActions(String[] actions) {
        final Handler ui = new Handler(Looper.getMainLooper());
        ui.post(() -> actionsAdapter.addAll(Arrays.asList(actions)));
    }

    private Game createGame(int uuid) {
        return new RockPaperScissors(this, uuid);

//        if (Arrays.equals(request.payload, ByteCodes.TTT)) {
//            return new TicTacToe();
//        }
//
//        if (Arrays.equals(request.payload, ByteCodes.RPS)) {
//            return new RockPaperScissors();
//        }
//
//        return null;
    }

    public void updateCurrentActions() {
        clearActions();

        if (!isConnected) {
            String[] actions = new String[]{"Connect"};
            setActions(actions);
            return;
        }

        if (game == null) {
            String[] actions = new String[]{"Play TicTacToe", "Play RPS"};
            setActions(actions);
            return;
        }

        String[] actions = game.getActions();
        setActions(actions);
    }

    public void handleAction(String action) {
        System.out.println(action);
        switch (action) {
            case "Connect":
                connect();
                break;
            case "Play TicTacToe":
                requestGame("TTT");
                break;
            case "Play RPS":
                requestGame("RPS");
                break;
            default:
                if (game != null) game.handleAction(action);
                else printMessage("Client: Unable to do " + action);
        }
    }
}
