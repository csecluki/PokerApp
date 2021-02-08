package com.example.pokerapp;

import java.util.ArrayList;

public class Player {

    private final String name;
    private Boolean inRound;
    private ArrayList<Card> hand;
    private String handPower;
    private ArrayList<Integer> handValues;
    private int currentBid;
    private int totalBid;
    private int balance;

    public Player(String name) {
        this.name = name;
        inRound = true;
        hand = new ArrayList<>();
        balance = 10000;
        handValues = new ArrayList<>();
    }

    public void bet(int i) {
        balance -= i;
        currentBid += i;
        totalBid += i;
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public void addToHandValues(Integer value) {
        handValues.add(value);
    }

    public void addWonMoney(Integer prize) {
        balance += prize;
    }

    public void prepareForNewGame() {
        setInRound(true);
        setHand(new ArrayList<>());
        setHandPower(null);
        setHandValues(new ArrayList<>());
        setCurrentBid(0);
        setTotalBid(0);
    }

    public String getName() {
        return name;
    }

    public Boolean getInRound() {
        return inRound;
    }

    public void setInRound(Boolean inRound) {
        this.inRound = inRound;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public String getHandPower() {
        return handPower;
    }

    public void setHandPower(String handPower) {
        this.handPower = handPower;
    }

    public ArrayList<Integer> getHandValues() {
        return handValues;
    }

    public void setHandValues(ArrayList<Integer> handValues) {
        this.handValues = handValues;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(int currentBid) {
        this.currentBid = currentBid;
    }

    public int getTotalBid() {
        return totalBid;
    }

    public void setTotalBid(int totalBid) {
        this.totalBid = totalBid;
    }

    public int getBalance() {
        return balance;
    }
}