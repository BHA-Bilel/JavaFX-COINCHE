package bg.coinche.model;

import java.util.ArrayList;
import java.util.List;

import bg.coinche.MainApp;
import bg.coinche.game.GameApp;
import bg.coinche.game.Handler;
import bg.coinche.gfx.Assets;
import javafx.beans.property.DoubleProperty;
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

    // server
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    // declaration
    public Card(RoomPosition position, Suit suit, Rank rank) {
        this.position = position;
        this.suit = suit;
        this.rank = rank;
    }

    // player, play
    public Card(Handler handler, DoubleProperty card_bind, int suit, int rank, RoomPosition position) {
        this.handler = handler;
        this.suit = Suit.values()[suit];
        this.rank = Rank.values()[rank];
        this.position = position;
        card = Assets.getCard(this);
        card.fitWidthProperty().bind(card_bind);
        getChildren().add(card);
        setOnMouseClicked(e -> {
            GameApp game = handler.getGame();
            if (!game.isPlayable() || !game.isYourTurn() || !isLegal())
                return;
            Card selected = game.getSelectedCard();
            if (selected == null) {
                game.setSelectedCard(this);
            } else if (selected == this) {
                game.getBoard().play(this);
            } else {
                game.setSelectedCard(null);
                game.setSelectedCard(this);
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
                case Hearts: {
                    cards = handler.getGame().getBottomPane().getPlayer().getHand().getHearts();
                    break;
                }
                case Spades: {
                    cards = handler.getGame().getBottomPane().getPlayer().getHand().getSpades();
                    break;
                }
                case Diamonds: {
                    cards = handler.getGame().getBottomPane().getPlayer().getHand().getDiamonds();
                    break;
                }
                case Clubs: {
                    cards = handler.getGame().getBottomPane().getPlayer().getHand().getClubs();
                    break;
                }
                default: {
                }
            }

            List<Card> trumpCards = null;
            switch (trump) {
                case Hearts: {
                    trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getHearts();
                    break;
                }
                case Spades: {
                    trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getSpades();
                    break;
                }
                case Diamonds: {
                    trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getDiamonds();
                    break;
                }
                case Clubs: {
                    trumpCards = handler.getGame().getBottomPane().getPlayer().getHand().getClubs();
                    break;
                }
                default: {
                }
            }

            Card maitre = handler.getGame().getBoard().getMaitre();
            if (!cards.isEmpty()) {
                if (demanded.getSuit() == trump || trump == Suit.TA) {
                    Card highestCard = cards.get(0);
                    for (Card card : cards)
                        if (card.getRank().getValue(true) > highestCard.getRank().getValue(true))
                            highestCard = card;
                    if (highestCard.getRank().getValue(true) <= maitre.getRank().getValue(true)) {
                        return suit == demanded.getSuit();
                    } else {
                        return (suit == demanded.getSuit()) &&
                                (rank.getValue(true) > maitre.getRank().getValue(true)
                                        || rank == Rank.Eight && maitre.getRank() == Rank.Seven);
                    }
                } else {
                    return suit == demanded.getSuit();
                }
            } else {
                if (trumpCards == null || trumpCards.isEmpty()) return true;
                if (maitre.getSuit() == trump && maitre.getPosition() != RoomPosition.TOP) {
                    Card highestTrumpCard = trumpCards.get(0);
                    for (Card card : trumpCards)
                        if (card.getRank().getValue(true) > highestTrumpCard.getRank().getValue(true))
                            highestTrumpCard = card;
                    if (highestTrumpCard.getRank().getValue(true) <= maitre.getRank().getValue(true)) {
                        return suit == trump;
                    } else {
                        return suit == trump &&
                                (rank.getValue(true) > maitre.getRank().getValue(true)
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
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        Card other = (Card) obj;
        return other.rank == rank && other.suit == suit;
    }

    /**
     * for server to send new game dominoes in adt_data
     */
    public static Integer[] to_array(Hand hand) {
        Integer[] array = new Integer[hand.getCards().size() * 2];
        int i = 0;
        for (Card card : hand.getCards()) {
            array[i++] = card.getSuit().ordinal();
            array[i++] = card.getRank().ordinal();
        }
        return array;
    }

    /**
     * for clients to get new game dominoes from game start adt_data
     */
    public static ArrayList<Card> to_list(Handler handler, Integer[] array) {
        ArrayList<Card> list = new ArrayList<>();
        int i = 0;
        while (i < array.length) list.add(new Card(handler, MainApp.playerCardsProperty,
                array[i++], array[i++], RoomPosition.BOTTOM));
        return list;
    }

    public int getValue(Suit trump) {
        if (trump == null) return rank.getValue(false); // trump is AT when counting points
        else return rank.getValue(trump == suit || trump == Suit.TA);
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

    public Image getImage() {
        return card.getImage();
    }
}
