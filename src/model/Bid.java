package model;

import gfx.Assets;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import shared.RoomPosition;

public class Bid extends VBox {

    private Suit trump;
    private int value;
    private int coinche = 1;
    private boolean capot;
    private final RoomPosition position;
    private final ImageView sign;
    private final Text text;

    public Bid(RoomPosition position) {
        this.position = position;
        text = new Text();
        text.setFont(Font.font(30));
        sign = new ImageView();
        sign.setPreserveRatio(true);
        sign.setFitWidth(30);
        setAlignment(Pos.TOP_LEFT);
        Platform.runLater(() -> getChildren().addAll(text, sign));
    }

    public void bought(Suit trump, int value, boolean capot) {
        this.trump = trump;
        this.value = value;
        this.capot = capot;
        Platform.runLater(() -> {
            text.setText(toString());
            if ((capot || value > 0) && trump.getIndex() < 4) {
                sign.setImage(Assets.getSign(trump).getImage());
            } else {
                sign.setImage(null);
            }
        });
    }

    public void setCoinche(boolean coincheOnly, Bid latestBid) {
        Platform.runLater(() -> {
            if (latestBid.equals(this)) {
                switch (position) {
                    case TOP -> text.setText(text.getText() + " Surcoinche! ");
                    case LEFT, RIGHT, BOTTOM -> text.setText("Surcoinche!\n" + text.getText());
                }
            } else {
                text.setText(coincheOnly ? "Coinche!" : "Surcoinche!");
                if (sign != null) {
                    sign.setImage(null);
                }
            }
        });
    }

    public void pass() {
        Platform.runLater(() -> {
            text.setText("Pass");
            sign.setImage(null);
        });
    }

    public void hide_unnecessary_bids(Bid latestBid) {
        boolean keep = this.equals(latestBid) || text.getText().contains("Coinche !") || text.getText().contains("Surcoinche !");
        if (!keep) {
            Platform.runLater(() -> setOpacity(0));
        }
    }

    public String pts_to_fail_contract(int dec) {
        if (capot)
            return "One fold" + (value > 0 ? ", or a higher declaration" : "");
        else {
            boolean sa_ta = trump == Suit.TA || trump == Suit.SA;
            int fail = (sa_ta ? 260 + 2 : 162 + 1) - value + dec;
            return fail < 0 ? "Contract out of range, You win !" :
                    fail > (sa_ta ? 260 + 2 : 162 + 1) ? "Capot or more total points" : Integer.toString(fail);
        }
    }

    public int result(boolean won, int total_pts, int declarations) {
        int mode = total_pts % 10;
        total_pts += -mode + (mode > 4 ? 10 : 0);
        boolean sa_ta = trump == Suit.SA || trump == Suit.TA;
        int capot_value = 250;
        if (sa_ta) {
            capot_value = 260;
        }
        if (won) {
            return (capot ? (capot_value + value) : value) * coinche + declarations
                    + (coinche > 1 ? (sa_ta ? 260 : 160) : 0) + total_pts;
        } else {
            return (capot ? (capot_value + value) : value) * coinche + declarations
                    + (sa_ta ? 260 : 160);
        }
    }

    public void reset_bid() {
        Platform.runLater(() -> {
            text.setText("");
            sign.setImage(null);
            setOpacity(1);
        });
    }

    public void coinche() {
        coinche = 2;
    }

    public void surcoinche() {
        coinche = 4;
    }

    public boolean isCoinchedOrSurcoinched() {
        return coinche > 1;
    }

    public boolean youBought() {
        return position == RoomPosition.BOTTOM || position == RoomPosition.TOP;
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

    public String one_line_str() {
        String ret = toString();
        return ret.replaceAll("\n", " ");
    }

    @Override
    public String toString() {
        String ret;
        if (value == 0 && !capot)
            return "Pass";
        else {
            if (capot) {
                ret = "Capot" + (value > 0 ? " + " + value : "");
            } else {
                ret = value + "";
            }
        }
        if (trump.getIndex() > 3)
            switch (position) {
                case TOP, BOTTOM -> ret += " " + trump;
                case LEFT, RIGHT -> ret += "\n" + trump;
            }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Bid bid = (Bid) obj;
        return trump == bid.trump && value == bid.value && capot == bid.capot;
    }

}
