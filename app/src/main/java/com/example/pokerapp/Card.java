package com.example.pokerapp;

public class Card {

    private final int value;
    private final String color;

    public Card(int value, String color) {
        this.value = value;
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}