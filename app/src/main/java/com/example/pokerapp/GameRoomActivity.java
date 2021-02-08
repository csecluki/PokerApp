package com.example.pokerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

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

    public void createGame(ArrayList<String> playerList) {
        Game game = new Game();
        for (String name: playerList) {
            game.addPlayer(new Player(name));
        }
    }

    private void loadData() {
        if (ClientHolder.getClient() != null) {
            client = ClientHolder.getClient();
            client.setGameRoomActivity(GameRoomActivity.this);
        } else {
            server = ServerHolder.getServer();
            server.setGameRoomActivity(GameRoomActivity.this);
            server.shufflePlayers();
            server.sendOrderedPlayers();
        }
    }
}