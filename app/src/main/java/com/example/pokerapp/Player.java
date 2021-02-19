package com.example.pokerapp;

import android.view.View;
import android.widget.TextView;

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

    private View view;
    private TextView textViewName;
    private TextView textViewBalance;
    private TextView textViewBid;

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

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setTextViewName(TextView textViewName) {
        this.textViewName = textViewName;
    }

    public void labelTextViewName(String name) {
        textViewName.setText(name);
    }

    public void setTextViewBalance(TextView textViewBalance) {
        this.textViewBalance = textViewBalance;
    }

    public void labelTextViewBalance(String balance) {
        textViewBalance.setText(balance);
    }

    public void setTextViewBid(TextView textViewBid) {
        this.textViewBid = textViewBid;
    }

    public void labelTextViewBid(String bid) {
        textViewBid.setText(bid);
    }
}