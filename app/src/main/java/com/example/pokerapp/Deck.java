package com.example.pokerapp;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private final ArrayList<Card> deck;
    private int currentCard;

    public Deck() {
        String values = "2 3 4 5 6 7 8 9 10 11 12 13 14";
        String colors = "CDHS";
        ArrayList<String> images = new ArrayList<>();
        images.add("@drawable/c2");
        images.add("@drawable/d2");
        images.add("@drawable/h2");
        images.add("@drawable/s2");
        images.add("@drawable/c3");
        images.add("@drawable/d3");
        images.add("@drawable/h3");
        images.add("@drawable/s3");
        images.add("@drawable/c4");
        images.add("@drawable/d4");
        images.add("@drawable/h4");
        images.add("@drawable/s4");
        images.add("@drawable/c5");
        images.add("@drawable/d5");
        images.add("@drawable/h5");
        images.add("@drawable/s5");
        images.add("@drawable/c6");
        images.add("@drawable/d6");
        images.add("@drawable/h6");
        images.add("@drawable/s6");
        images.add("@drawable/c7");
        images.add("@drawable/d7");
        images.add("@drawable/h7");
        images.add("@drawable/s7");
        images.add("@drawable/c8");
        images.add("@drawable/d8");
        images.add("@drawable/h8");
        images.add("@drawable/s8");
        images.add("@drawable/c9");
        images.add("@drawable/d9");
        images.add("@drawable/h9");
        images.add("@drawable/s9");
        images.add("@drawable/c10");
        images.add("@drawable/d10");
        images.add("@drawable/h10");
        images.add("@drawable/s10");
        images.add("@drawable/cj");
        images.add("@drawable/dj");
        images.add("@drawable/hj");
        images.add("@drawable/sj");
        images.add("@drawable/cq");
        images.add("@drawable/dq");
        images.add("@drawable/hq");
        images.add("@drawable/sq");
        images.add("@drawable/ck");
        images.add("@drawable/dk");
        images.add("@drawable/hk");
        images.add("@drawable/sk");
        images.add("@drawable/ca");
        images.add("@drawable/da");
        images.add("@drawable/ha");
        images.add("@drawable/sa");

        deck = new ArrayList<>();

        int i = 0;
        for (String v: values.split(" ")) {
            for (String c: colors.split(" ")) {
                deck.add(new Card(Integer.parseInt(v), c, images.get(i)));
                i++;
            }
        }

        currentCard = -1;
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public void resetCurrentCard() {
        setCurrentCard(0);
    }

    public Card getCard() {
        currentCard += 1;
        return deck.get(currentCard);
    }

    public void setCurrentCard(int currentCard) {
        this.currentCard = currentCard;
    }
}
