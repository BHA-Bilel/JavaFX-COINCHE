package gfx;

import game.Handler;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.*;
import shared.RoomPosition;

import java.util.List;

public class LeftRightPane extends VBox {

    private final Handler handler;
    private Player player;
    private final Pane dealer;
    private final RoomPosition panePosition;
    private final Bid bid;

    public LeftRightPane(Handler handler, RoomPosition panePosition, String name_str) {
        this.panePosition = panePosition;
        this.handler = handler;
        setAlignment(Pos.CENTER);

        dealer = setup_dealer_chip();

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Text name = new Text(name_str);
        name.setFont(Font.font(30));
        bid = new Bid(panePosition);
        VBox.setMargin(bid, new Insets(10, 0, 10, 0));
        getChildren().addAll(name, bid, dealer);
    }

    private VBox setup_dealer_chip() {
        Circle circle = new Circle(15);
        circle.setFill(Paint.valueOf("white"));
        circle.setStroke(Paint.valueOf("black"));
        Text dealer_txt = new Text("D");
        dealer_txt.setFont(Font.font(20));
        StackPane chip_sp = new StackPane();
        chip_sp.getChildren().addAll(circle, dealer_txt);
        VBox.setMargin(chip_sp, new Insets(10, 0, 10, 0));
        VBox dealer = new VBox(5);
        dealer.setAlignment(Pos.CENTER);
        dealer.getChildren().addAll(chip_sp);
        dealer.setOpacity(0);
        return dealer;
    }

    public void bought(Suit anySuit, int bid, boolean capot) {
        this.bid.bought(anySuit, bid, capot);
    }

    public void updateDealer() {
        Platform.runLater(() -> {
            if (panePosition == handler.getGame().getDealer()) {
                dealer.setOpacity(1);
            } else if (panePosition.next() == handler.getGame().getDealer()) {
                dealer.setOpacity(0);
            }
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
