package bcit.darcy.voicechat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import bcit.darcy.voicechat.Packet.Request;

public class TCPSocket {
    Socket socket = new Socket();
    DataInputStream inputStream;
    DataOutputStream outputStream;
    GameClient gameClient;

    private static final int MIN_SIZE = 3;
    private static final int PAYLOAD_SIZE_INDEX = 2;
    private final String HOSTNAME;

    public TCPSocket(String hostname, GameClient gameClient) {
        this.HOSTNAME = hostname;
        this.gameClient = gameClient;
    }

    public void connect() {
        new Thread(() -> {
            try {
                socket = new Socket();
                InetSocketAddress a = new InetSocketAddress(HOSTNAME, 3000);
                socket.connect(a, 1000);
                gameClient.setConnected(socket.isConnected());

                if (socket.isConnected()) {
                    inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    outputStream = new DataOutputStream(socket.getOutputStream());
                    listen();
                }
            } catch (Exception e) {
                System.out.println("TCPSocket connect() catch");
                gameClient.setConnected(false);
            }
        }).start();
    }

    public void listen() {
        new Thread(() -> {
            while (true) {
                try {
                    byte[] buffer = new byte[16];
                    int totalRead = 0;
                    int numRead = 0;

                    while (totalRead != MIN_SIZE && numRead != -1) {
                        numRead = inputStream.read(buffer, totalRead, 1);
                        totalRead += numRead;
                        System.out.println("numRead=" + numRead + " totalRead=" + totalRead);
                    }

                    if (numRead == -1) {
                        System.out.println(Arrays.toString(buffer));
                        gameClient.setConnected(false);
                        return;
                    }

                    int payloadSize = buffer[PAYLOAD_SIZE_INDEX];

                    while (totalRead != (MIN_SIZE + payloadSize) && numRead != -1) {
                        numRead = inputStream.read(buffer, totalRead, 1);
                        totalRead += numRead;
                        System.out.println("numRead=" + numRead + " totalRead=" + totalRead);
                    }

                    if (numRead == -1) {
                        System.out.println(Arrays.toString(buffer));
                        gameClient.setConnected(false);
                        return;
                    }

                    if (totalRead > 0) {
                        byte[] message = Arrays.copyOfRange(buffer, 0, totalRead);
                        gameClient.handleServerMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("TCPSocket listen() catch");
                    gameClient.setConnected(false);
                    return;
                }
            }
        }).start();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Request r) {
        byte[] bytes = Packet.getBytes(r);
        System.out.println("TCPSocket send(): " + Arrays.toString(bytes));

        new Thread(() -> {
            if (socket.isClosed()) return;
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
