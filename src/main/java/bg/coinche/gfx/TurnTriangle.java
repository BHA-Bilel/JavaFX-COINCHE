package bg.coinche.gfx;

import bg.coinche.MainApp;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import shared.RoomPosition;

public class TurnTriangle extends VBox {

    public RoomPosition position;
    public JFXButton triangle;
    public TurnTriangle next;

    public TurnTriangle(RoomPosition position, boolean flip) {
        this.position = position;
        triangle = new JFXButton();
        triangle.prefWidthProperty().bind(MainApp.turnSignProperty);
        triangle.prefHeightProperty().bind(MainApp.turnSignProperty);
        triangle.minHeightProperty().bind(MainApp.turnSignProperty);
        triangle.minWidthProperty().bind(MainApp.turnSignProperty);
        triangle.maxHeightProperty().bind(MainApp.turnSignProperty);
        triangle.maxWidthProperty().bind(MainApp.turnSignProperty);
        if (flip)
            switch (position) {
                case BOTTOM: {
                    triangle.setStyle("-fx-shape: 'M 0 4 L 4 4 L 2 0 Z'");
                    break;
                }
                case RIGHT: {
                    triangle.setStyle("-fx-shape: 'M 4 0 L 4 4 L 0 2 Z'");
                    break;
                }
                case TOP: {
                    triangle.setStyle("-fx-shape: 'M 0 0 L 4 0 L 2 4 Z'");
                    break;
                }
                case LEFT: {
                    triangle.setStyle("-fx-shape: 'M 0 0 L 0 4 L 4 2 Z'");
                    break;
                }
            }
        else
            switch (position) {
                case BOTTOM: {
                    triangle.setStyle("-fx-shape: 'M 0 0 L 4 0 L 2 4 Z'");
                    break;
                }
                case RIGHT: {
                    triangle.setStyle("-fx-shape: 'M 0 0 L 0 4 L 4 2 Z'");
                    break;
                }
                case TOP: {
                    triangle.setStyle("-fx-shape: 'M 0 4 L 4 4 L 2 0 Z'");
                    break;
                }
                case LEFT: {
                    triangle.setStyle("-fx-shape: 'M 4 0 L 4 4 L 0 2 Z'");
                    break;
                }
            }
        triangle.setDisable(true);
        triangle.getStyleClass().add("turn");
        triangle.setOpacity(0);
        if (flip) {
            switch (position) {
                case RIGHT:
                case LEFT: {
                    setAlignment(Pos.CENTER);
                    break;
                }
                case BOTTOM: {
                    setAlignment(Pos.CENTER_LEFT);
                    break;
                }
                case TOP: {
                    setAlignment(Pos.CENTER_RIGHT);
                    break;
                }
            }
        } else
            setAlignment(Pos.CENTER);
        getChildren().add(triangle);
    }

    public TurnTriangle(boolean flip) {
        triangle = new JFXButton();
        triangle.prefWidthProperty().bind(MainApp.turnSignProperty);
        triangle.prefHeightProperty().bind(MainApp.turnSignProperty);
        triangle.minHeightProperty().bind(MainApp.turnSignProperty);
        triangle.minWidthProperty().bind(MainApp.turnSignProperty);
        triangle.maxHeightProperty().bind(MainApp.turnSignProperty);
        triangle.maxWidthProperty().bind(MainApp.turnSignProperty);
        if (flip) {
            triangle.setStyle("-fx-shape: 'M 0 4 L 4 4 L 2 0 Z'");
            triangle.getStyleClass().add("take-place");
        } else {
            triangle.setStyle("-fx-shape: 'M 0 0 L 4 0 L 2 4 Z'");
            triangle.getStyleClass().add("kick");
        }
        setAlignment(Pos.CENTER);
        getChildren().add(triangle);
    }

    public void setOnAction(EventHandler<ActionEvent> e) {
        triangle.setOnAction(e);
    }

    public static void adapt_turn(TurnTriangle current_turn) {
        Timeline timeLine = new Timeline();
        TurnTriangle old = current_turn.next;
        while (old.triangle.getOpacity() != 1) old = old.next;
        if (old != current_turn) timeLine = old.disappear();
        current_turn.enlighten(timeLine);
        timeLine.play();
    }

    public void init_turn() {
        Timeline timeLine = new Timeline();
        enlighten(timeLine);
        timeLine.play();
    }

    public void switch_turn() {
        Timeline timeLine = disappear();
        next.enlighten(timeLine);
        timeLine.play();
    }

    public Timeline disappear() {
        Timeline timeLine = new Timeline();
        switch (position) {
            case BOTTOM: {
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateYProperty(), 30, Interpolator.EASE_IN)));
                break;
            }
            case RIGHT: {
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateXProperty(), 30, Interpolator.EASE_IN)));
                break;
            }
            case TOP: {
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateYProperty(), -30, Interpolator.EASE_IN)));
                break;
            }
            case LEFT: {
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateXProperty(), -30, Interpolator.EASE_IN)));
                break;
            }
        }
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(triangle.opacityProperty(), 0)));
        return timeLine;
    }

    private void enlighten(Timeline timeLine) {
        switch (position) {
            case BOTTOM: {
                triangle.setTranslateY(30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateYProperty(), 0, Interpolator.EASE_IN)));
                break;
            }
            case RIGHT: {
                triangle.setTranslateX(30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateXProperty(), 0, Interpolator.EASE_IN)));
                break;
            }
            case TOP: {
                triangle.setTranslateY(-30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateYProperty(), 0, Interpolator.EASE_IN)));
                break;
            }
            case LEFT: {
                triangle.setTranslateX(-30);
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(triangle.translateXProperty(), 0, Interpolator.EASE_IN)));
                break;
            }
        }
        timeLine.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), new KeyValue(triangle.opacityProperty(), 1)));

    }

    public void show() {
        triangle.setOpacity(1);
    }

    public void hide() {
        triangle.setOpacity(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        TurnTriangle turn = (TurnTriangle) obj;
        return position == turn.position;
    }

}
