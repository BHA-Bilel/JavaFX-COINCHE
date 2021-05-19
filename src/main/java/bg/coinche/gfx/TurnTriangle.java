package bg.coinche.gfx;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import shared.RoomPosition;

public class TurnTriangle extends VBox {
    public final RoomPosition position;
    public TurnTriangle next;
    public JFXButton triangle;

    public TurnTriangle(RoomPosition position, boolean flip) {
        this.position = position;
        triangle = new JFXButton();
        switch (position) {
            case BOTTOM:
            case TOP: {
                triangle.setPrefWidth(50);
                break;
            }
            case RIGHT:
            case LEFT: {
                triangle.setPrefHeight(50);
                break;
            }
        }
        if (flip)
            switch (position) {
                case BOTTOM: {
                    triangle.setStyle("-fx-background-color: blue;" + " -fx-shape: 'M 0 4 L 4 4 L 2 0 Z'");
                    break;
                }
                case RIGHT: {
                    triangle.setStyle("-fx-background-color: blue;" + " -fx-shape: 'M 4 0 L 4 4 L 0 2 Z'");
                    break;
                }
                case TOP: {
                    triangle.setStyle("-fx-background-color: blue;" + " -fx-shape: 'M 0 0 L 4 0 L 2 4 Z'");
                    break;
                }
                case LEFT: {
                    triangle.setStyle("-fx-background-color: blue;" + " -fx-shape: 'M 0 0 L 0 4 L 4 2 Z'");
                    break;
                }
            }
        else
            switch (position) {
                case BOTTOM: {
                    triangle.setStyle("-fx-background-color: blue; -fx-shape: 'M 0 0 L 4 0 L 2 4 Z'");
                    break;
                }
                case RIGHT: {
                    triangle.setStyle("-fx-background-color: blue; -fx-shape: 'M 0 0 L 0 4 L 4 2 Z'");
                    break;
                }
                case TOP: {
                    triangle.setStyle("-fx-background-color: blue; -fx-shape: 'M 0 4 L 4 4 L 2 0 Z'");
                    break;
                }
                case LEFT: {
                    triangle.setStyle("-fx-background-color: blue; -fx-shape: 'M 4 0 L 4 4 L 0 2 Z'");
                    break;
                }
            }
        triangle.setDisable(true);
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

    public static void adapt_turn(TurnTriangle current_turn) {
        Timeline timeLine = new Timeline();
        TurnTriangle old = current_turn.next;
        while (old.triangle.getOpacity() != 1)
            old = old.next;
        if (old != current_turn) {
            timeLine = old.disappear();
        }
        current_turn.enlighten(timeLine);
        timeLine.setAutoReverse(false);
        timeLine.setCycleCount(1);
        timeLine.play();
    }

    public void init_turn() {
        Timeline timeLine = new Timeline();
        enlighten(timeLine);
        timeLine.setAutoReverse(false);
        timeLine.setCycleCount(1);
        timeLine.play();
    }

    public void switch_turn() {
        Timeline timeLine = disappear();
        next.enlighten(timeLine);
        timeLine.setAutoReverse(false);
        timeLine.setCycleCount(1);
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        TurnTriangle turn = (TurnTriangle) obj;
        return position == turn.position;
    }

}
