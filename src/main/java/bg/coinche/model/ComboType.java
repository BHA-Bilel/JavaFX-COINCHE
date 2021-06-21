package bg.coinche.model;

import bg.coinche.lang.Language;
import javafx.beans.property.StringProperty;

public enum ComboType {
    JackCarre(200, Language.JACK_CARRE), NineCarre(150, Language.NINE_CARRE),
    Carre(100, Language.CARRE), Suite(100, Language.SUITE),
    Annonce(50, Language.ANNONE), Tierce(20, Language.TIERCE),
    Belote(20, Language.BELOTE);

    private final int value;
    private final StringProperty name;

    ComboType(int value, StringProperty name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public StringProperty getNameProperty() {
        return name;
    }

}
