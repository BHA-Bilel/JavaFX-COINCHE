package bg.coinche.gfx;

import bg.coinche.MainApp;
import bg.coinche.game.Handler;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import bg.coinche.model.Card;
import bg.coinche.model.Combination;
import bg.coinche.model.MyTimeLine;
import shared.RoomPosition;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class PlayGUI extends GameGUI {

    // Synchronization
    private final Semaphore showDec_order = new Semaphore(1);
    // LOGIC
    private boolean prevShown = false;

    private final TurnTriangle prevBTurn, prevRTurn, prevTTurn, prevLTurn;
    private TurnTriangle starting_pos;

    // SAY
    private final Label tsay, bsay, lsay, rsay;
    private final Label prevTsay, prevBsay, prevLsay, prevRsay;

    // CARDS
    private final ImageView tcard, bcard, lcard, rcard;
    private final ImageView prevTcard, prevBcard, prevLcard, prevRcard;

    public PlayGUI(Handler handler, Board board) {
        super(handler, board);
        tcard = new ImageView();
        bcard = new ImageView();
        lcard = new ImageView();
        rcard = new ImageView();
        prevTcard = new ImageView();
        prevBcard = new ImageView();
        prevLcard = new ImageView();
        prevRcard = new ImageView();
        prevBTurn = new TurnTriangle(RoomPosition.BOTTOM, true);
        prevRTurn = new TurnTriangle(RoomPosition.RIGHT, true);
        prevTTurn = new TurnTriangle(RoomPosition.TOP, true);
        prevLTurn = new TurnTriangle(RoomPosition.LEFT, true);
        tsay = new Label();
        bsay = new Label();
        lsay = new Label();
        rsay = new Label();
        prevTsay = new Label();
        prevBsay = new Label();
        prevLsay = new Label();
        prevRsay = new Label();
        setup_cards();
        setup_prev_cards();
        setup_prevTurns();
        setup_Say();
        setup_prevSay();
        add_gui_elements();
    }

    @Override
    void init_grid() {
        setAlignment(Pos.CENTER);
        double width = 30, height = 30;
        boolean grow = false;
        for (int i = 0; i < 7; i++) {
            if (i == 5 || i == 1) {
                width = 70;
                height = 30;
                grow = false;
            } else if (i == 2) {
                width = 200;
                height = 200;
                grow = true;
            } else if (i == 6) {
                width = 30;
                height = 30;
                grow = false;
            }
            ColumnConstraints col;
            if (grow) {
                col = new ColumnConstraints();
                col.setHgrow(Priority.ALWAYS);
            } else {
                col = new ColumnConstraints(width);
            }
            getColumnConstraints().add(col);

            RowConstraints row;
            if (grow) {
                row = new RowConstraints();
                row.setVgrow(Priority.ALWAYS);
            } else {
                row = new RowConstraints(height);
            }
            getRowConstraints().add(row);
        }

    }

    private void setup_cards() {
        tcard.setPreserveRatio(true);
        bcard.setPreserveRatio(true);
        rcard.setPreserveRatio(true);
        lcard.setPreserveRatio(true);

        tcard.fitWidthProperty().bind(MainApp.announcePlayProperty);
        bcard.fitWidthProperty().bind(MainApp.announcePlayProperty);
        rcard.fitWidthProperty().bind(MainApp.announcePlayProperty);
        lcard.fitWidthProperty().bind(MainApp.announcePlayProperty);
    }

    private void setup_prev_cards() {
        prevTcard.setPreserveRatio(true);
        prevBcard.setPreserveRatio(true);
        prevLcard.setPreserveRatio(true);
        prevRcard.setPreserveRatio(true);

        prevTcard.fitWidthProperty().bind(MainApp.prevCardsProperty);
        prevBcard.fitWidthProperty().bind(MainApp.prevCardsProperty);
        prevLcard.fitWidthProperty().bind(MainApp.prevCardsProperty);
        prevRcard.fitWidthProperty().bind(MainApp.prevCardsProperty);

        prevTcard.setOpacity(0);
        prevBcard.setOpacity(0);
        prevLcard.setOpacity(0);
        prevRcard.setOpacity(0);
    }

    private void setup_prevTurns() {
        prevBTurn.next = prevRTurn;
        prevRTurn.next = prevTTurn;
        prevTTurn.next = prevLTurn;
        prevLTurn.next = prevBTurn;
    }

    private void setup_Say() {
        tsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        bsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        lsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        rsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        tsay.setOpacity(0);
        bsay.setOpacity(0);
        lsay.setOpacity(0);
        rsay.setOpacity(0);
    }

    private void setup_prevSay() {
        prevTsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        prevBsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        prevLsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        prevRsay.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        prevTsay.setOpacity(0);
        prevBsay.setOpacity(0);
        prevLsay.setOpacity(0);
        prevRsay.setOpacity(0);
    }

    @Override
    void add_gui_elements() {
        GridPane.setHalignment(bcard, HPos.CENTER);
        GridPane.setHalignment(tcard, HPos.CENTER);
        GridPane.setHalignment(rcard, HPos.CENTER);
        GridPane.setHalignment(lcard, HPos.CENTER);

        GridPane.setHalignment(prevTcard, HPos.RIGHT);
        GridPane.setHalignment(prevBcard, HPos.LEFT);
        GridPane.setHalignment(prevLcard, HPos.LEFT);
        GridPane.setHalignment(prevRcard, HPos.RIGHT);

        GridPane.setHalignment(bsay, HPos.CENTER);
        GridPane.setHalignment(tsay, HPos.CENTER);
        GridPane.setHalignment(rsay, HPos.CENTER);
        GridPane.setHalignment(lsay, HPos.CENTER);

        GridPane.setHalignment(prevBsay, HPos.LEFT);
        GridPane.setHalignment(prevTsay, HPos.RIGHT);
        GridPane.setHalignment(prevRsay, HPos.CENTER);
        GridPane.setHalignment(prevLsay, HPos.CENTER);

        add(tcard, 3, 2);
        add(lcard, 2, 3);
        add(rcard, 4, 3);
        add(bcard, 3, 4);

        add(prevTcard, 2, 2);
        add(prevLcard, 2, 4);
        add(prevRcard, 4, 2);
        add(prevBcard, 4, 4);

        add(bottomTurn, 3, 6);
        add(rightTurn, 6, 3);
        add(topTurn, 3, 0);
        add(leftTurn, 0, 3);

        add(prevBTurn, 4, 6);
        add(prevRTurn, 6, 2);
        add(prevTTurn, 2, 0);
        add(prevLTurn, 0, 4);

        add(tsay, 3, 1);
        add(bsay, 3, 5);
        add(lsay, 1, 3);
        add(rsay, 5, 3);

        add(prevTsay, 2, 1);
        add(prevBsay, 4, 5);
        add(prevLsay, 1, 4);
        add(prevRsay, 5, 2);
    }

    @Override
    public void setup_first_turn() {
        super.setup_first_turn();
        switch (handler.getGame().getDealer().next()) {
            case BOTTOM: {
                starting_pos = prevBTurn;
                break;
            }
            case RIGHT: {
                starting_pos = prevRTurn;
                break;
            }
            case TOP: {
                starting_pos = prevTTurn;
                break;
            }
            case LEFT: {
                starting_pos = prevLTurn;
                break;
            }
        }
    }

    public void play(Card card, MyTimeLine my_timeLine, Thread annonceThread,
                     boolean last_card_played) {

        Timeline timeLine;
        if (my_timeLine == null) {
            timeLine = new Timeline();
            my_timeLine = new MyTimeLine(timeLine);
        } else {
            timeLine = my_timeLine.timeLine;
        }
        switch (card.getPosition()) {
            case BOTTOM: {
                bcard.setTranslateY(30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(bcard.translateYProperty(), 0,
                                        Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(bcard.opacityProperty(), 1)));
                break;
            }
            case RIGHT: {
                rcard.setTranslateX(30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(rcard.translateXProperty(), 0,
                                        Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(rcard.opacityProperty(), 1)));
                break;
            }
            case TOP: {
                tcard.setTranslateY(-30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(tcard.translateYProperty(), 0,
                                        Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(tcard.opacityProperty(), 1)));
                break;
            }
            case LEFT: {
                lcard.setTranslateX(-30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(lcard.translateXProperty(), 0,
                                        Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(lcard.opacityProperty(), 1)));
                break;
            }
        }
        if (last_card_played) {
            EventHandler<ActionEvent> eventHandler = e -> {
            };
            timeLine.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(3), eventHandler));
        }
        switch (card.getPosition()) {
            case BOTTOM: {
                bcard.setImage(card.getImage());
                break;
            }
            case RIGHT: {
                rcard.setImage(card.getImage());
                break;
            }
            case TOP: {
                tcard.setImage(card.getImage());
                break;
            }
            case LEFT: {
                lcard.setImage(card.getImage());
                break;
            }
        }
        timeLine.setOnFinished(e -> annonceThread.start());
        my_timeLine.play();
    }

    public void clear(RoomPosition demanded_pos, boolean der) {
        Timeline timeLine = null;
        switch (handler.getGame().getCurrentPosition()) { // winner position
            case BOTTOM: {
                timeLine = slide_cards_bottom();
                break;
            }
            case RIGHT: {
                timeLine = slide_cards_right();
                break;
            }
            case TOP: {
                timeLine = slide_cards_top();
                break;
            }
            case LEFT: {
                timeLine = slide_cards_left();
                break;
            }
        }
        timeLine.setOnFinished(e -> {
            reset_cards_XY_properties();
            handler.getGame().disable_animation_timer();
            prevBcard.setImage(bcard.getImage());
            prevTcard.setImage(tcard.getImage());
            prevLcard.setImage(lcard.getImage());
            prevRcard.setImage(rcard.getImage());
            prevBsay.setText(bsay.getText());
            prevTsay.setText(tsay.getText());
            prevLsay.setText(lsay.getText());
            prevRsay.setText(rsay.getText());
            while (starting_pos.position != demanded_pos) starting_pos = starting_pos.next;
            handler.getGame().enable_animatio_timer();
            tcard.setImage(null);
            bcard.setImage(null);
            lcard.setImage(null);
            rcard.setImage(null);
            bsay.setText("");
            tsay.setText("");
            lsay.setText("");
            rsay.setText("");
            if (board.round == 9) handler.getGame().
                    checkWinner(der, board.yourPlies_count, board.opPlies_count);
            else handler.getGame().setPlayable(true);
        });
        timeLine.play();
    }

    private void reset_cards_XY_properties() {
        bcard.setTranslateX(0);
        bcard.setTranslateY(0);

        tcard.setTranslateX(0);
        tcard.setTranslateY(0);

        rcard.setTranslateX(0);
        rcard.setTranslateY(0);

        lcard.setTranslateX(0);
        lcard.setTranslateY(0);
    }

    private Timeline slide_cards_bottom() {
        Timeline timeLine = new Timeline();

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(bcard.translateYProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(bcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(tcard.translateYProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(tcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(rcard.translateYProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(rcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(lcard.translateYProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(lcard.opacityProperty(), 0)));

        return timeLine;
    }

    private Timeline slide_cards_top() {
        Timeline timeLine = new Timeline();

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(tcard.translateYProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(tcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(bcard.translateYProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(bcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(rcard.translateYProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(rcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(lcard.translateYProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(lcard.opacityProperty(), 0)));

        return timeLine;
    }

    private Timeline slide_cards_right() {
        Timeline timeLine = new Timeline();

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(rcard.translateXProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(rcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(lcard.translateXProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(lcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(tcard.translateXProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(tcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(bcard.translateXProperty(), 30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(bcard.opacityProperty(), 0)));

        return timeLine;
    }

    private Timeline slide_cards_left() {
        Timeline timeLine = new Timeline();

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(lcard.translateXProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(lcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(rcard.translateXProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(rcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(tcard.translateXProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(tcard.opacityProperty(), 0)));

        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(bcard.translateXProperty(), -30, Interpolator.EASE_IN)));
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(bcard.opacityProperty(), 0)));

        return timeLine;
    }

    public void showPrev(boolean show) {
        if (prevShown && !show) {
            prevTsay.setOpacity(0);
            prevBsay.setOpacity(0);
            prevLsay.setOpacity(0);
            prevRsay.setOpacity(0);
            prevTcard.setOpacity(0);
            prevBcard.setOpacity(0);
            prevLcard.setOpacity(0);
            prevRcard.setOpacity(0);
            starting_pos.hide();
            prevShown = false;
        } else if (show && !prevShown) {
            if (prevBcard != null) {
                prevShown = true;
                prevTsay.setOpacity(1);
                prevBsay.setOpacity(1);
                prevLsay.setOpacity(1);
                prevRsay.setOpacity(1);
                prevTcard.setOpacity(1);
                prevBcard.setOpacity(1);
                prevLcard.setOpacity(1);
                prevRcard.setOpacity(1);
                starting_pos.show();
            }
        }
    }

    public void reveal_decs(Map<RoomPosition, String> decs) {
        MyTimeLine my_timeLine = new MyTimeLine(new Timeline());

        String bottom_decs = decs.get(RoomPosition.BOTTOM);
        if (bottom_decs != null) say(RoomPosition.BOTTOM, bottom_decs, my_timeLine);

        String top_decs = decs.get(RoomPosition.TOP);
        if (top_decs != null) say(RoomPosition.TOP, top_decs, my_timeLine);

        String right_decs = decs.get(RoomPosition.RIGHT);
        if (right_decs != null) say(RoomPosition.RIGHT, right_decs, my_timeLine);

        String left_decs = decs.get(RoomPosition.LEFT);
        if (left_decs != null) say(RoomPosition.LEFT, left_decs, my_timeLine);

        EventHandler<ActionEvent> eventHandler = e -> {
        };
        my_timeLine.timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(3), eventHandler));
        my_timeLine.timeLine.setAutoReverse(true);
        my_timeLine.timeLine.setCycleCount(2);
        my_timeLine.timeLine.setOnFinished(e -> {
            bsay.setText("");
            tsay.setText("");
            lsay.setText("");
            rsay.setText("");
            handler.getGame().setPlayable(true);
        });
        my_timeLine.play();
    }

    public MyTimeLine say(RoomPosition who, String what, MyTimeLine my_timeLine) {
        Timeline timeLine;
        if (my_timeLine == null) {
            timeLine = new Timeline();
            my_timeLine = new MyTimeLine(timeLine);
        } else {
            timeLine = my_timeLine.timeLine;
        }
        switch (who) {
            case BOTTOM: {
                my_timeLine.put_text(bsay, what);
                bsay.setTranslateY(30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(bsay.translateYProperty(), 0, Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1), new KeyValue(bsay.opacityProperty(), 1)));
                break;
            }
            case RIGHT: {
                my_timeLine.put_text(rsay, what);
                rsay.setTranslateX(30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(rsay.translateXProperty(), 0, Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1), new KeyValue(rsay.opacityProperty(), 1)));
                break;
            }
            case TOP: {
                my_timeLine.put_text(tsay, what);
                tsay.setTranslateY(-30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(tsay.translateYProperty(), 0, Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1), new KeyValue(tsay.opacityProperty(), 1)));
                break;
            }
            case LEFT: {
                my_timeLine.put_text(lsay, what);
                lsay.setTranslateX(-30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(lsay.translateXProperty(), 0, Interpolator.EASE_IN)));
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1), new KeyValue(lsay.opacityProperty(), 1)));
                break;
            }
        }
        return my_timeLine;
    }

    public void add_belote_to_say(RoomPosition who, MyTimeLine say, String belote) {
        switch (who) {
            case BOTTOM: {
                say.add_text(bsay, belote);
                break;
            }
            case RIGHT: {
                say.add_text(rsay, belote);
                break;
            }
            case TOP: {
                say.add_text(tsay, belote);
                break;
            }
            case LEFT: {
                say.add_text(lsay, belote);
                break;
            }
        }
    }

    public void showDec(RoomPosition position, Combination comb, CountDownLatch decShow_latch) {
        try {
            showDec_order.acquire();
            VBox vbox = Assets.getDeclaration(comb);
            Platform.runLater(() -> {
                vbox.setOpacity(0);
                Timeline timeLine = new Timeline();
                switch (position) {
                    case BOTTOM: {
                        vbox.setTranslateY(bcard.getLayoutY());
                        timeLine.getKeyFrames().add(
                                new KeyFrame(Duration.seconds(3),
                                        new KeyValue(vbox.translateYProperty(), 0, Interpolator.EASE_IN)));
                        break;
                    }
                    case RIGHT: {
                        vbox.setTranslateX(rcard.getLayoutX());
                        timeLine.getKeyFrames().add(
                                new KeyFrame(Duration.seconds(3),
                                        new KeyValue(vbox.translateXProperty(), 0, Interpolator.EASE_IN)));
                        break;
                    }
                    case TOP: {
                        vbox.setTranslateY(-tcard.getLayoutY());
                        timeLine.getKeyFrames().add(
                                new KeyFrame(Duration.seconds(3),
                                        new KeyValue(vbox.translateYProperty(), 0, Interpolator.EASE_IN)));
                        break;
                    }
                    case LEFT: {
                        vbox.setTranslateX(-lcard.getLayoutX());
                        timeLine.getKeyFrames().add(
                                new KeyFrame(Duration.seconds(3),
                                        new KeyValue(vbox.translateXProperty(), 0, Interpolator.EASE_IN)));
                        break;
                    }
                }
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(3), new KeyValue(vbox.opacityProperty(), 1)));
                EventHandler<ActionEvent> eventHandler = e -> {
                };
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(5), eventHandler));
                timeLine.setAutoReverse(true);
                timeLine.setCycleCount(2);
                add(vbox, 3, 3);
                timeLine.setOnFinished(e -> {
                    getChildren().remove(vbox);
                    showDec_order.release();
                    decShow_latch.countDown();
                });
                timeLine.play();
            });
        } catch (InterruptedException ignore) {
        }
    }
}
