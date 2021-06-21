package bg.coinche.model;

import bg.coinche.lang.Language;
import javafx.beans.property.StringProperty;

public enum Suit {

    Hearts(Language.HEARTS), Spades(Language.SPADES), Diamonds(Language.DIAMONDS), Clubs(Language.CLUBS),
    SA(Language.NT), TA(Language.AT);

    private final StringProperty name;

    Suit(StringProperty name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.getValue();
    }
}
