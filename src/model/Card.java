package model;

import java.util.ArrayList;
import java.util.List;

import game.Handler;
import gfx.Assets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import shared.RoomPosition;

public class Card extends StackPane {
    // LOGIC
    private final Suit suit;
    private final Rank rank;
    private RoomPosition position;
    private Handler handler;
    // GRAPHIC
    private ImageView card;

    public Card(RoomPosition position, Suit suit, Rank rank) {
        this.position = position;
        this.suit = suit;
        this.rank = rank;
    }

    public Card(Handler handler, int suitIndex, int rankIndex, RoomPosition position) {
        this.handler = handler;
        this.suit = Suit.get(suitIndex);
        this.rank = Rank.get(rankIndex);
        this.position = position;
        card = Assets.getCard(this);
        getChildren().add(card);
        setOnMouseClicked(e -> {
            if (!handler.getGame().isPlayable() || !handler.getGame().isYourTurn() || !isLegal())
                return;
            Card selected = handler.getGame().getSelectedCard();
            if (selected == null) {
                handler.getGame().setSelectedCard(this);
            } else if (selected == this) {
                handler.getGame().getBoard().play(this);
            } else {
                handler.getGame().setSelectedCard(null);
                handler.getGame().setSelectedCard(this);
            }
        });
    }

    private boolean isLegal() {
        Card demanded = handler.getGame().getBoard().getDemanded();
        if (demanded == null)
            return true;
        else {
            Suit trump = handler.getGame().getBoard().getLatestBid().getTrump();
            List<Card> cards = new ArrayList<>();

            switch (demanded.getSuit()) {
                case Hearts -> cards = handler.getGame().getBottomPane().getPlayer().getHand().getHearts();
                case Spades -> cards = handler.getGame().getBottomPane().getPlayer().getHand().getSpades();
                case Diamonds -> cards = handler.getGame().getBottomPane().getPlayer().getHand().getDiamonds();
                case Clubs -> cards = handler.getGame().getBottomPane().getPlayer().getHand().getClubs();
                case SA, TA -> {
                }
            }

            List<Card> trumpCards = null;
            switch (trump) {
                case Hearts -> trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getHearts();
                case Spades -> trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getSpades();
                case Diamonds -> trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getDiamonds();
                case Clubs -> trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getClubs();
                case SA, TA -> {
                }
            }

            Card maitre = handler.getGame().getBoard().getMaitre();
            if (!cards.isEmpty()) {
                if (demanded.getSuit() == trump || trump == Suit.TA) {
                    Card highestCard = cards.get(0);
                    for (Card card : cards)
                        if (card.getRank().getValue(true) > highestCard.getRank().getValue(true))
                            highestCard = card;
                    if (highestCard.getRank().getValue(true) < maitre.getRank().getValue(true)) {
                        return suit == demanded.getSuit();
                    } else {
                        return (suit == demanded.getSuit()) && (rank.getValue(true) > maitre.getRank().getValue(true)
                                || rank == Rank.Eight && maitre.getRank() == Rank.Seven);
                    }
                } else {
                    return suit == demanded.getSuit();
                }
            } else {
                if (trumpCards == null || trumpCards.isEmpty())
                    return true;
                if (maitre.getSuit() == trump && maitre.getPosition() != RoomPosition.TOP) {
                    Card highestTrumpCard = trumpCards.get(0);
                    for (Card card : trumpCards)
                        if (card.getRank().getValue(true) > highestTrumpCard.getRank().getValue(true))
                            highestTrumpCard = card;
                    if (highestTrumpCard.getRank().getValue(true) < maitre.getRank().getValue(true)) {
                        return suit == trump;
                    } else {
                        return suit == trump && (rank.getValue(true) > maitre.getRank().getValue(true)
                                || rank == Rank.Eight && maitre.getRank() == Rank.Seven);
                    }
                } else {
                    return suit == trump || maitre.getPosition() == RoomPosition.TOP;
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Card other = (Card) obj;
        return other.rank == rank && other.suit == suit;
    }

    // GETTERS SETTERS

    public int getValue(Suit trump) {
        if (trump == null)
            return rank.getValue(false); // trump is AT when counting points
        else
            return rank.getValue(trump == suit || trump == Suit.TA);
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public RoomPosition getPosition() {
        return position;
    }

    public void setPosition(RoomPosition position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return rank.toString() + " of " + suit.toString();
    }

    public Image getImage() {
        return card.getImage();
    }

    public void set_fold_height() {
        card.setFitHeight(50);
    }
}
