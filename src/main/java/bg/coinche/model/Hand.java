package bg.coinche.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hand {

    private final ArrayList<Card> cards;

    public Hand(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getSpades() {
        List<Card> spades = new ArrayList<>();
        for (Card card : cards) {
            if (card.getSuit() == Suit.Spades) {
                spades.add(card);
            }
        }
        for (int i = 0; i < spades.size(); i++) {
            int index = i;
            for (int j = i + 1; j < spades.size(); j++) {
                if (spades.get(j).getRank().getIndex() < spades.get(index).getRank().getIndex())
                    index = j;
            }
            if (index != i) {
                Card other = spades.set(i, spades.get(index));
                spades.set(index, other);
            }
        }
        return spades;
    }

    public List<Card> getHearts() {
        List<Card> hearts = new ArrayList<>();
        for (Card card : cards) {
            if (card.getSuit() == Suit.Hearts) {
                hearts.add(card);
            }
        }
        for (int i = 0; i < hearts.size(); i++) {
            int index = i;
            for (int j = i + 1; j < hearts.size(); j++) {
                if (hearts.get(j).getRank().getIndex() < hearts.get(index).getRank().getIndex())
                    index = j;
            }
            if (index != i) {
                Card other = hearts.set(i, hearts.get(index));
                hearts.set(index, other);
            }
        }
        return hearts;
    }

    public List<Card> getClubs() {
        List<Card> clubs = new ArrayList<>();
        for (Card card : cards) {
            if (card.getSuit() == Suit.Clubs) {
                clubs.add(card);
            }
        }
        for (int i = 0; i < clubs.size(); i++) {
            int index = i;
            for (int j = i + 1; j < clubs.size(); j++) {
                if (clubs.get(j).getRank().getIndex() < clubs.get(index).getRank().getIndex())
                    index = j;
            }
            if (index != i) {
                Card other = clubs.set(i, clubs.get(index));
                clubs.set(index, other);
            }
        }
        return clubs;
    }

    public List<Card> getDiamonds() {
        List<Card> diamonds = new ArrayList<>();
        for (Card card : cards) {
            if (card.getSuit() == Suit.Diamonds) {
                diamonds.add(card);
            }
        }
        for (int i = 0; i < diamonds.size(); i++) {
            int index = i;
            for (int j = i + 1; j < diamonds.size(); j++) {
                if (diamonds.get(j).getRank().getIndex() < diamonds.get(index).getRank().getIndex())
                    index = j;
            }
            if (index != i) {
                Card other = diamonds.set(i, diamonds.get(index));
                diamonds.set(index, other);
            }
        }
        return diamonds;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void remove(Card card) {
        cards.remove(card);
    }

    @Override
    public String toString() {
        return Arrays.toString(cards.toArray());
    }

}
