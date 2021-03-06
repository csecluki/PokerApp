package com.example.pokerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class GameRoomActivity extends AppCompatActivity {

    private EditText editTextAmount;
    private Button btnRaise;
    private Button btnCheck;
    private Button btnCall;
    private Button btnFold;
    private Button btnAllIn;

    private ArrayList<View> views = new ArrayList<>();
    private View player1;
    private View player2;
    private View player3;
    private View player4;
    private View player5;
    private View player6;

    private Server server;
    private Client client;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editTextAmount = findViewById(R.id.editTextAmount);
        btnRaise = findViewById(R.id.btnRaise);
        btnRaise.setEnabled(false);
        btnCheck = findViewById(R.id.btnCheck);
        btnCheck.setEnabled(false);
        btnCall = findViewById(R.id.btnCall);
        btnCall.setEnabled(false);
        btnFold = findViewById(R.id.btnFold);
        btnFold.setEnabled(false);
        btnAllIn = findViewById(R.id.btnAllIn);
        btnAllIn.setEnabled(false);

        player1 = findViewById(R.id.player1);
        views.add(player1);
        player2 = findViewById(R.id.player2);
        views.add(player2);
        player3 = findViewById(R.id.player3);
        views.add(player3);
        player4 = findViewById(R.id.player4);
        views.add(player4);
        player5 = findViewById(R.id.player5);
        views.add(player5);
        player6 = findViewById(R.id.player6);
        views.add(player6);

        loadData();
    }

    public void createGame(ArrayList<String> orderedPlayerList) {
        Game game = new Game();
        for (int i =0; i < orderedPlayerList.size(); i++){
            Player player = new Player(orderedPlayerList.get(i));
            game.addPlayer(player);
            player.setView(views.get(i));

            TextView textViewName = player.getView().findViewById(R.id.textViewName);
            player.setTextViewName(textViewName);
            player.labelTextViewName(player.getName());

            TextView textViewBalance = player.getView().findViewById(R.id.textViewBalance);
            player.setTextViewBalance(textViewBalance);
            player.labelTextViewBalance(String.valueOf(player.getBalance()));

            TextView textViewBid = player.getView().findViewById(R.id.textViewBid);
            player.setTextViewBid(textViewBid);
            player.labelTextViewBid(String.valueOf(player.getCurrentBid()));
        }
    }

    private void loadData() {
        if (ClientHolder.getClient() != null) {
            client = ClientHolder.getClient();
            client.setGameRoomActivity(GameRoomActivity.this);
            setName(client.getName());
        } else {
            server = ServerHolder.getServer();
            server.setGameRoomActivity(GameRoomActivity.this);
            setName(server.getName());
            ArrayList<String> orderedPlayerList = server.shufflePlayers();
            server.sendOrderedPlayers();
            createGame(orderedPlayerList);
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}