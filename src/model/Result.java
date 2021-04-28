package model;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Result extends TableView<TableRow> {

    public Result() {
        TableColumn<TableRow, String> value = new TableColumn<>();
        TableColumn<TableRow, String> you = new TableColumn<>("You");
        TableColumn<TableRow, String> op = new TableColumn<>("Opponent");
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        you.setCellValueFactory(new PropertyValueFactory<>("you"));
        op.setCellValueFactory(new PropertyValueFactory<>("op"));
        getColumns().add(value);
        getColumns().add(you);
        getColumns().add(op);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    }

    public void update(Bid bid, boolean won, int dedans, int your_fold_pts, int op_fold_pts,
                       int your_round_pts, int op_round_pts, int belote, int yourDec, int opDec, int Ytotal, int Ototal) {

        getItems().clear();
        String bid_str = bid.one_line_str() + (bid.getTrump().getIndex() < 4 ? " " + bid.getTrump() : "");
        getItems().add(new TableRow("Contract", bid.youBought() ? bid_str : "", bid.youBought() ? "" : bid_str));
        boolean sa_ta = bid.getTrump() == Suit.SA || bid.getTrump() == Suit.TA;
        String fail_pts_str = bid.pts_to_fail_contract((bid.youBought() ? yourDec : opDec));
        getItems().add(new TableRow("Points to fail contract", bid.youBought() ? "" : fail_pts_str,
                bid.youBought() ? fail_pts_str : ""));

        if (bid.isCoinchedOrSurcoinched()) {
            if (bid.youBought())
                getItems().add(new TableRow("Coinche", bid.getCoinche() == 4 ? "Surcoinche" : "", "Coinche"));
            else
                getItems().add(new TableRow("Coinche", "Coinche", bid.getCoinche() == 4 ? "Surcoinche" : ""));
        }

        getItems().add(new TableRow((yourDec + opDec > 0) ? "Points done" : "Total points done", Integer.toString(your_fold_pts), Integer.toString(op_fold_pts)));
        if (belote > 0)
            getItems().add(new TableRow("Belote", belote == 1 ? "20" : "", belote == 2 ? "20" : ""));
        if (!sa_ta) {
            int your_dec = yourDec - (belote == 1 ? 20 : 0), op_dec = opDec - (belote == 2 ? 20 : 0);
            if (your_dec > 0 || op_dec > 0)
                getItems().add(new TableRow("Declarations", your_dec > 0 ? Integer.toString(your_dec) : "",
                        op_dec > 0 ? Integer.toString(op_dec) : ""));
            if (yourDec + opDec > 0)
                getItems().add(new TableRow("Total", Integer.toString(your_fold_pts + yourDec), Integer.toString(op_fold_pts + opDec)));
        }
        getItems().add(new TableRow("Result", bid.youBought() ? won ? "Won" : "Failed" : "", bid.youBought() ? "" : won ? "WON" : "FAILED"));

        if (dedans > 0) {
            String case1 = bid.youBought() ? Integer.toString(dedans) : "";
            String case2 = bid.youBought() ? "" : Integer.toString(dedans);
            if (won)
                getItems().add(new TableRow("Coinche win bonus", case1, case2));
            else
                getItems().add(new TableRow(bid.isCoinchedOrSurcoinched() ? "Coinche fail penalty" : "Failed contract penalty", case2, case1));
        }

        getItems().add(new TableRow("Round Total", Integer.toString(your_round_pts), Integer.toString(op_round_pts)));
        getItems().add(new TableRow("Match Total", Integer.toString(Ytotal), Integer.toString(Ototal)));
    }
}
