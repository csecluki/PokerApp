package com.example.pokerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class GameRoomActivity extends AppCompatActivity {

    private EditText editTextMessage;

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        Button btnSend = findViewById(R.id.btnSend);
        editTextMessage = findViewById(R.id.editTextMessage);

        player = PlayerHolder.getPlayer();

        btnSend.setOnClickListener(view -> {
            try {
                player.getClient().sendMessage(editTextMessage.getText().toString());
            } catch (NullPointerException e){
                player.getServer().broadcastMessage(editTextMessage.getText().toString());
            }
        });
    }
}