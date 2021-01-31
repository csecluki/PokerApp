package com.example.pokerapp;

public class ServerHolder {

    private static Server server;

    public static synchronized Server getServer() {
        return server;
    }

    public static synchronized void setServer(Server server) {
        ServerHolder.server = server;
    }
}
