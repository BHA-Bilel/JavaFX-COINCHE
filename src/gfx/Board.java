package gfx;

import game.Handler;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import model.*;
import shared.RoomPosition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Board extends StackPane {

    private final Handler handler;

    // 1ST PHASE
    private final FirstPhaseGUI first_phase_gui;
    protected Bid latestBid;

    private int passCount;
    private final Score score;
    private boolean scoreShown;
    private int beloteID;
    protected int first_capot_bid;

    // 2ND PHASE
    private SecondPhaseGUI second_phase_gui;
    // LOGIC
    private CountDownLatch decShow_latch, play_wait_latch;
    private boolean secondPhase;
    private Card leftCard, bottomCard, topCard, rightCard;
    private Card demanded;
    private Card maitre;
    private int yourPts, opponentPts;
    protected int round;

    protected int yourPlies_count = 0, opPlies_count = 0;

    public Board(Handler handler) {
        this.handler = handler;
        setAlignment(Pos.CENTER);
        score = new Score();
        first_phase_gui = new FirstPhaseGUI(handler, this);
    }

    // 1ST PHASE

    public void initFirstPhase() {
        if (handler.getGame().first_game) {
            handler.getGame().initPlayers();
            handler.getGame().first_game = false;
        } else {
            first_phase_gui.reset_gui();
        }
        handler.getGame().loadCards();
        handler.getGame().setPlayable(false);
        latestBid = null;
        first_capot_bid = 0;
        yourPts = 0;
        opponentPts = 0;
        beloteID = -1;
        secondPhase = false;
        passCount = 0;
        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().add(first_phase_gui);
        });
    }

    public boolean buy(RoomPosition currentPosition, Suit anySuit, int bid, boolean capot) {
        first_phase_gui.adapt_sliders(capot);
        if (currentPosition == RoomPosition.BOTTOM) {
            handler.getGame().getBuyThread().send(anySuit.getIndex(), bid, capot);
        }
        boolean adapt = false;
        if (bid == -2) { // coinche value -2
            passCount = 0;
            latestBid.coinche();
            first_phase_gui.coinche(currentPosition);
            coinched(currentPosition);
            handler.getGame().setNextTurn(currentPosition);
            adapt = true;
        } else if (bid == -4) { // surcoinche value -4
            latestBid.surcoinche();
            surcoinched(currentPosition);
            initSecondPhase();
            return true;
        } else if (bid > 0 || capot && first_capot_bid == 0) {
            passCount = 0;
            switch (currentPosition) {
                case BOTTOM -> {
                    handler.getGame().getBottomPane().bought(anySuit, bid, capot);
                    latestBid = handler.getGame().getBottomPane().getBid();
                }
                case RIGHT -> {
                    handler.getGame().getRightPane().bought(anySuit, bid, capot);
                    latestBid = handler.getGame().getRightPane().getBid();
                }
                case TOP -> {
                    handler.getGame().getTopPane().bought(anySuit, bid, capot);
                    latestBid = handler.getGame().getTopPane().getBid();
                }
                case LEFT -> {
                    handler.getGame().getLeftPane().bought(anySuit, bid, capot);
                    latestBid = handler.getGame().getLeftPane().getBid();
                }
            }
            first_capot_bid = capot ? 1 : 0;
            first_phase_gui.update_value(latestBid);
        } else if (bid == 0) {
            passCount++;
            if (latestBid == null && passCount == 4 || latestBid != null && passCount == 3) {
                if (latestBid == null) {
                    if (currentPosition == RoomPosition.BOTTOM)
                        handler.getGame().getBuyThread().startAnotherGame();
                    handler.getGame().startNewGame();
                } else
                    initSecondPhase();
                return true;
            }
            if (latestBid != null && latestBid.isCorS()) {
                handler.getGame().switchTurn();
                adapt = true;
                passCount = 2;
            } else
                pass(currentPosition);
        }
        if (currentPosition == RoomPosition.BOTTOM)
            handler.getGame().getBuyThread().WaitForYourTurn();
        handler.getGame().switchTurn();
        if (adapt) {
            adapt_turn();
        } else {
            switchTurn();
        }
        return false;
    }

    public void pass(RoomPosition position) {
        switch (position) {
            case BOTTOM -> handler.getGame().getBottomPane().pass_turn();
            case RIGHT -> handler.getGame().getRightPane().pass_turn();
            case TOP -> handler.getGame().getTopPane().pass_turn();
            case LEFT -> handler.getGame().getLeftPane().pass_turn();
        }
    }

    public void coinched(RoomPosition position) {
        switch (position) {
            case BOTTOM -> handler.getGame().getBottomPane().setCoinche(true);
            case RIGHT -> handler.getGame().getRightPane().setCoinche(true);
            case TOP -> handler.getGame().getTopPane().setCoinche(true);
            case LEFT -> handler.getGame().getLeftPane().setCoinche(true);
        }
    }

    public void surcoinched(RoomPosition position) {
        switch (position) {
            case BOTTOM -> handler.getGame().getBottomPane().setCoinche(false);
            case RIGHT -> handler.getGame().getRightPane().setCoinche(false);
            case TOP -> handler.getGame().getTopPane().setCoinche(false);
            case LEFT -> handler.getGame().getLeftPane().setCoinche(false);
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    // 2ND PHASE

    public void initSecondPhase() {
        second_phase_gui = new SecondPhaseGUI(handler, this);
        handler.getGame().hide_pass_bids();
        handler.getGame().setNextTurn(handler.getGame().getDealer());
        handler.getGame().switchTurn();
        handler.getGame().disable_animatio_timer();
        secondPhase = true;
        setup_first_turn();
        round = 1;
        handler.getGame().getPlayThread().initSecondPhase();
        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().add(second_phase_gui);
        });
    }

    public void setup_first_turn() {
        if (secondPhase) {
            second_phase_gui.setup_first_turn();
        } else {
            first_phase_gui.setup_first_turn();
        }
    }

    public void play(Card card) {
        MyTimeLine say = null;
        if (demanded == null) {
            demanded = card;
            maitre = demanded;
            if (card.getSuit() == latestBid.getTrump())
                say = second_phase_gui.say(card.getPosition(), "Atout", null);
        } else {
            int cardRankValue = card.getValue(latestBid.getTrump());
            int maitreRankValue = maitre.getValue(latestBid.getTrump());
            int cardRankIndex = card.getRank().getIndex();
            int maitreRankIndex = maitre.getRank().getIndex();
            boolean monte = (cardRankValue > maitreRankValue
                    || (cardRankValue == maitreRankValue && cardRankIndex > maitreRankIndex))
                    && card.getSuit() == maitre.getSuit();
            boolean piss = card.getSuit() == latestBid.getTrump() && card.getSuit() == maitre.getSuit() &&
                    (cardRankValue < maitreRankValue
                            || (cardRankValue == maitreRankValue && cardRankIndex < maitreRankIndex));
            boolean coupe = card.getSuit() == latestBid.getTrump() && maitre.getSuit() != latestBid.getTrump();
            if (monte || coupe) {
                maitre = card;
                if (coupe) {
                    say = second_phase_gui.say(card.getPosition(), "Je coupe", null);
                } else {
                    if (card.getSuit() == latestBid.getTrump()) {
                        say = second_phase_gui.say(card.getPosition(), "Je monte", null);
                    } else {
                        say = second_phase_gui.say(card.getPosition(), "Maitre", null);
                    }
                }
            } else if (piss) {
                say = second_phase_gui.say(card.getPosition(), "Je piss", null);
            }
        }
        int beloteId = 0;
        List<Combination> declarations = null;
        switch (handler.getGame().getCurrentPosition()) {
            case BOTTOM -> {
                handler.getGame().getPlayThread().send(card.getSuit().getIndex(), card.getRank().getIndex());
                bottomCard = card;
                declarations = handler.getGame().getBottomPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getBottomPane().checkForBelote() ? 1 : -1;
            }
            case RIGHT -> {
                rightCard = card;
                declarations = handler.getGame().getRightPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getRightPane().checkForBelote() ? 2 : -1;
            }
            case TOP -> {
                topCard = card;
                declarations = handler.getGame().getTopPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getTopPane().checkForBelote() ? 1 : -1;
            }
            case LEFT -> {
                leftCard = card;
                declarations = handler.getGame().getLeftPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getLeftPane().checkForBelote() ? 2 : -1;
            }
        }
        handler.getGame().removeCard();

        String belote = null;
        if (card.getRank() == Rank.Queen) {
            belote = "Belote";
        } else if (card.getRank() == Rank.King) {
            belote = "Rebelote";
        }
        if (belote != null && beloteId > 0 && card.getSuit() == latestBid.getTrump()) {
            beloteID = beloteId;
            if (say == null)
                say = second_phase_gui.say(card.getPosition(), belote, null);
            else
                second_phase_gui.add_belote_to_say(card.getPosition(), say, belote);
        }

        List<Combination> finalDeclarations = declarations;
        Thread annonceThread = new Thread(() -> {
            if (round == 3) {
                decShow_latch = null;
                if (finalDeclarations != null) {
                    decShow_latch = new CountDownLatch(finalDeclarations.size());
                    for (Combination comb : finalDeclarations) {
                        if (comb.getType() != ComboType.Belote) {
                            second_phase_gui.showDec(card.getPosition(), comb, decShow_latch);
                        } else {
                            decShow_latch.countDown();
                        }
                    }
                }
                if (decShow_latch != null) {
                    try {
                        decShow_latch.await();
                    } catch (InterruptedException ignore) {
                    }
                }
            }
            boolean full = isFull();
            if (full) {
                checkWinner();
            } else {
                handler.getGame().switchTurn();
                switchTurn();
            }
            if (card.getPosition() != RoomPosition.BOTTOM) {
                play_wait_latch.countDown();
            }
        });
        play_wait_latch = new CountDownLatch(1);
        second_phase_gui.play(card, say, annonceThread, isFull());
        if (card.getPosition() != RoomPosition.BOTTOM) {
            try {
                play_wait_latch.await();
            } catch (InterruptedException ignore) {
            }
        }
    }

    private boolean isFull() {
        return leftCard != null && bottomCard != null && topCard != null && rightCard != null;
    }

    private void checkWinner() {
        round++;
        boolean der;
        Suit suit = latestBid.getTrump() != Suit.TA ? latestBid.getTrump() : null;
        int points = bottomCard.getValue(suit)
                + leftCard.getValue(suit)
                + topCard.getValue(suit)
                + rightCard.getValue(suit);

        if (maitre == bottomCard) {
            der = latestBid.youBought();
            handler.getGame().setNextTurn(RoomPosition.BOTTOM);
            yourPts += points;
            yourPlies_count += 1;
        } else if (maitre == leftCard) {
            der = !latestBid.youBought();
            handler.getGame().setNextTurn(RoomPosition.LEFT);
            opponentPts += points;
            opPlies_count += 1;
        } else if (maitre == topCard) {
            der = latestBid.youBought();
            handler.getGame().setNextTurn(RoomPosition.TOP);
            yourPts += points;
            yourPlies_count += 1;
        } else { // RIGHT
            der = !latestBid.youBought();
            handler.getGame().setNextTurn(RoomPosition.RIGHT);
            opponentPts += points;
            opPlies_count += 1;
        }
        clearBoard(der);
    }

    public void clearBoard(boolean der) {
        handler.getGame().setPlayable(false);
        second_phase_gui.clear(demanded.getPosition(), der);
        demanded = null;
        maitre = null;
        leftCard = null;
        bottomCard = null;
        topCard = null;
        rightCard = null;
    }

    public int getYourPts(boolean sa_ta, boolean won_all_hands, int der) {
        yourPts += der;
        return sa_ta ? yourPts * 2 : won_all_hands ? 250 : yourPts;
    }

    public int getOpponentPts(boolean sa_ta, boolean won_all_hands, int der) {
        opponentPts += der;
        return sa_ta ? opponentPts * 2 : won_all_hands ? 250 : opponentPts;
    }

    public Card getDemanded() {
        return demanded;
    }

    public Card getMaitre() {
        return maitre;
    }

    public void show(boolean tabClicked) {
        if (secondPhase)
            second_phase_gui.showPrev(tabClicked);
        else
            showScore(tabClicked);
    }

    private void showScore(boolean show) {
        if (scoreShown && !show) {
            getChildren().clear();
            getChildren().add(first_phase_gui);
            scoreShown = false;
        } else if (show && !scoreShown) {
            scoreShown = true;
            getChildren().clear();
            getChildren().add(score);
        }
    }

    public Score getScore() {
        return score;
    }

    public int getBelote() {
        return beloteID;
    }

    public void resetPlies_count() {
        yourPlies_count = 0;
        opPlies_count = 0;
    }

    public void switchTurn() {
        if (secondPhase) {
            second_phase_gui.switchTurn();
        } else {
            first_phase_gui.switchTurn();
        }
    }

    public void adapt_turn() {
        if (secondPhase) {
            second_phase_gui.adapt_turn();
        } else {
            first_phase_gui.adapt_turn();
        }
    }

    public void announceDeclarations(List<Combination> bottom, List<Combination> right, List<Combination> top, List<Combination> left) {
        Map<RoomPosition, String> decs = new HashMap<>();
        if (bottom != null && !bottom.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (bottom.get(0).getType() != ComboType.Belote)
                combs_str.append(bottom.get(0).getType());
            for (int i = 1; i < bottom.size(); i++) {
                if (bottom.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(bottom.get(i).getType());
                }
            }
            decs.put(RoomPosition.BOTTOM, combs_str.toString());
        }
        if (right != null && !right.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (right.get(0).getType() != ComboType.Belote)
                combs_str.append(right.get(0).getType());
            for (int i = 1; i < right.size(); i++) {
                if (right.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(right.get(i).getType());
                }
            }
            decs.put(RoomPosition.RIGHT, combs_str.toString());
        }
        if (top != null && !top.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (top.get(0).getType() != ComboType.Belote)
                combs_str.append(top.get(0).getType());
            for (int i = 1; i < top.size(); i++) {
                if (top.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(top.get(i).getType());
                }
            }
            decs.put(RoomPosition.TOP, combs_str.toString());
        }
        if (left != null && !left.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (left.get(0).getType() != ComboType.Belote)
                combs_str.append(left.get(0).getType());
            for (int i = 1; i < left.size(); i++) {
                if (left.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(left.get(i).getType());
                }
            }
            decs.put(RoomPosition.LEFT, combs_str.toString());
        }
        second_phase_gui.reveal_decs(decs);
    }
}
