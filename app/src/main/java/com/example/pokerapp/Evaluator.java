package com.example.pokerapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Evaluator {

    private ArrayList<Card> communityCards;
    private final ArrayList<String> handPower;
    private ArrayList<Player> winnerList;
    private String bestHand;
    private ArrayList<Integer> bestValues;

    public Evaluator() {
        winnerList = new ArrayList<>();
        bestHand = null;
        bestValues = new ArrayList<>();
        handPower = new ArrayList<>();
        handPower.add("High card");
        handPower.add("One pair");
        handPower.add("Two pair");
        handPower.add("Three of a kind");
        handPower.add("Straight");
        handPower.add("Flush");
        handPower.add("Full House");
        handPower.add("Four of a kind");
        handPower.add("Straight Flush");
    }

    public void evaluate(Player player, ArrayList<Card> playerCards) {
        ArrayList<Card> hand = new ArrayList<>();
        hand.addAll(playerCards);
        hand.addAll(communityCards);

        ArrayList<Integer> values = new ArrayList<>();
        HashMap<String, Integer> colors = new HashMap<>();

        boolean flush = false;
        String color = null;
        boolean straight = false;

        int betterGroupCount = 0;
        int betterGroupValue = 0;
        int weakerGroupCount = 0;
        int weakerGroupValue = 0;

        for (Card card : hand) {
            values.add(card.getValue());
            int count = colors.getOrDefault(card.getColor(), 0);
            colors.put(card.getColor(), count + 1);
        }

        if (Collections.max(colors.values()) >= 5) {
            flush = true;
            for (String s: "CDHS".split("")) {
                if (colors.getOrDefault(s, 0) >= 5) {
                    color = s;
                    break;
                }
            }
        }

        Collections.sort(values);
        values.add(20);
        int straightCount = 1;
        int biggestStraight = 0;
        int sameCardsCount = 1;

        for (int i = 0; i < 7; i++) {
            int currentValue = values.get(i);
            if (values.get(i + 1) - currentValue == 1) {
                straightCount += 1;
                if (currentValue == 4 && straightCount == 4 && values.get(6) == 14) {
                    straight = true;
                    biggestStraight = 5;
                }
            } else if (values.get(i + 1) - currentValue == 0){
                assert true;
            } else {
                straightCount = 1;
            }

            if (straightCount >= 5) {
                straight = true;
                biggestStraight = currentValue + 1;
            }

            if (values.get(i + 1) - currentValue == 0) {
                sameCardsCount += 1;
            } else {
                if (sameCardsCount == 2) {
                    if (sameCardsCount >= betterGroupCount && currentValue > betterGroupValue) {
                        weakerGroupCount = betterGroupCount;
                        weakerGroupValue = betterGroupValue;
                        betterGroupCount = sameCardsCount;
                        betterGroupValue = currentValue;
                    } else if (betterGroupCount == 3 || (currentValue > betterGroupValue && currentValue > weakerGroupValue)){
                        weakerGroupCount = sameCardsCount;
                        weakerGroupValue = currentValue;
                    }
                } else if (sameCardsCount == 3) {
                    if (currentValue > betterGroupValue) {
                        if (betterGroupCount == 3) {
                            betterGroupCount -= 1;
                        }
                        weakerGroupCount = betterGroupCount;
                        weakerGroupValue = betterGroupValue;
                        betterGroupCount = 3;
                        betterGroupValue = currentValue;
                    } else if ((betterGroupCount == 3 && betterGroupValue > currentValue) && currentValue > weakerGroupValue) {
                        weakerGroupCount = 2;
                        weakerGroupValue = currentValue;
                    }
                } else if (sameCardsCount == 4) {
                    betterGroupCount = 4;
                    betterGroupValue = currentValue;
                    break;
                }
                sameCardsCount = 1;
            }
        }

        values.remove(values.size() - 1);

        if (flush && straight) {
            player.setHandPower("Straight Flush");
            player.addToHandValues(biggestStraight);
        } else if (sameCardsCount == 4) {
            player.setHandPower("Four of a kind");
            player.addToHandValues(betterGroupValue);
            for (int i = 6; i >= 0; i--) {
                if (values.get(i) != betterGroupValue) {
                    player.addToHandValues(values.get(i));
                    break;
                }
            }
        } else if (betterGroupCount == 3 && weakerGroupCount == 2) {
            player.setHandPower("Full House");
            player.addToHandValues(betterGroupValue);
            player.addToHandValues(weakerGroupValue);
        } else if (flush) {
            ArrayList<Integer> colval = new ArrayList<>();
            player.setHandPower("Flush");
            for (int i = 6; i >= 0; i--) {
                if (hand.get(i).getColor().equals(color)) {
                    colval.add(hand.get(i).getValue());
                }
            }
            Collections.sort(colval);
            for (int i = colval.size() - 1; i >= colval.size() - 5; i--) {
                player.addToHandValues(colval.get(i));
            }
        } else if (straight) {
            player.setHandPower("Straight");
            player.addToHandValues(biggestStraight);
        } else if (betterGroupCount == 3) {
            player.setHandPower("Three of a kind");
            player.addToHandValues(betterGroupValue);
            int count = 0;
            for (int i = 6; i >= 0; i--) {
                if (values.get(i) != betterGroupValue && count < 2) {
                    count += 1;
                    player.addToHandValues(values.get(i));
                }
            }
        } else if (betterGroupCount == 2 && weakerGroupCount == 2) {
            player.setHandPower("Two pair");
            player.addToHandValues(betterGroupValue);
            player.addToHandValues(weakerGroupValue);
            for (int i = 6; i >= 0; i--) {
                if (values.get(i) != betterGroupValue && values.get(i) != weakerGroupValue) {
                    player.addToHandValues(values.get(i));
                    break;
                }
            }
        } else if (betterGroupCount == 2) {
            player.setHandPower("One pair");
            player.addToHandValues(betterGroupValue);
            int count = 0;
            for (int i = 6; i > 1; i--) {
                if (values.get(i) != betterGroupValue && count < 4) {
                    count += 1;
                    player.addToHandValues(values.get(i));
                }
            }
        } else {
            player.setHandPower("high card");
            for (int i = 6; i > 1; i--) {
                player.addToHandValues(values.get(i));
            }
        }
        checkWinner(player);
    }

    public void checkWinner(Player player) {
        if (handPower.indexOf(player.getHandPower()) > handPower.indexOf(bestHand)) {
            winnerList.clear();
            winnerList.add(player);
            setBestHand(player.getHandPower());
            setBestValues(player.getHandValues());
        } else if (handPower.indexOf(player.getHandPower()) == handPower.indexOf(bestHand)) {
            for (int i = 0; i < bestValues.size(); i++) {
                if (player.getHandValues().get(i) > bestValues.get(i)) {
                    winnerList.clear();
                    winnerList.add(player);
                    setBestValues(player.getHandValues());
                    break;
                } else if (player.getHandValues().get(i) < bestValues.get(i)) {
                    break;
                } else if (i == bestValues.size() - 1){
                    winnerList.add(player);
                }
            }
        }
    }

    public void restart() {
        setCommunityCards(null);
        setWinnerList(new ArrayList<>());
        setBestHand("High card");
        setBestValues(new ArrayList<>());
        bestValues.add(1);
    }

    public void setCommunityCards(ArrayList<Card> communityCards) {
        this.communityCards = communityCards;
    }

    public ArrayList<Player> getWinnerList() {
        return winnerList;
    }

    public void setWinnerList(ArrayList<Player> winnerList) {
        this.winnerList = winnerList;
    }

    public void setBestHand(String bestHand) {
        this.bestHand = bestHand;
    }

    public void setBestValues(ArrayList<Integer> bestValues) {
        this.bestValues = bestValues;
    }
}