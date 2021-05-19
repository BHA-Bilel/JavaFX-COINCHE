package bg.coinche.model;

import shared.RoomPosition;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final RoomPosition position;
    private Hand hand;
    private ArrayList<Combination> declarations;

    public Player(RoomPosition position) {
        this.position = position;
    }

    public void setCards(ArrayList<Card> playerCards) {
        hand = new Hand(playerCards);
    }

    public void removeCard(Card card) {
        hand.remove(card);
    }

    public Hand getHand() {
        return hand;
    }

    public List<Combination> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(ArrayList<Combination> combos) {
        this.declarations = combos;
    }

    public RoomPosition getPosition() {
        return position;
    }
}
