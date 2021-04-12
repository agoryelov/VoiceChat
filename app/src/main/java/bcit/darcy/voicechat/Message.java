package bcit.darcy.voicechat;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Message {
    private byte[] rawMessage;

    public Message(byte[] rawData) {
        this.rawMessage = rawData;
        parseMessage(rawData);

    }

    public byte[] getRawMessage() {
        return rawMessage;
    }

    private void parseMessage(byte[] rawData) {
        byte[] bytes = rawData;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byte status = byteBuffer.get();
        byte context = byteBuffer.get();
        byte payloadSize = byteBuffer.get();
        byte[] payload = new byte[payloadSize];
        byteBuffer.get(payload);

        System.out.println("status=" + status);
        System.out.println("context=" + context);
        System.out.println("payloadSize=" + payloadSize);
        System.out.println("payload=" + Arrays.toString(payload));
    }
}
