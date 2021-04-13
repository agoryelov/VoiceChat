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

    public TCPSocket(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public void connect() {
        new Thread(() -> {
            try {
                socket = new Socket();
                InetSocketAddress a = new InetSocketAddress("70.71.235.164", 2034);
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
            int numRead = 0;
            while (true) {
                try {
                    byte[] buffer = new byte[16];
                    System.out.println("buffer before read: " + Arrays.toString(buffer));
                    numRead = inputStream.read(buffer, 0, 3);
                    if (numRead != 3) {
                        System.out.println("TCPSocket listen() bad request");
                        gameClient.setConnected(false);
                        return;
                    }
                    int payloadSize = buffer[2];
                    numRead = inputStream.read(buffer, 3, payloadSize);
                    byte[] message = Arrays.copyOfRange(buffer, 0, 3 + numRead);
                    gameClient.handleServerMessage(message);
                    System.out.println("buffer after read: " + Arrays.toString(buffer));
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
        System.out.println("TCPSocket send()");
        System.out.println(Arrays.toString(bytes));

        new Thread(() -> {
            System.out.println("TCPSocket send()");
            if (socket.isClosed()) return;
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
