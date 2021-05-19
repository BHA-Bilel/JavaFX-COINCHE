package bg.coinche.model;

import shared.RoomPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Combination {

    private final ComboType type;
    private final Rank rank;
    private final List<Card> cards;
    private final RoomPosition position;

    public Combination(RoomPosition position, int suit, int rank, int order) {
        this.position = position;
        this.type = ComboType.get(order);
        this.rank = Rank.get(rank);
        this.cards = new ArrayList<>();
        if (type == ComboType.JackCarre) { // Carre Jack
            cards.add(new Card(position, Suit.Hearts, Rank.Jack));
            cards.add(new Card(position, Suit.Spades, Rank.Jack));
            cards.add(new Card(position, Suit.Diamonds, Rank.Jack));
            cards.add(new Card(position, Suit.Clubs, Rank.Jack));
        } else if (type == ComboType.NineCarre) { // Carre Nine
            cards.add(new Card(position, Suit.Hearts, Rank.Nine));
            cards.add(new Card(position, Suit.Spades, Rank.Nine));
            cards.add(new Card(position, Suit.Diamonds, Rank.Nine));
            cards.add(new Card(position, Suit.Clubs, Rank.Nine));
        } else if (type == ComboType.Carre) { // Carre
            cards.add(new Card(position, Suit.Hearts, Rank.get(rank)));
            cards.add(new Card(position, Suit.Spades, Rank.get(rank)));
            cards.add(new Card(position, Suit.Diamonds, Rank.get(rank)));
            cards.add(new Card(position, Suit.Clubs, Rank.get(rank)));
        } else if (type != ComboType.Belote) { // Tierce, Annone, Suite
            cards.add(new Card(position, Suit.get(suit), Rank.get(rank)));
            cards.add(new Card(position, Suit.get(suit), Rank.get(rank - 1)));
            cards.add(new Card(position, Suit.get(suit), Rank.get(rank - 2)));
            if (type != ComboType.Tierce) {
                cards.add(new Card(position, Suit.get(suit), Rank.get(rank - 3)));
                if (type == ComboType.Suite)
                    cards.add(new Card(position, Suit.get(suit), Rank.get(rank - 4)));
            }
        } else { // Belote
            cards.add(new Card(position, Suit.get(suit), Rank.Queen));
            cards.add(new Card(position, Suit.get(suit), Rank.King));
        }
    }

    public ComboType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Position :" + position + " type : " + type + " rank : " + rank + "\ncards : "
                + Arrays.toString(cards.toArray());
    }

    public List<Card> getCards() {
        return cards;
    }

    public RoomPosition getPosition() {
        return position;
    }

}
