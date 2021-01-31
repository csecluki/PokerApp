package com.example.pokerapp;

public class ClientHolder {

    private static Client client;

    public static synchronized Client getClient() {
        return client;
    }

    public static synchronized void setClient(Client client) {
        ClientHolder.client = client;
    }
}
