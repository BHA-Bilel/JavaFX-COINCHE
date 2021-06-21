package bg.coinche.model;

import bg.coinche.MainApp;
import bg.coinche.gfx.Assets;
import bg.coinche.lang.Language;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import shared.RoomPosition;

public class Bid extends VBox {

    private Suit trump;
    private int value;
    private int coinche = 1;
    private boolean capot;
    private final RoomPosition position;
    private final ImageView sign, Cchip;
    private final Label text;

    public Bid(RoomPosition position) {
        this.position = position;
        text = new Label();
        text.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        sign = new ImageView();
        sign.setPreserveRatio(true);
        sign.fitWidthProperty().bind(MainApp.turnSignProperty);
        Cchip = new ImageView();
        Cchip.setPreserveRatio(true);
        Cchip.fitWidthProperty().bind(MainApp.dealerCchipProperty);
        setAlignment(Pos.TOP_LEFT);
        spacingProperty().bind(MainApp.spacingProperty.multiply(.001));
        styleProperty().bind(Bindings.concat("-fx-padding: ", MainApp.paddingProperty.asString()));
        Platform.runLater(() -> getChildren().addAll(text, sign, Cchip));
    }

    public void bought(Suit trump, int value, boolean capot) {
        this.trump = trump;
        this.value = value;
        this.capot = capot;
        Platform.runLater(() -> {
            text.setText(toString());
            if ((capot || value > 0) && trump.ordinal() < 4) {
                sign.setImage(Assets.getSign(trump));
            } else {
                sign.setImage(null);
            }
        });
    }

    public void setCoinche(boolean coincheOnly, Bid latestBid) {
        if (coincheOnly) coinche();
        else surcoinche();
        Platform.runLater(() -> {
            Cchip.setImage(Assets.getCoincheChip(coincheOnly));
            if (!latestBid.equals(this)) {
                text.setText("");
                sign.setImage(null);
            }
        });
    }

    public void pass() {
        Platform.runLater(() -> {
            text.setText(Language.PASS.getValue());
            sign.setImage(null);
        });
    }

    public void hide_unnecessary_bids(Bid latestBid) {
        boolean keep = this.equals(latestBid) || isCorS();
        if (!keep) Platform.runLater(() -> setOpacity(0));
    }

    public StringProperty pts_to_fail_contract(int dec) {
        if (capot) {
            if (value > 0)
                return Language.PTS_CONT_FAIL_C2;
            else
                return Language.PTS_CONT_FAIL_C1;
        } else {
            boolean sa_ta = trump == Suit.TA || trump == Suit.SA;
            int fail = (sa_ta ? 260 + 2 : 162 + 1) - value + dec;
            StringProperty INT_FAIL = new SimpleStringProperty();
            INT_FAIL.set(Integer.toString(fail));
            return fail < 0 ? Language.CONTRACT_OUT_OF_RANGE :
                    fail > (sa_ta ? 260 + 2 : 162 + 1) ? Language.CAPOT_MORE_PTS : INT_FAIL;
        }
    }

    public int result(boolean won, boolean took_all_hands, int total_pts, int declarations) {
        int mode = total_pts % 10;
        total_pts += -mode + (mode > 4 ? 10 : 0);
        boolean sa_ta = trump == Suit.SA || trump == Suit.TA;
        int capot_value = 250;
        if (sa_ta) {
            capot_value = 260;
        }
        if (coinche > 1) {
            return (capot ? capot_value + value : value) * coinche + declarations
                    + (sa_ta ? 260 : 160)
                    + (took_all_hands ? total_pts : 0);
        } else {
            if (won) {
                return (capot ? capot_value + value : value) * coinche + declarations + total_pts;
            } else {
                return (capot ? (capot_value + value) : value) * coinche + declarations
                        + (sa_ta ? 260 : 160) + (took_all_hands ? total_pts : 0);
            }
        }
    }

    public void reset_bid() {
        trump = null;
        value = 0;
        coinche = 1;
        capot = false;
        Platform.runLater(() -> {
            text.setText("");
            sign.setImage(null);
            Cchip.setImage(null);
            setOpacity(1);
        });
    }

    public void coinche() {
        coinche = 2;
    }

    public void surcoinche() {
        coinche = 4;
    }

    public boolean isCorS() { // isCoinchedOrSurcoinched
        return coinche > 1;
    }

    public boolean youBought() {
        return position == RoomPosition.BOTTOM || position == RoomPosition.TOP;
    }

    public String one_line_str() {
        String ret = toString();
        return ret.replaceAll("\n", " ");
    }

    public int fullValue() {
        boolean sa_ta = trump == Suit.TA || trump == Suit.SA;
        return (capot ? (sa_ta ? 260 : 250) : 0) + value;
    }

    public Suit getTrump() {
        return trump;
    }

    public int getValue() {
        return value;
    }

    public int getCoinche() {
        return coinche;
    }

    public boolean isCapot() {
        return capot;
    }

    public RoomPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        String ret;
        if (value == 0 && !capot)
            return Language.PASS.getValue();
        else {
            if (capot) {
                ret = Language.CAPOT.getValue() + (value > 0 ? " + " + value : "");
            } else {
                ret = value + "";
            }
        }
        if (trump.ordinal() > 3)
            switch (position) {
                case TOP:
                case BOTTOM: {
                    ret += " " + trump;
                    break;
                }
                case LEFT:
                case RIGHT: {
                    ret += "\n" + trump;
                    break;
                }
            }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Bid bid = (Bid) obj;
        return trump == bid.trump && value == bid.value && capot == bid.capot;
    }

}
