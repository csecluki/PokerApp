package com.example.pokerapp;

import java.util.ArrayList;

public class Game {

    private final Deck deck;
    private final Evaluator evaluator;
    private final ArrayList<Player> playerList;
    private ArrayList<Player> winnerList;
    private ArrayList<Card> communityCards;
    private int blind;
    private Player dealer;
    private Player currentPlayer;
    private int inGamePlayers;
    private int pool;
    private String turn;
    private int biggestBid;
    private Player biggestBidPlayer;

    public Game() {
        deck = new Deck();
        evaluator = new Evaluator();
        playerList = new ArrayList<>();
        winnerList = new ArrayList<>();
        blind = 200;
        dealer = null;
        currentPlayer = null;
        pool = 0;
        biggestBid = 0;
        biggestBidPlayer = null;
    }

    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public void startGame() {
        setTurn("Pre-Flop");
        setWinnerList(new ArrayList<>());
        setBiggestBid(0);
        setBiggestBidPlayer(null);
        setPool(0);
        setInGamePlayers(playerList.size());

        switchDealer();
        setCurrentPlayer(dealer);

        setCommunityCards(new ArrayList<>());

        evaluator.restart();

        deck.shuffle();
        hand();
        beginning();
    }

    public void hand() {
        deck.resetCurrentCard();
        for (Player player : playerList) {
            player.setInRound(true);
            player.addToHand(deck.getCard());
            player.addToHand(deck.getCard());
        }
    }

    public void beginning() {
        switchPlayer();
        currentPlayer.bet(blind);
        switchPlayer();
        currentPlayer.bet(blind * 2);
        biggestBid = currentPlayer.getCurrentBid();
        switchPlayer();
    }

    public void switchDealer() {
        try {
            setDealer(playerList.get(playerList.indexOf(dealer) + 1));
        } catch (IndexOutOfBoundsException e) {
            setDealer(playerList.get(0));
        }
    }

    public void switchPlayer() {
        if (inGamePlayers == 1) {
            updatePool();
            onePlayerEnd();
        } else {
            do {
                try {
                    currentPlayer = playerList.get(playerList.indexOf(currentPlayer) + 1);
                } catch (IndexOutOfBoundsException e) {
                    currentPlayer = playerList.get(0);
                }
            } while (!currentPlayer.getInRound());
            System.out.println("Player: " + currentPlayer.getName());
            if (currentPlayer.equals(biggestBidPlayer)) {
                goToNextTurn();
            }
        }
    }

    public void fold() {
        currentPlayer.setInRound(false);
        inGamePlayers -= 1;
        System.out.println(currentPlayer.getName() + " IS OUT!!!");
        switchPlayer();
    }

    public void allIn() {
        currentPlayer.bet(currentPlayer.getBalance());
        switchPlayer();
    }

    public void check() {
        if (biggestBidPlayer == null) {
            setBiggestBidPlayer(currentPlayer);
        }
        switchPlayer();
    }

    public void call() {
        currentPlayer.bet(biggestBid - currentPlayer.getCurrentBid());
        if (biggestBidPlayer == null) {
            setBiggestBidPlayer(currentPlayer);
        }
        switchPlayer();
    }

    public void raise(int amount) {
        currentPlayer.bet(amount);
        setBiggestBid(currentPlayer.getCurrentBid());
        setBiggestBidPlayer(currentPlayer);
        switchPlayer();
    }

    public void goToNextTurn() {
        setBiggestBidPlayer(null);
        setBiggestBid(0);
        updatePool();
        for (Player player: playerList) {
            player.setCurrentBid(0);
        }
        if ("Pre-Flop".equals(getTurn())) {
            flop();
        } else if ("Flop".equals(getTurn())) {
            turn();
        } else if ("Turn".equals(getTurn())) {
            river();
        } else if ("River".equals(getTurn())) {
            showdown();
        }
    }

