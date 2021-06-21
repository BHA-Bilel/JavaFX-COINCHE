package bg.coinche.gfx;

import bg.coinche.game.GameApp;
import bg.coinche.game.Handler;
import bg.coinche.lang.Language;
import bg.coinche.model.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import shared.RoomPosition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Board extends StackPane {

    private final Handler handler;

    // 1ST PHASE
    private final BuyGUI buy_gui;
    protected Bid latestBid;

    private int passCount;
    private final Score score;
    private boolean scoreShown;
    protected int first_capot_bid;

    // 2ND PHASE
    private final PlayGUI play_gui;
    // LOGIC
    private CountDownLatch decShow_latch, play_wait_latch;
    private boolean playing, on_time;
    private Card leftCard, bottomCard, topCard, rightCard;
    private Card demanded;
    private Card maitre;
    private int yourPts, opponentPts;
    protected int round;

    protected int yourPlies_count, opPlies_count;

    public Board(Handler handler) {
        this.handler = handler;
        setAlignment(Pos.CENTER);
        score = new Score();
        buy_gui = new BuyGUI(handler, this);
        play_gui = new PlayGUI(handler, this);
    }

    // BUY PHASE

    public void setupBuyPhase() {
        buy_gui.reset_gui();
        buy_gui.hide_turn();
        play_gui.hide_turn();
        buy_gui.setup_first_turn();
        latestBid = null;
        playing = false;
        first_capot_bid = 0;
        yourPts = 0;
        opponentPts = 0;
        passCount = 0;
        yourPlies_count = 0;
        opPlies_count = 0;
        on_time = true;
    }

    public void set_buy_gui() {
        getChildren().clear();
        getChildren().add(buy_gui);
        if (on_time) buy_gui.show_first_turn();
    }

    public void buy(RoomPosition currentPosition, Suit anySuit, int bid, boolean capot) {
        if (currentPosition == RoomPosition.BOTTOM) handler.getGame().bought(anySuit.ordinal(), bid, capot);
        if (capot && first_capot_bid == 0) first_capot_bid = 1;
        passCount = 0;
        on_time = false;
        switch (currentPosition) {
            case BOTTOM: {
                TopBottomPane pane = handler.getGame().getBottomPane();
                pane.bought(anySuit, bid, capot);
                latestBid = pane.getBid();
                break;
            }
            case RIGHT: {
                LeftRightPane pane = handler.getGame().getRightPane();
                pane.bought(anySuit, bid, capot);
                latestBid = pane.getBid();
                break;
            }
            case TOP: {
                TopBottomPane pane = handler.getGame().getTopPane();
                pane.bought(anySuit, bid, capot);
                latestBid = pane.getBid();
                break;
            }
            case LEFT: {
                LeftRightPane pane = handler.getGame().getLeftPane();
                pane.bought(anySuit, bid, capot);
                latestBid = pane.getBid();
                break;
            }
        }
        if (buy_gui.update_value(latestBid))
            buy_gui.disable_buttons(
                    latestBid.getPosition().equals(RoomPosition.TOP)
                            || latestBid.getPosition().equals(RoomPosition.BOTTOM));
        handler.getGame().switchTurn();
        buy_gui.switchTurn();
    }

    public void coinche(RoomPosition coinched_position) {
        passCount = 0;
        latestBid.coinche();
        buy_gui.disable_buttons(coinched_position == RoomPosition.TOP
                || coinched_position == RoomPosition.BOTTOM);
        coinched(coinched_position);
        handler.getGame().setNextTurn(coinched_position);
        handler.getGame().switchTurn();
        buy_gui.adapt_turn();
    }

    public void surcoinche() {
        RoomPosition currentPosition = handler.getGame().getCurrentPosition();
        if (currentPosition.equals(RoomPosition.BOTTOM)) {
            handler.getGame().surcoinched();
        }
        latestBid.surcoinche();
        surcoinched(currentPosition);
        initPlayPhase();
    }

    public void pass() {
        GameApp game = handler.getGame();
        RoomPosition currentPosition = game.getCurrentPosition();
        if (currentPosition.equals(RoomPosition.BOTTOM)) game.passed();
        boolean adapt = false;
        passCount++;
        on_time = false;
        if (latestBid == null && passCount == 4 || latestBid != null && passCount == 3) {
            if (latestBid == null) {
                game.startAnotherGame();
                game.clearCards();
                game.reset_panes();
                game.initBuyPhase();
                buy_gui.show_first_turn();
            } else initPlayPhase();
            return;
        }
        if (latestBid != null && latestBid.isCorS()) {
            game.switchTurn();
            adapt = true;
            passCount = 2;
        } else
            pass(currentPosition);
        game.switchTurn();
        if (adapt) buy_gui.adapt_turn();
        else buy_gui.switchTurn();
    }

    public void pass(RoomPosition position) {
        switch (position) {
            case BOTTOM: {
                handler.getGame().getBottomPane().pass_turn();
                break;
            }
            case RIGHT: {
                handler.getGame().getRightPane().pass_turn();
                break;
            }
            case TOP: {
                handler.getGame().getTopPane().pass_turn();
                break;
            }
            case LEFT: {
                handler.getGame().getLeftPane().pass_turn();
                break;
            }
        }
    }

    public void coinched(RoomPosition position) {
        switch (position) {
            case BOTTOM: {
                handler.getGame().getBottomPane().setCoinche(true);
                break;
            }
            case RIGHT: {
                handler.getGame().getRightPane().setCoinche(true);
                break;
            }
            case TOP: {
                handler.getGame().getTopPane().setCoinche(true);
                break;
            }
            case LEFT: {
                handler.getGame().getLeftPane().setCoinche(true);
                break;
            }
        }
    }

    public void surcoinched(RoomPosition position) {
        switch (position) {
            case BOTTOM: {
                handler.getGame().getBottomPane().setCoinche(false);
                break;
            }
            case RIGHT: {
                handler.getGame().getRightPane().setCoinche(false);
                break;
            }
            case TOP: {
                handler.getGame().getTopPane().setCoinche(false);
                break;
            }
            case LEFT: {
                handler.getGame().getLeftPane().setCoinche(false);
                break;
            }
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    // 2ND PHASE

    public void initPlayPhase() {
        handler.getGame().hide_pass_bids();
        handler.getGame().setNextTurn(handler.getGame().getDealer());
        handler.getGame().switchTurn();
        handler.getGame().disable_animation_timer();
        playing = true;
        buy_gui.hide_turn();
        play_gui.setup_first_turn();
        round = 1;
        handler.getGame().initPlayPhase();
        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().add(play_gui);
            play_gui.show_first_turn();
        });
    }

    public void set_buy_if_buying() {
        if (!playing) set_buy_gui();
    }

    public void play(Card card) {
        MyTimeLine say = null;
        if (demanded == null) {
            demanded = card;
            maitre = demanded;
            if (card.getSuit() == latestBid.getTrump())
                say = play_gui.say(card.getPosition(), Language.SAY_ATOUT.getValue(), null);
        } else {
            int card_value = card.getValue(latestBid.getTrump());
            int maitre_value = maitre.getValue(latestBid.getTrump());
            int card_rank = card.getRank().ordinal();
            int maitre_rank = maitre.getRank().ordinal();
            boolean monte = (card_value > maitre_value
                    || (card_value == maitre_value && card_rank > maitre_rank))
                    && card.getSuit() == maitre.getSuit();
            boolean piss = card.getSuit() == latestBid.getTrump() && card.getSuit() == maitre.getSuit() &&
                    (card_value < maitre_value
                            || (card_value == maitre_value && card_rank < maitre_rank));
            boolean coupe = card.getSuit() == latestBid.getTrump() && maitre.getSuit() != latestBid.getTrump();
            if (monte || coupe) {
                maitre = card;
                if (coupe) {
                    say = play_gui.say(card.getPosition(), Language.SAY_COUPE.getValue(), null);
                } else {
                    if (card.getSuit() == latestBid.getTrump()) {
                        say = play_gui.say(card.getPosition(), Language.SAY_MONTE.getValue(), null);
                    } else {
                        say = play_gui.say(card.getPosition(), Language.SAY_MAITRE.getValue(), null);
                    }
                }
            } else if (piss) {
                say = play_gui.say(card.getPosition(), Language.SAY_PISS.getValue(), null);
            }
        }
        int beloteId = 0;
        List<Combination> declarations = null;
        switch (handler.getGame().getCurrentPosition()) {
            case BOTTOM: {
                handler.getGame().Iplayed(card.getSuit().ordinal(), card.getRank().ordinal());
                bottomCard = card;
                declarations = handler.getGame().getBottomPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getBottomPane().checkForBelote() ? 1 : -1;
                break;
            }
            case RIGHT: {
                rightCard = card;
                declarations = handler.getGame().getRightPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getRightPane().checkForBelote() ? 2 : -1;
                break;
            }
            case TOP: {
                topCard = card;
                declarations = handler.getGame().getTopPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getTopPane().checkForBelote() ? 1 : -1;
                break;
            }
            case LEFT: {
                leftCard = card;
                declarations = handler.getGame().getLeftPane().getPlayer().getDeclarations();
                beloteId = handler.getGame().getLeftPane().checkForBelote() ? 2 : -1;
                break;
            }
        }
        handler.getGame().removeCard();

        String belote = null;
        if (card.getRank() == Rank.Queen) {
            belote = Language.BELOTE.getValue();
        } else if (card.getRank() == Rank.King) {
            belote = Language.REBELOTE.getValue();
        }
        if (belote != null && beloteId > 0 && card.getSuit() == latestBid.getTrump()) {
            if (say == null)
                say = play_gui.say(card.getPosition(), belote, null);
            else
                play_gui.add_belote_to_say(card.getPosition(), say, belote);
        }

        List<Combination> finalDeclarations = declarations;
        Thread annonceThread = new Thread(() -> {
            if (round == 3) {
                decShow_latch = null;
                if (finalDeclarations != null) {
                    decShow_latch = new CountDownLatch(finalDeclarations.size());
                    for (Combination comb : finalDeclarations) {
                        if (comb.getType() != ComboType.Belote) {
                            play_gui.showDec(card.getPosition(), comb, decShow_latch);
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
            if (full) checkWinner();
            else {
                handler.getGame().switchTurn();
                play_gui.switchTurn();
            }
            if (card.getPosition() != RoomPosition.BOTTOM) play_wait_latch.countDown();
        });
        play_wait_latch = new CountDownLatch(1);
        play_gui.play(card, say, annonceThread, isFull());
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
        play_gui.adapt_turn();
        clearBoard(der);
    }

    public void clearBoard(boolean der) {
        handler.getGame().setPlayable(false);
        play_gui.clear(demanded.getPosition(), der);
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
        if (playing) play_gui.showPrev(tabClicked);
        else showScore(tabClicked);
    }

    private void showScore(boolean show) {
        if (scoreShown && !show) {
            getChildren().clear();
            getChildren().add(buy_gui);
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

    public void announceDeclarations(List<Combination> bottom, List<Combination> right,
                                     List<Combination> top, List<Combination> left) {
        Map<RoomPosition, String> decs = new HashMap<>();
        if (bottom != null && !bottom.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (bottom.get(0).getType() != ComboType.Belote)
                combs_str.append(bottom.get(0).getType().getNameProperty().getValue());
            for (int i = 1; i < bottom.size(); i++) {
                if (bottom.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(bottom.get(i).getType().getNameProperty().getValue());
                }
            }
            decs.put(RoomPosition.BOTTOM, combs_str.toString());
        }
        if (right != null && !right.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (right.get(0).getType() != ComboType.Belote)
                combs_str.append(right.get(0).getType().getNameProperty().getValue());
            for (int i = 1; i < right.size(); i++) {
                if (right.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(right.get(i).getType().getNameProperty().getValue());
                }
            }
            decs.put(RoomPosition.RIGHT, combs_str.toString());
        }
        if (top != null && !top.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (top.get(0).getType() != ComboType.Belote)
                combs_str.append(top.get(0).getType().getNameProperty().getValue());
            for (int i = 1; i < top.size(); i++) {
                if (top.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(top.get(i).getType().getNameProperty().getValue());
                }
            }
            decs.put(RoomPosition.TOP, combs_str.toString());
        }
        if (left != null && !left.isEmpty()) {
            StringBuilder combs_str = new StringBuilder();
            if (left.get(0).getType() != ComboType.Belote)
                combs_str.append(left.get(0).getType().getNameProperty().getValue());
            for (int i = 1; i < left.size(); i++) {
                if (left.get(i).getType() != ComboType.Belote) {
                    combs_str.append(" + ");
                    combs_str.append(left.get(i).getType().getNameProperty().getValue());
                }
            }
            decs.put(RoomPosition.LEFT, combs_str.toString());
        }
        play_gui.reveal_decs(decs);
    }
}
