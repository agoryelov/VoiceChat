package bcit.darcy.voicechat;


import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

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

class TCPSocket {
    Socket socket = null;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    GameClient gameClient;

    public TCPSocket(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public void connect() {
        Thread thread = new Thread(() -> {
            try {
                socket = new Socket("70.71.235.164", 2034);
                inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                outputStream = new DataOutputStream(socket.getOutputStream());
                gameClient.setConnected(true);
                gameClient.updateCurrentActions();
                while (socket.isConnected()) {
                    byte[] buffer = new byte[1024];
                    int numRead = inputStream.read(buffer, 0, 1024);
                    byte[] message = Arrays.copyOfRange(buffer, 0, numRead);
                    gameClient.handleServerMessage(message);
                }
            } catch (Exception e) {
                System.out.println("Exception here :-(");
                e.printStackTrace();
            } finally {
                disconnect();
            }
        });
        thread.start();
    }

    public void disconnect() {
        gameClient.setConnected(false);
        gameClient.updateCurrentActions();
        if (socket == null) return;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] message) {
        Thread thread = new Thread(() -> {
            try {
                outputStream.write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}

public class GameClient {
    private boolean inGame = false;
    private boolean isConnected = false;
    private int uuid = 0;
    ArrayList<String> currentActions;

    private final TextView gameTerminal;
    private final TCPSocket tcpSocket;

    public GameClient(TextView gameTerminal) {
        this.currentActions = new ArrayList<>();
        currentActions.add("Connect");
        this.gameTerminal = gameTerminal;
        this.tcpSocket = new TCPSocket(this);
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    private void connect() {
        tcpSocket.connect();
        gameTerminal.append("\nClient: Connecting to server...");
        gameTerminal.append("\nClient: Connected to server");
    }

    private void requestRPS() {
        byte[] message = new byte[9];

        byte one = 1;
        byte two = 2;
        ByteBuffer buffer = ByteBuffer.wrap(message);
        buffer.putInt(uuid).put(one).put(one).put(two).put(one).put(two);

        tcpSocket.send(buffer.array());
    }

    private void requestGame(String game) {
        switch (game) {
            case "RPS":
                requestRPS();
                gameTerminal.append("\nClient: Requesting to play RPS...");
                gameTerminal.append("\nClient: Joined game");
                inGame = true;
                updateCurrentActions();
                break;
            case "TTT":
                requestRPS();
                gameTerminal.append("\nClient: Requesting to play TTT...");
                gameTerminal.append("\nClient: Joined game");
                inGame = true;
                updateCurrentActions();
                break;
            default:
                //
        }
    }

    public void handleServerMessage(byte[] message) {
        System.out.println("handleServerMessage()");
        System.out.println(Arrays.toString(message));
        Message m = new Message(message);
        byte uuidSize = 4;
        if (m.payloadSize == uuidSize) {
            uuid = ByteBuffer.wrap(m.payload).getInt();
        }
        System.out.println(uuid);
    }

    public void updateCurrentActions() {
        currentActions.clear();

        if (!isConnected) {
            String[] actions = new String[]{"Connect"};
            currentActions.addAll(Arrays.asList(actions));
            return;
        }

        if (!inGame) {
            String[] actions = new String[]{"Play TicTacToe", "Play RPS"};
            currentActions.addAll(Arrays.asList(actions));
            return;
        }

        String[] actions = new String[]{"Rock", "Paper", "Scissors"};;
        currentActions.addAll(Arrays.asList(actions));
    }

    public void handleAction(String action) {
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
        }
        System.out.println(action);
    }
}