    public void updatePool() {
        for (Player player: playerList) {
            pool += player.getCurrentBid();
        }
    }

    public void flop() {
        setTurn("Flop");
        System.out.println("FLOP");
        for (int i = 0; i < 3; i++) {
            communityCards.add(deck.getCard());
        }
        giveTurnToDealer();
    }

    public void turn() {
        setTurn("Turn");
        System.out.println("TURN");
        communityCards.add(deck.getCard());
        giveTurnToDealer();
    }

    public void river() {
        setTurn("River");
        System.out.println("RIVER");
        communityCards.add(deck.getCard());
        giveTurnToDealer();
    }

    public void giveTurnToDealer() {
        setCurrentPlayer(dealer);
        while (!currentPlayer.getInRound()) {
            switchPlayer();
        }
    }

    public void showdown() {
        evaluator.setCommunityCards(communityCards);
        for (Player player: playerList) {
            if (player.getInRound()) {
                evaluator.evaluate(player, player.getHand());
            }
        }
        setWinnerList(evaluator.getWinnerList());
        distributePrize();
    }

    public void onePlayerEnd() {
        for (Player player: playerList) {
            if (player.getInRound()) {
                player.addWonMoney(pool);
                break;
            }
        }
        ending();
    }

    public void distributePrize() {
        while (winnerList.size() > 1) {
            int winnerMin = Integer.MAX_VALUE;
            for (int i = 0; i < winnerList.size(); i++) {
                Player player = winnerList.get(i);
                int bid = player.getTotalBid();
                if (bid == 0) {
                    winnerList.remove(player);
                    i--;
                } else {
                    if (winnerMin > bid) {
                        winnerMin = bid;
                    }
                }
            }
            int split = 0;
            for (Player player : playerList) {
                int bid = player.getTotalBid();
                split += Math.min(winnerMin, bid);
                player.setTotalBid(bid - Math.min(winnerMin, bid));
            }
            pool -= split;
            for (Player player : winnerList) {
                player.addWonMoney(split / winnerList.size());
            }
        }
        if (winnerList.size() == 1) {
            winnerList.get(0).addWonMoney(pool);
        }
        ending();
    }

    public void ending() {
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getBalance() == 0) {
                playerList.remove(i);
                i--;
            }
        }
        for (Player player: playerList) {
            player.prepareForNewGame();
        }
        startGame();
    }

    public void printInfo() {
        System.out.println("Player: " + currentPlayer.getName());
        System.out.println("Hand: " +
                currentPlayer.getHand().get(0).getValue() +
                currentPlayer.getHand().get(0).getColor() + " " +
                currentPlayer.getHand().get(1).getValue() +
                currentPlayer.getHand().get(1).getColor());
        System.out.println("Balance: " + currentPlayer.getBalance());
        System.out.println("Biggest bid: " + getBiggestBid());
        System.out.println("Player bid: " + currentPlayer.getTotalBid());
        System.out.println("Pool: " + getPool());
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public ArrayList<Player> getWinnerList() {
        return winnerList;
    }

    public void setWinnerList(ArrayList<Player> winnerList) {
        this.winnerList = winnerList;
    }

    public ArrayList<Card> getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(ArrayList<Card> communityCards) {
        this.communityCards = communityCards;
    }

    public void setBlind(int blind) {
        this.blind = blind;
    }

    public void setDealer(Player dealer) {
        this.dealer = dealer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setInGamePlayers(int inGamePlayers) {
        this.inGamePlayers = inGamePlayers;
    }

    public int getPool() {
        return pool;
    }

    public void setPool(int pool) {
        this.pool = pool;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public int getBiggestBid() {
        return biggestBid;
    }

    public void setBiggestBid(int biggestBid) {
        this.biggestBid = biggestBid;
    }

    public void setBiggestBidPlayer(Player biggestBidPlayer) {
        this.biggestBidPlayer = biggestBidPlayer;
    }
}