package bg.coinche.gfx;

import bg.coinche.game.Handler;
import bg.coinche.model.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import shared.RoomPosition;

import java.util.List;

public class LeftRightPane extends VBox {

    private final Handler handler;
    private Player player;
    private final ImageView dealer;
    private final RoomPosition panePosition;
    private final Bid bid;

    public LeftRightPane(Handler handler, RoomPosition panePosition) {
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

    public void updateDealer() {
        Platform.runLater(() -> {
            if (handler.getGame().getDealer().equals(panePosition)) dealer.setOpacity(1);
            else if (handler.getGame().getDealer().equals(panePosition.next())) dealer.setOpacity(0);
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

    public void setCoinche(boolean coincheOnly) {
        bid.setCoinche(coincheOnly, handler.getGame().getBoard().latestBid);
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
