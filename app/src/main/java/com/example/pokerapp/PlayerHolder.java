package com.example.pokerapp;

public class PlayerHolder {

    private static Player player;
    private static String name;

    public static synchronized Player getPlayer() {
        return player;
    }

    public static synchronized void setPlayer(Player player) {
        PlayerHolder.player = player;
    }

    public static synchronized String getName() {
        return name;
    }

    public static synchronized void setName(String name) {
        PlayerHolder.name = name;
    }
}
