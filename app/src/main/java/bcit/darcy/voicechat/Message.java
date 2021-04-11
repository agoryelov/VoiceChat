package bcit.darcy.voicechat;

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

    }
}
