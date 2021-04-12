package bcit.darcy.voicechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MICROPHONE = 1;
    Spinner spActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Server to Client: <010> <001> <004> <000-000-000-156>
//        Server to Client: <020> <001> <001> <001>
        byte[] message1 = {10, 1, 4, 0, 0, 0, (byte) 156};
        System.out.println(Arrays.toString(message1));
        Message m1 = new Message(message1);
        System.out.println(Arrays.toString(m1.getRawMessage()));

        ScrollView svTerminal = findViewById(R.id.svTerminal);
        TextView tvTerminal = findViewById(R.id.tvTerminal);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);

//        String board = "\n        X | _ | _ \n        _ | _ | _ \n        _ | O | _";

//        String[] arraySpinner = new String[] {
//                "Cell 1x1", "Cell 1x2", "Cell 1x3", "Cell 2x1", "Cell 2x2", "Cell 2x3", "Cell 3x1", "Cell 3x2", "Cell 3x3"
//        };

        spActions = findViewById(R.id.spActions);

        GameClient gameClient = new GameClient(tvTerminal);
        String[] actions = gameClient.getCurrentActions();
        setSpinnerActions(actions);

        Thread thread = new Thread(() -> {

            try {
                Socket socket = new Socket("70.71.235.164", 2034);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                OutputStream out = socket.getOutputStream();
                while (socket.isConnected()) {
                    String msg = in.readLine();
                    System.out.println(msg);
                }
            } catch (Exception e) {
                System.out.println("Exception here :-(");
                e.printStackTrace();
            }
        });
        thread.start();

        btnSendMessage.setOnClickListener(v -> {
            String selectedAction = spActions.getSelectedItem().toString();
            gameClient.handleAction(selectedAction);
            svTerminal.post(() -> svTerminal.fullScroll(ScrollView.FOCUS_DOWN));
            String[] newActions = gameClient.getCurrentActions();
            setSpinnerActions(newActions);
//            tvTerminal.append("\nServer: " + "Board update");
//            tvTerminal.append(board);
        });



//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.RECORD_AUDIO},
//                REQUEST_MICROPHONE);
//        VoiceChat vc = new VoiceChat(37);
//        Button startSpeakButton = findViewById(R.id.btn_start_speak);
//        startSpeakButton.setOnClickListener(v -> {
//            vc.startSpeak();
//        });
//
//        Button stopSpeakButton = findViewById(R.id.btn_stop_speak);
//        stopSpeakButton.setOnClickListener(v -> {
//            vc.stopSpeak();
//        });
//
//        Button startListenButton = findViewById(R.id.btn_start_listen);
//        startListenButton.setOnClickListener(v -> {
//            vc.startListen();
//        });
//
//        Button stopListenButton = findViewById(R.id.btn_stop_listen);
//        stopListenButton.setOnClickListener(v -> {
//            vc.stopListen();
//        });

    }

    public void setSpinnerActions(String[] actions) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, actions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActions.setAdapter(adapter);
    }
}