package bg.coinche.gfx;

import bg.coinche.game.Handler;
import bg.coinche.model.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import shared.RoomPosition;

import java.util.ArrayList;
import java.util.List;

public class TopBottomPane extends HBox {

    private final Handler handler;
    private Player player;
    private List<Card> cards;
    private final ImageView dealer;
    private final RoomPosition panePosition;
    private final Bid bid;

    public TopBottomPane(Handler handler, RoomPosition panePosition) {
        cards = new ArrayList<>();
        this.panePosition = panePosition;
        this.handler = handler;
        setAlignment(Pos.CENTER);
        dealer = Assets.getDealerChip();
        dealer.setOpacity(0);
        bid = new Bid(panePosition);
        getChildren().addAll(bid, dealer);
    }

    public void bought(Suit anySuit, int bid, boolean capot) {
        this.bid.bought(anySuit, bid, capot);
    }

    public void LoadCards() {
        Platform.runLater(() -> {
            cards = player.getHand().getCards();
            getChildren().addAll(cards);
        });
    }

    public void updateDealer() {
        Platform.runLater(() -> {
            if (handler.getGame().getDealer().equals(panePosition)) dealer.setOpacity(1);
            else if (handler.getGame().getDealer().equals(panePosition.next())) dealer.setOpacity(0);
        });
    }

    public void removeCard(Card card) {
        cards.remove(card);
        player.removeCard(card);
        Platform.runLater(() -> getChildren().remove(card));
    }

    public void clearCards() {
        cards.clear();
        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().addAll(bid, dealer);
        });
    }

    public boolean checkForBelote() {
        List<Combination> declarations = player.getDeclarations();
        if (declarations == null)
            return false;
        for (Combination comb : declarations) {
            if (comb.getType() == ComboType.Belote)
                return true;
        }
        return false;
    }

    public void reset_bid_decs() {
        player.setDeclarations(null);
        bid.reset_bid();
    }

    public void hide_pass_bids() {
        bid.hide_unnecessary_bids(handler.getGame().getBoard().latestBid);
    }

    public void pass_turn() {
        bid.pass();
    }

    public void setCoinche(boolean coinche) {
        bid.setCoinche(coinche, handler.getGame().getBoard().latestBid);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Bid getBid() {
        return bid;
    }

}
