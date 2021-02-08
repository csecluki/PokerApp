package com.example.pokerapp;

public class Card {

    private final int value;
    private final String color;
    private final String imageURI;

    public Card(int value, String color, String imageURI) {
        this.value = value;
        this.color = color;
        this.imageURI = imageURI;
    }

    public int getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public String getImageURI() {
        return imageURI;
    }
}