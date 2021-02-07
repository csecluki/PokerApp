package com.example.pokerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class GameRoomActivity extends AppCompatActivity {

    private EditText editTextMessage;

    private Server server;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button btnSend = findViewById(R.id.btnSend);
        editTextMessage = findViewById(R.id.editTextMessage);

        loadData();

        btnSend.setOnClickListener(view -> {
            try {
                client.sendMessage(editTextMessage.getText().toString());
            } catch (NullPointerException e){
                server.broadcastMessage(editTextMessage.getText().toString());
            }
            editTextMessage.setText("");
        });
    }

    private void loadData() {
        if (ClientHolder.getClient() != null) {
            client = ClientHolder.getClient();
        } else {
            server = ServerHolder.getServer();
        }
    }
}