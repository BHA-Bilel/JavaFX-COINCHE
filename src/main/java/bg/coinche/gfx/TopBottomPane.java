package bg.coinche.gfx;

import bg.coinche.game.Handler;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import bg.coinche.model.*;
import shared.RoomPosition;

import java.util.ArrayList;
import java.util.List;

public class TopBottomPane extends HBox {

    private final Handler handler;
    private Player player;
    private List<Card> cards;
    private final Pane dealer;
    private final RoomPosition panePosition;
    private final Bid bid;

    public TopBottomPane(Handler handler, RoomPosition panePosition, String name_str) {
        cards = new ArrayList<>();
        this.panePosition = panePosition;
        this.handler = handler;
        setAlignment(Pos.CENTER);

        dealer = setup_dealer_chip();

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        Text name = new Text(name_str);
        name.setFont(Font.font(30));
        bid = new Bid(panePosition);
        HBox.setMargin(bid, new Insets(0, 10, 0, 10));
        getChildren().addAll(name, bid, dealer);
    }

    private HBox setup_dealer_chip() {
        Circle circle = new Circle(15);
        circle.setFill(Paint.valueOf("white"));
        circle.setStroke(Paint.valueOf("black"));
        Text dealer_txt = new Text("D");
        dealer_txt.setFont(Font.font(20));
        StackPane chip_sp = new StackPane();
        chip_sp.getChildren().addAll(circle, dealer_txt);
        HBox.setMargin(chip_sp, new Insets(0, 10, 0, 10));
        HBox dealer = new HBox(5);
        dealer.setAlignment(Pos.CENTER);
        dealer.getChildren().addAll(chip_sp);
        dealer.setOpacity(0);
        return dealer;
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
            if (panePosition == handler.getGame().getDealer()) {
                dealer.setOpacity(1);
            } else if (panePosition.next() == handler.getGame().getDealer()) {
                dealer.setOpacity(0);
            }
        });
    }

    public void removeCard(Card card) {
        cards.remove(card);
        player.removeCard(card);
        Platform.runLater(() -> getChildren().remove(card));
    }

    public void removeCards() {
        int size = cards.size();
        for (int i = 0; i < size; i++) {
            Card removedCard = cards.get(0);
            cards.remove(0);
            Platform.runLater(() -> getChildren().remove(removedCard));
        }
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
