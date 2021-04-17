package bcit.darcy.voicechat;

import java.nio.ByteBuffer;

public class Packet {
    public static class Response {
        public byte status = 0;
        public byte context = 0;
        public byte payloadSize = 0;
        public byte[] payload;
    }

    public static class Request {
        public static final int MIN_SIZE = 7;
        public int uuid = 0;
        public byte type = 0;
        public byte context = 0;
        public byte payloadSize = 0;
        public byte[] payload;
    }

    public static Response getResponse(byte[] rawData) {
        Response r = new Response();

        ByteBuffer byteBuffer = ByteBuffer.wrap(rawData);
        r.status = byteBuffer.get();
        r.context = byteBuffer.get();
        r.payloadSize = byteBuffer.get();
        r.payload = new byte[r.payloadSize];
        byteBuffer.get(r.payload);

        return r;
    }

    public static Request getRequest(int uuid, byte type, byte context, byte[] payload) {
        Request r = new Request();

        r.uuid = uuid;
        r.type = type;
        r.context = context;
        r.payloadSize = (byte) payload.length;
        r.payload = payload.clone();

        return r;
    }

    public static byte[] getBytes(Request r) {
        byte[] bytes = new byte[Request.MIN_SIZE + r.payloadSize];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(r.uuid).put(r.type).put(r.context).put(r.payloadSize).put(r.payload);
        return buffer.array();
    }
}
