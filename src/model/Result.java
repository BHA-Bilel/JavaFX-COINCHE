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

    public void update(Bid bid, boolean won, String fail_reasons, int dedans,
                       int your_fold_pts, int op_fold_pts,
                       int your_round_pts, int op_round_pts,
                       boolean your_capot, boolean op_capot,
                       int yourDec, int opDec,
                       String yourDecDetail, String opDecDetail,
                       int Ytotal, int Ototal) {

        getItems().clear();
        String bid_str = bid.one_line_str() + (bid.getTrump().getIndex() < 4 ? " " + bid.getTrump() : "");
        getItems().add(new TableRow("Contract", bid.youBought() ? bid_str : "", bid.youBought() ? "" : bid_str));
        boolean sa_ta = bid.getTrump() == Suit.SA || bid.getTrump() == Suit.TA;
        String fail_pts_str = bid.pts_to_fail_contract((bid.youBought() ? yourDec : opDec));
        getItems().add(new TableRow("Points to fail contract", bid.youBought() ? "" : fail_pts_str,
                bid.youBought() ? fail_pts_str : ""));

        if (bid.isCorS()) {
            if (bid.youBought())
                getItems().add(new TableRow("Coinche", bid.getCoinche() == 4 ? "Surcoinche" : "", "Coinche"));
            else
                getItems().add(new TableRow("Coinche", "Coinche", bid.getCoinche() == 4 ? "Surcoinche" : ""));
        }

        String your_round_calc = (bid.youBought() ? bid.fullValue() + " x " + bid.getCoinche() + " + " : "")
                + (your_fold_pts >= 250 ? your_fold_pts + " (Played capot)" : your_fold_pts);
        String op_round_calc = (bid.youBought() ? "" : bid.fullValue() + " x " + bid.getCoinche() + " + ")
                + (op_fold_pts >= 250 ? op_fold_pts + " (Played capot)" : op_fold_pts);

        String init_Yround_calc = your_round_calc, init_OPround_calc = op_round_calc;

        getItems().add(new TableRow((yourDec + opDec > 0) ? "Points done" : "Total points done", Integer.toString(your_fold_pts), Integer.toString(op_fold_pts)));

        if (!sa_ta) {
            if (yourDec > 0)
                your_round_calc += " + " + yourDec + " (Declarations)";
            if (opDec > 0)
                op_round_calc += " + " + opDec + " (Declarations)";
            if (yourDec > 0 || opDec > 0)
                getItems().add(new TableRow("Declarations", yourDec > 0 ? yourDec + " " + yourDecDetail : "",
                        opDec > 0 ? opDec + " " + opDecDetail : ""));
            if (yourDec + opDec > 0)
                getItems().add(new TableRow("Total", Integer.toString(your_fold_pts + yourDec), Integer.toString(op_fold_pts + opDec)));
        }
        getItems().add(new TableRow("Result", bid.youBought() ? won ? "Won" : "Failed" : "", bid.youBought() ? "" : won ? "WON" : "FAILED"));
        if (!won) {
            getItems().add(new TableRow("Reason(s)", bid.youBought() ? fail_reasons : "", bid.youBought() ? "" : fail_reasons));
            if (your_capot || op_capot) {
                if (your_capot && !bid.youBought())
                    your_round_calc += " + " + your_round_pts + " (Defender Capot)";
                if (op_capot && bid.youBought())
                    op_round_calc += " + " + op_round_pts + " (Defender Capot)";
            }
        }
        if (dedans > 0) {
            if (bid.youBought()) {
                if (won) {
                    your_round_calc += " + " + dedans + " (Coinche win bonus)";
                } else {
                    op_round_calc += " + " + dedans + " (" + (bid.isCorS() ? "Coinche fail penalty" : "Failed contract penalty") + ")";
                }
            } else {
                if (won) {
                    op_round_calc += " + " + dedans + " (Coinche win bonus)";
                } else {
                    your_round_calc += " + " + dedans + " (" + (bid.isCorS() ? "Coinche fail penalty" : "Failed contract penalty") + ")";
                }
            }
        }

        if (!won || bid.isCorS()) {
            if (your_round_calc.startsWith(init_Yround_calc + " + "))
                init_Yround_calc += " + ";
            if (op_round_calc.startsWith(init_OPround_calc + " + "))
                init_OPround_calc += " + ";
            op_round_calc = op_round_calc.substring(init_OPround_calc.length());
            your_round_calc = your_round_calc.substring(init_Yround_calc.length());
            String loss_calc = bid.fullValue() + (bid.getCoinche() > 1 ? " x " + bid.getCoinche() : "")
                    + (op_round_calc.isEmpty() ? "" : " +\n" + op_round_calc)
                    + (your_round_calc.isEmpty() ? "" : " +\n" + your_round_calc);
            if (bid.youBought()) {
                op_round_calc = loss_calc;
                your_round_calc = "Loser gets nothing";
            } else {
                your_round_calc = loss_calc;
                op_round_calc = "Loser gets nothing";
            }
        }

        getItems().add(new TableRow("Points calculation", your_round_calc, op_round_calc));

        getItems().add(new TableRow("Round Total", Integer.toString(your_round_pts), Integer.toString(op_round_pts)));
        getItems().add(new TableRow("Match Total", Integer.toString(Ytotal), Integer.toString(Ototal)));
    }
}
