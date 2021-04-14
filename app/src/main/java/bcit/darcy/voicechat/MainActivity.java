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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MICROPHONE = 1;
    Spinner spActions;
    ArrayAdapter<String> actionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScrollView svTerminal = findViewById(R.id.svTerminal);
        TextView tvTerminal = findViewById(R.id.tvTerminal);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_MICROPHONE);

        spActions = findViewById(R.id.spActions);

        actionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        GameClient gameClient = new GameClient(tvTerminal, actionsAdapter, svTerminal);
        actionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActions.setAdapter(actionsAdapter);

        btnSendMessage.setOnClickListener(v -> {
            String selectedAction = spActions.getSelectedItem().toString();
            gameClient.handleAction(selectedAction);
        });

        Button btnDisconnect = findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(view -> {
            gameClient.disconnect();
        });

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(view -> {
            tvTerminal.setText("");
        });
    }
}