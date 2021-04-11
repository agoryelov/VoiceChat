package bcit.darcy.voicechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MICROPHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AsyncTask.execute(() -> {
//            try {
//                String udpStr = "My String";
//                int msg_length = udpStr.length();
//                byte[] message = udpStr.getBytes();
//                InetAddress addr = InetAddress.getByName("70.71.235.164");
//                DatagramPacket p = new DatagramPacket(message, msg_length, addr, 2033);
//                System.out.println("1");
//                DatagramSocket s = new DatagramSocket();
//                s.send(p);
//                System.out.println("2");
//                s.close();
//                System.out.println("3");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_MICROPHONE);
        VoiceChat vc = new VoiceChat(37);
        Button startSpeakButton = findViewById(R.id.btn_start_speak);
        startSpeakButton.setOnClickListener(v -> {
            vc.startSpeak();
        });

        Button stopSpeakButton = findViewById(R.id.btn_stop_speak);
        stopSpeakButton.setOnClickListener(v -> {
            vc.stopSpeak();
        });

        Button startListenButton = findViewById(R.id.btn_start_listen);
        startListenButton.setOnClickListener(v -> {
            vc.startListen();
        });

        Button stopListenButton = findViewById(R.id.btn_stop_listen);
        stopListenButton.setOnClickListener(v -> {
            vc.stopListen();
        });

    }
}