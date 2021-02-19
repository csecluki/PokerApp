package com.example.pokerapp;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client implements Runnable {

    private static final int PORT = 8888;

    private final InetAddress inetAddress;
    private Socket socket;
    private Sender sender;
    private PrintWriter out;
    private final Handler handler;
    private final Context context;
    private final MainActivity mainActivity;
    private GameRoomActivity gameRoomActivity;

    private String name;
    private final ArrayList<String> playerNameList = new ArrayList<>();

    public Client(InetAddress inetAddress, Handler handler, Context context, MainActivity mainActivity) {
        this.inetAddress = inetAddress;
        this.handler = handler;
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(inetAddress, PORT);
            sender = new Sender();

            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(new Receiver()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        if (msg.startsWith("/name ")) {
            setName(msg.substring(6));
        }
        sender.sendMessage(msg);
    }

    private class Receiver implements Runnable {

        @Override
        public void run() {
            String msg;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    msg = in.readLine();
                    if (msg != null && !(msg.isEmpty())) {
                        String finalMsg = msg;
                        if (msg.equals("/goToRoom")) {
                            mainActivity.launchGameRoomActivity();
                        } else if (msg.startsWith("/nameList")) {
                            msg = msg.substring(10);
                            playerNameList.clear();
                            playerNameList.addAll(Arrays.asList(msg.split(" ")));
                            if (name == null) setName(playerNameList.get(playerNameList.size() - 1));
                            handler.post(() -> mainActivity.setPlayerNameList(playerNameList));
                        } else if (msg.startsWith("/order")) {
                            msg = msg.substring(7);
                            ArrayList<String> order = new ArrayList<>(Arrays.asList(msg.split(" ")));
                            handler.post(() -> gameRoomActivity.createGame(order));
                        } else {
                            handler.post(() -> Toast.makeText(context, finalMsg, Toast.LENGTH_SHORT).show());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Sender {

        public void sendMessage(String msg) {
            new Thread(() -> out.println(msg)).start();
        }
    }

    public void setGameRoomActivity(GameRoomActivity gameRoomActivity) {
        this.gameRoomActivity = gameRoomActivity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
