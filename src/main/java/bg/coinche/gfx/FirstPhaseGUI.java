package bg.coinche.gfx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSlider;
import bg.coinche.game.Handler;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import bg.coinche.model.Bid;
import bg.coinche.model.Suit;
import shared.RoomPosition;

public class FirstPhaseGUI extends GridPane {

    private final Handler handler;
    private final Board board;
    private JFXSlider slider, capotSlider;
    private JFXButton hearts, spades, diamonds, clubs, AT, NT, coincher, pass;
    private JFXCheckBox capot;
    private TurnTriangle current_turn;
    private TurnTriangle bottomTurn, rightTurn, topTurn, leftTurn;

    public FirstPhaseGUI(Handler handler, Board board) {
        this.handler = handler;
        this.board = board;
        init_grid();
        initButons();
        initSliders();
        initTurns();
    }

    private void init_grid() {
//        setGridLinesVisible(true);
//        setHgap(30);
//        setVgap(30);
//        setMaxWidth(750);
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

    private void initTurns() {
        bottomTurn = new TurnTriangle(RoomPosition.BOTTOM, false);
        rightTurn = new TurnTriangle(RoomPosition.RIGHT, false);
        topTurn = new TurnTriangle(RoomPosition.TOP, false);
        leftTurn = new TurnTriangle(RoomPosition.LEFT, false);

        bottomTurn.next = rightTurn;
        rightTurn.next = topTurn;
        topTurn.next = leftTurn;
        leftTurn.next = bottomTurn;

        add(bottomTurn, 1, 6, 5, 1);
        add(rightTurn, 6, 1, 1, 5);
        add(topTurn, 1, 0, 5, 1);
        add(leftTurn, 0, 1, 1, 5);
    }

    private void initButons() {
        hearts = new JFXButton();
        ImageView graphics = Assets.getSign(Suit.Hearts);
        hearts.setGraphic(graphics);
        hearts.setOnAction(e -> {
            if (handler.getGame().isYourTurn())
                board.buy(handler.getGame().getCurrentPosition(), Suit.Hearts,
                        (int) (capot.isSelected() ? capotSlider.getValue() : slider.getValue()),
                        capot.isSelected());
        });

        spades = new JFXButton();
        graphics = Assets.getSign(Suit.Spades);
        spades.setGraphic(graphics);
        spades.setOnAction(e -> {
            if (handler.getGame().isYourTurn())
                board.buy(handler.getGame().getCurrentPosition(), Suit.Spades,
                        (int) (capot.isSelected() ? capotSlider.getValue() : slider.getValue()),
                        capot.isSelected());
        });

        clubs = new JFXButton();
        graphics = Assets.getSign(Suit.Clubs);
        clubs.setGraphic(graphics);
        clubs.setOnAction(e -> {
            if (handler.getGame().isYourTurn())
                board.buy(handler.getGame().getCurrentPosition(), Suit.Clubs,
                        (int) (capot.isSelected() ? capotSlider.getValue() : slider.getValue()),
                        capot.isSelected());
        });

        diamonds = new JFXButton();
        graphics = Assets.getSign(Suit.Diamonds);
        diamonds.setGraphic(graphics);
        diamonds.setOnAction(e -> {
            if (handler.getGame().isYourTurn())
                board.buy(handler.getGame().getCurrentPosition(), Suit.Diamonds,
                        (int) (capot.isSelected() ? capotSlider.getValue() : slider.getValue()),
                        capot.isSelected());
        });

        NT = new JFXButton("No Trumps");
        NT.setOnAction(e -> {
            if (slider.getValue() < 130) {
                slider.setValue(130);
            }
            if (handler.getGame().isYourTurn())
                board.buy(handler.getGame().getCurrentPosition(), Suit.SA,
                        (int) (capot.isSelected() ? capotSlider.getValue() : slider.getValue()),
                        capot.isSelected());
        });

        AT = new JFXButton("All Trumps");
        AT.setOnAction(e -> {
            if (slider.getValue() < 130) {
                slider.setValue(130);
            }
            if (handler.getGame().isYourTurn())
                board.buy(handler.getGame().getCurrentPosition(), Suit.TA,
                        (int) (capot.isSelected() ? capotSlider.getValue() : slider.getValue()),
                        capot.isSelected());
        });

        coincher = new JFXButton("coincher!");
        coincher.setOnAction(e -> {
            if (board.latestBid == null || board.latestBid.youBought() && !board.latestBid.isCorS())
                return;
            if (!board.latestBid.isCorS()) {
                board.buy(RoomPosition.BOTTOM, board.latestBid.getTrump(), -2, capot.isSelected());
            } else if (handler.getGame().isYourTurn()) {
                board.buy(RoomPosition.BOTTOM, board.latestBid.getTrump(), -4, capot.isSelected());
            }
        });
        pass = new JFXButton("pass");
        pass.setOnAction(e -> {
            if (handler.getGame().isYourTurn()) {
                board.buy(handler.getGame().getCurrentPosition(), Suit.Hearts, 0, capot.isSelected());
            }
        });

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
    }

    public void initSliders() {
        slider = new JFXSlider(80, 500, 80);
        slider.setMinorTickCount(0);
        slider.setMajorTickUnit(10);
//        slider.setMinWidth(800);
        slider.autosize();
        slider.setSnapToTicks(true);
        capot = new JFXCheckBox("Capot");
        slider.visibleProperty().bind(capot.selectedProperty().not());
        capotSlider = new JFXSlider(0, 500, 0);
        capotSlider.visibleProperty().bind(capot.selectedProperty());
        capotSlider.setMinorTickCount(0);
        capotSlider.setMajorTickUnit(10);
//        capotSlider.setMinWidth(800);
        capotSlider.autosize();
        capotSlider.setSnapToTicks(true);

        GridPane.setHalignment(capot, HPos.CENTER);
        GridPane.setHgrow(slider, Priority.ALWAYS);
        GridPane.setHgrow(capotSlider, Priority.ALWAYS);
        add(slider, 1, 3, 5, 1);
        add(capot, 3, 4);
        add(capotSlider, 1, 5, 5, 1);
    }

    public void adapt_turn() {
        while (current_turn.position != handler.getGame().getCurrentPosition())
            current_turn = current_turn.next;
        TurnTriangle.adapt_turn(current_turn);
    }

    private void enable_buttons() {
        coincher.setDisable(false);
        coincher.setText("coincher!");
        pass.setDisable(false);
        hearts.setDisable(false);
        spades.setDisable(false);
        diamonds.setDisable(false);
        clubs.setDisable(false);
        AT.setDisable(false);
        NT.setDisable(false);
    }

    private void reset_sliders() {
        slider.setDisable(false);
        capot.setDisable(false);
        capotSlider.setDisable(false);
        slider.setMin(80);
        slider.setValue(80);
        capot.setSelected(false);
        capotSlider.setMin(0);
        capotSlider.setValue(0);
    }

    public void reset_gui() {
        adapt_turn();
        enable_buttons();
        reset_sliders();
    }

    public void adapt_sliders(boolean capot) {
        if (capot) {
            this.capot.setSelected(true);
            this.capot.setDisable(true);
            slider.setDisable(true);
            capotSlider.setDisable(false);
        }
    }

    public void coinche(RoomPosition coinchedPosition) {
        if (coinchedPosition == RoomPosition.TOP || coinchedPosition == RoomPosition.BOTTOM) {
            coincher.setDisable(true);
            pass.setDisable(true);
        } else {
            Platform.runLater(() -> coincher.setText("surcoincher!"));
        }
        hearts.setDisable(true);
        spades.setDisable(true);
        diamonds.setDisable(true);
        clubs.setDisable(true);
        AT.setDisable(true);
        NT.setDisable(true);
    }

    public void update_value(Bid latestBid) {
        Platform.runLater(() -> {
            int value = latestBid.getValue() + 10;
            if (latestBid.isCapot()) {
                if (board.first_capot_bid == 1 || latestBid.getValue() > 0) {
                    capotSlider.setValue(value);
                    capotSlider.setMin(value);
                    if (board.first_capot_bid == 1) {
                        board.first_capot_bid = 2;
                    }
                }
            } else if (latestBid.getValue() > 0) {
                slider.setValue(value);
                slider.setMin(value);
            }
        });
    }

    public void setup_first_turn() {
        switch (handler.getGame().getCurrentPosition()) {
            case BOTTOM: {
                current_turn = bottomTurn;
                break;
            }
            case RIGHT: {
                current_turn = rightTurn;
                break;
            }
            case TOP: {
                current_turn = topTurn;
                break;
            }
            case LEFT: {
                current_turn = leftTurn;
                break;
            }
        }
        current_turn.init_turn();
    }

    public void switchTurn() {
        current_turn.switch_turn();
        current_turn = current_turn.next;
    }
}
