package com.example.pokerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button btnSend = findViewById(R.id.btnSend);
        editTextMessage = findViewById(R.id.editTextMessage);

        player = PlayerHolder.getPlayer();

        btnSend.setOnClickListener(view -> {
            try {
                player.getClient().sendMessage(editTextMessage.getText().toString());
            } catch (NullPointerException e){
                player.getServer().broadcastMessage(editTextMessage.getText().toString());
            }
            editTextMessage.setText("");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        player = PlayerHolder.getPlayer();
    }
}