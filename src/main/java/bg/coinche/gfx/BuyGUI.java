package bg.coinche.gfx;

import bg.coinche.game.Handler;
import bg.coinche.lang.Language;
import bg.coinche.model.Bid;
import bg.coinche.model.Suit;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import shared.RoomPosition;

public class BuyGUI extends GameGUI {

    private final JFXButton hearts, spades, diamonds, clubs, AT, NT, coincher, pass;
    private final BuyValue buy_value;

    public BuyGUI(Handler handler, Board board) {
        super(handler, board);
        hearts = new JFXButton();
        spades = new JFXButton();
        clubs = new JFXButton();
        diamonds = new JFXButton();
        NT = new JFXButton();
        AT = new JFXButton();
        coincher = new JFXButton();
        pass = new JFXButton();
        buy_value = new BuyValue();
        setup_buttons();
        add_gui_elements();
    }

    @Override
    void init_grid() {
        double percentage = 100.0 / 7.0;
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(percentage);
            getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(percentage);
            getRowConstraints().add(row);
        }
    }

    private void setup_buttons() {
        ImageView graphics = Assets.getButtonSign(Suit.Hearts);
        hearts.setGraphic(graphics);
        hearts.setOnAction(e -> {
            if (!handler.getGame().isYourTurn()) return;
            Object[] value = buy_value.get_value();
            board.buy(handler.getGame().getCurrentPosition(), Suit.Hearts,
                    (int) (value[0]), (boolean) value[1]);
        });

        graphics = Assets.getButtonSign(Suit.Spades);
        spades.setGraphic(graphics);
        spades.setOnAction(e -> {
            if (!handler.getGame().isYourTurn()) return;
            Object[] value = buy_value.get_value();
            board.buy(handler.getGame().getCurrentPosition(), Suit.Spades,
                    (int) (value[0]), (boolean) value[1]);
        });

        graphics = Assets.getButtonSign(Suit.Clubs);
        clubs.setGraphic(graphics);
        clubs.setOnAction(e -> {
            if (!handler.getGame().isYourTurn()) return;
            Object[] value = buy_value.get_value();
            board.buy(handler.getGame().getCurrentPosition(), Suit.Clubs,
                    (int) (value[0]), (boolean) value[1]);
        });

        graphics = Assets.getButtonSign(Suit.Diamonds);
        diamonds.setGraphic(graphics);
        diamonds.setOnAction(e -> {
            if (!handler.getGame().isYourTurn()) return;
            Object[] value = buy_value.get_value();
            board.buy(handler.getGame().getCurrentPosition(), Suit.Diamonds,
                    (int) (value[0]), (boolean) value[1]);
        });

        NT.textProperty().bind(Language.NT);
        NT.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        NT.setOnAction(e -> {
            buy_value.assert_min_atnt();
            if (!handler.getGame().isYourTurn()) return;
            Object[] value = buy_value.get_value();
            board.buy(handler.getGame().getCurrentPosition(), Suit.SA,
                    (int) (value[0]), (boolean) value[1]);
        });

        AT.textProperty().bind(Language.AT);
        AT.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        AT.setOnAction(e -> {
            buy_value.assert_min_atnt();
            if (!handler.getGame().isYourTurn()) return;
            Object[] value = buy_value.get_value();
            board.buy(handler.getGame().getCurrentPosition(), Suit.TA,
                    (int) (value[0]), (boolean) value[1]);
        });

        coincher.textProperty().bind(Language.COINCHER);
        coincher.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        coincher.setOnAction(e -> {
            if (board.latestBid == null
                    || board.latestBid.youBought() && !board.latestBid.isCorS()) return;
            if (!board.latestBid.isCorS()) {
                handler.getGame().coinched();
                board.coinche(RoomPosition.BOTTOM);
            } else if (handler.getGame().isYourTurn()) board.surcoinche();
        });

        pass.textProperty().bind(Language.PASS);
        pass.setMinSize(JFXButton.USE_PREF_SIZE, JFXButton.USE_PREF_SIZE);
        pass.setOnAction(e -> {
            if (!handler.getGame().isYourTurn()) return;
            board.pass();
        });
    }

    @Override
    void add_gui_elements() {
        GridPane.setHalignment(hearts, HPos.CENTER);
        GridPane.setHalignment(clubs, HPos.CENTER);
        GridPane.setHalignment(NT, HPos.CENTER);
        GridPane.setHalignment(coincher, HPos.CENTER);
        GridPane.setHalignment(spades, HPos.CENTER);
        GridPane.setHalignment(diamonds, HPos.CENTER);
        GridPane.setHalignment(AT, HPos.CENTER);
        GridPane.setHalignment(pass, HPos.CENTER);

        add(hearts, 1, 1);
        add(clubs, 2, 1);
        add(NT, 4, 1);
        add(coincher, 5, 1);
        add(spades, 1, 2);
        add(diamonds, 2, 2);
        add(AT, 4, 2);
        add(pass, 5, 2);
        add(buy_value, 1, 3, 5, 3);
    }

    @Override
    void initTurns() {
        super.initTurns();
        add(bottomTurn, 1, 6, 5, 1);
        add(rightTurn, 6, 1, 1, 5);
        add(topTurn, 1, 0, 5, 1);
        add(leftTurn, 0, 1, 1, 5);
    }

    private void enable_buttons() {
        coincher.setDisable(false);
        coincher.textProperty().bind(Language.COINCHER);
        pass.setDisable(false);
        hearts.setDisable(false);
        spades.setDisable(false);
        diamonds.setDisable(false);
        clubs.setDisable(false);
        AT.setDisable(false);
        NT.setDisable(false);
    }

    public void reset_gui() {
        enable_buttons();
        buy_value.reset();
    }

    public void disable_buttons(boolean all_buttons) {
        if (all_buttons) {
            coincher.setDisable(true);
            pass.setDisable(true);
        } else Platform.runLater(() -> coincher.textProperty().bind(Language.SURCOINCHER));
        disable_buttons();
    }

    private void disable_buttons() {
        hearts.setDisable(true);
        spades.setDisable(true);
        diamonds.setDisable(true);
        clubs.setDisable(true);
        AT.setDisable(true);
        NT.setDisable(true);
    }

    public boolean update_value(Bid latestBid) {
        int value = latestBid.getValue() + 10;
        Platform.runLater(() -> {
            if (latestBid.isCapot()) {
                if (board.first_capot_bid == 1 || latestBid.getValue() > 0) {
                    if (board.first_capot_bid == 1) board.first_capot_bid = 2;
                    buy_value.update(value, true);
                }
            } else if (latestBid.getValue() > 0) {
                buy_value.update(value, false);
            }
        });
        return latestBid.isCapot() && buy_value.reached_max(value);
    }
}
