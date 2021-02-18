package com.example.pokerapp;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Server implements Runnable {

    private static final int PORT = 8888;


    private Sender sender;
    private final Handler handler;
    private final Context context;
    private final MainActivity mainActivity;
    private GameRoomActivity gameRoomActivity;

    private final ArrayList<PrintWriter> clientList = new ArrayList<>();
    private final HashMap<PrintWriter, String> playerOrder = new HashMap<>();

    private int playerNumber = 1;
    private final ArrayList<String> playerNameList = new ArrayList<>();

    public Server(Handler handler, Context context, MainActivity mainActivity) {
        this.handler = handler;
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            sender = new Sender();
            clientList.add(null);
            playerNameList.add("Player_0");

            while (true) {
                new Thread(new SocketHandler(serverSocket.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String msg) {
        if (msg.startsWith("/name")) {
            playerNameList.set(0, msg.substring(6));
            sender.sendNames();
        } else {
            sender.broadcastMessage(msg);
        }
    }

    private class SocketHandler implements Runnable {

        private final Socket socket;
        private PrintWriter out;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientList.add(out);
                playerNameList.add("Player_" + playerNumber);
                playerNumber++;
                sender.sendNames();

                String msg;
                while ((msg = in.readLine()) != null) {
                    if (!msg.isEmpty()) {
                        if (msg.startsWith("/name")) {
                            playerNameList.set(clientList.indexOf(out), msg.substring(6));
                            sender.sendNames();
                        } else {
                            String finalMsg = msg;
                            sender.broadcastMessage(msg);
                            handler.post(() -> Toast.makeText(context, finalMsg, Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clientList.remove(out);
                handler.post(() -> Toast.makeText(context, "Client disconnected. Current number of client: " + clientList.size(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    public class Sender {

        public void broadcastMessage(String msg) {
            for (PrintWriter client: clientList) {
                if (client != null) new Thread(() -> client.println(msg)).start();
            }
        }

        public void sendNames() {
            StringBuilder nameString = new StringBuilder();
            for (String name: playerNameList) {
                nameString.append(" ").append(name);
            }
            handler.post(() -> mainActivity.setPlayerNameList(playerNameList));
            broadcastMessage("/nameList" + nameString);
        }
    }

    public void shufflePlayers() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < playerNameList.size(); i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < list.size(); i++) {
            playerOrder.put(clientList.get(i), playerNameList.get(i));
        }
    }

    public void sendOrderedPlayers() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String name: playerOrder.values()) {
            stringBuilder.append(" ").append(name);
        }
        broadcastMessage("/order" + stringBuilder);
    }

    public void setGameRoomActivity(GameRoomActivity gameRoomActivity) {
        this.gameRoomActivity = gameRoomActivity;
    }
}
