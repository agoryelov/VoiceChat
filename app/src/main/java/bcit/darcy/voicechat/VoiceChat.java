package bcit.darcy.voicechat;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceChat {
    private static final int SAMPLE_RATE = 10000; // hz
    private static final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int ORDER_SIZE = 4; // bytes
    private static final int UUID_SIZE = 4; // bytes
    private static final int PORT_SIZE = 2; // bytes
    private static final int VOICE_SIZE = 5000; // bytes
    private static final int PACKET_SIZE = ORDER_SIZE + UUID_SIZE + PORT_SIZE + VOICE_SIZE;

    private boolean isSpeaking = false;
    private boolean isListening = false;
    private final int UUID;

    DatagramSocket voiceSocket = null;

    public VoiceChat(int UUID) {
        System.out.println(PACKET_SIZE);
        this.UUID = UUID;

        try {
            voiceSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void startSpeak() {
        if (isSpeaking) return;
        System.out.println("startSpeak()");
        isSpeaking = true;

        Thread thread = new Thread(() -> {
            ByteBuffer buffer = ByteBuffer.allocateDirect(PACKET_SIZE);
            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE, CHANNELS, ENCODING, VOICE_SIZE);
            recorder.startRecording();
            int currentOrder = 0;

            while (isSpeaking) {
                buffer.clear();
                buffer.putInt(currentOrder++).putInt(UUID).putShort((short) 0);

                byte[] voiceBuffer = new byte[VOICE_SIZE];
                recorder.read(voiceBuffer, 0, VOICE_SIZE);
                buffer = buffer.put(voiceBuffer);

                buffer.rewind();
                byte[] rawBuffer = new byte[buffer.remaining()];
                buffer.get(rawBuffer);

                System.out.println("SENDING:");
                System.out.println(Arrays.toString(rawBuffer));

                try {
                    InetAddress dest = InetAddress.getByName("70.71.235.164");
                    DatagramPacket p = new DatagramPacket(rawBuffer, rawBuffer.length, dest, 2034);
                    voiceSocket.send(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            recorder.stop();
            recorder.release();
        });

        thread.start();
    }

    public void stopSpeak() {
        System.out.println("stopSpeak()");
        isSpeaking = false;
    }

    public void startListen() {
        if (isListening) return;
        System.out.println("startListen()");
        isListening = true;

        Thread thread = new Thread(() -> {
            byte[] buffer = new byte[PACKET_SIZE];

            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, VOICE_SIZE, AudioTrack.MODE_STREAM);
            track.play();
            int currentOrder = 0;

            while (isListening) {
                DatagramPacket p = new DatagramPacket(buffer, PACKET_SIZE);
                try {
                    voiceSocket.receive(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                buffer = p.getData();
                System.out.println("RECEIVED:");
                System.out.println(Arrays.toString(buffer));

                int packetOrder = ByteBuffer.wrap(buffer).getInt();
                System.out.println("currentOrder=" + currentOrder);
                System.out.println("packetOrder=" + packetOrder);
                if (packetOrder > currentOrder) {
                    currentOrder = packetOrder;
                    track.write(buffer,ORDER_SIZE + UUID_SIZE + PORT_SIZE, VOICE_SIZE);
                }
            }

            track.stop();
            track.flush();
            track.release();
        });

        thread.start();
    }

    public void stopListen() {
        System.out.println("stopListen()");
        isListening = false;
    }

}
