package bg.coinche.gfx;

import bg.coinche.game.Handler;
import javafx.scene.layout.GridPane;
import shared.RoomPosition;

public abstract class GameGUI extends GridPane {

    final Handler handler;
    final Board board;
    TurnTriangle current_turn;
    TurnTriangle bottomTurn, rightTurn, topTurn, leftTurn;

    public GameGUI(Handler handler, Board board) {
        this.handler = handler;
        this.board = board;
        init_grid();
        initTurns();
    }

    abstract void init_grid();

    abstract void add_gui_elements();

    void initTurns() {
        bottomTurn = new TurnTriangle(RoomPosition.BOTTOM, false);
        rightTurn = new TurnTriangle(RoomPosition.RIGHT, false);
        topTurn = new TurnTriangle(RoomPosition.TOP, false);
        leftTurn = new TurnTriangle(RoomPosition.LEFT, false);

        bottomTurn.next = rightTurn;
        rightTurn.next = topTurn;
        topTurn.next = leftTurn;
        leftTurn.next = bottomTurn;
        current_turn = bottomTurn; // to avoid null pointer exception on first_game
    }

    public void adapt_turn() {
        while (current_turn.position != handler.getGame().getCurrentPosition()) current_turn = current_turn.next;
        TurnTriangle.adapt_turn(current_turn);
    }

    public void setup_first_turn() {
        switch (handler.getGame().getDealer().next()) {
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
    }

    public void switchTurn() {
        current_turn.switch_turn();
        current_turn = current_turn.next;
    }

    public void show_first_turn() {
        current_turn.init_turn();
    }

    public void hide_turn() {
        current_turn.hide();
    }
}
