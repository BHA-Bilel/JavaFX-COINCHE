package model;

public enum ComboType {
    JackCarre(200, 6), NineCarre(150, 5), Carre(100, 4), Suite(100, 3), Annonce(50, 2), Tierce(20, 1), Belote(20, 0);

    private final int order;
    private final int value;

    ComboType(int value, int order) {
        this.value = value;
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public int getValue() {
        return value;
    }

    public static ComboType get(int order) {
        for (ComboType name : ComboType.values()) {
            if (name.getOrder() == order) {
                return name;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return switch (this) {
            case JackCarre -> "Carr\u00E9 Valets";
            case NineCarre -> "Carr\u00E9 Neufs";
            case Carre -> "Carr\u00E9";
            case Suite -> "Suite";
            case Annonce -> "Annonce";
            case Tierce -> "Tierce";
            case Belote -> "Belote";
        };
    }

}
