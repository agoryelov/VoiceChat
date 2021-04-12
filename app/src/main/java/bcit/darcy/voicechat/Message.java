package bcit.darcy.voicechat;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Message {
    private final byte[] rawMessage;
    public byte status = 0;
    public byte context = 0;
    public byte payloadSize = 0;
    public byte[] payload;


    public Message(byte[] rawData) {
        this.rawMessage = rawData;
        parseMessage(rawData);
    }

    public byte[] getRawMessage() {
        return rawMessage;
    }

    private void parseMessage(byte[] rawData) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawData);
        status = byteBuffer.get();
        context = byteBuffer.get();
        payloadSize = byteBuffer.get();
        payload = new byte[payloadSize];
        byteBuffer.get(payload);
    }
}
