package bg.coinche.model;

import bg.coinche.lang.Language;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Result extends TableView<TableRow> {

    public Result() {
        TableColumn<TableRow, String> value = new TableColumn<>();
        TableColumn<TableRow, String> you = new TableColumn<>();
        you.textProperty().bind(Language.YOU);
        TableColumn<TableRow, String> op = new TableColumn<>();
        op.textProperty().bind(Language.THEM);
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        you.setCellValueFactory(new PropertyValueFactory<>("you"));
        op.setCellValueFactory(new PropertyValueFactory<>("op"));
        getColumns().add(value);
        getColumns().add(you);
        getColumns().add(op);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setPlaceholder(new Label(Language.table_placeholder()));
    }

    public void update(Bid bid, boolean won, String fail_reasons, int dedans,
                       int your_fold_pts, int op_fold_pts,
                       int your_round_pts, int op_round_pts,
                       boolean your_capot, boolean op_capot,
                       int yourDec, int opDec,
                       String yourDecDetail, String opDecDetail,
                       int Ytotal, int Ototal) {

        getItems().clear();
        String bid_str = bid.one_line_str() + (bid.getTrump().ordinal() < 4 ? " " + bid.getTrump() : "");
        getItems().add(new TableRow(Language.CONTRACT.getValue(),
                bid.youBought() ? bid_str : "", bid.youBought() ? "" : bid_str));
        boolean sa_ta = bid.getTrump() == Suit.SA || bid.getTrump() == Suit.TA;
        String fail_pts_str = bid.pts_to_fail_contract((bid.youBought() ? yourDec : opDec)).getValue();
        getItems().add(new TableRow(Language.PTS_CONT_FAIL_H.getValue(), bid.youBought() ? "" : fail_pts_str,
                bid.youBought() ? fail_pts_str : ""));

        if (bid.isCorS()) {
            if (bid.youBought())
                getItems().add(new TableRow(Language.COINCHE_2.getValue(),
                        bid.getCoinche() == 4 ? Language.COINCHE_4.getValue() : "", Language.COINCHE_2.getValue()));
            else
                getItems().add(new TableRow(Language.COINCHE_2.getValue(),
                        Language.COINCHE_2.getValue(), bid.getCoinche() == 4 ? Language.COINCHE_4.getValue() : ""));
        }

        String your_round_calc = (bid.youBought() ? bid.fullValue() + " x " + bid.getCoinche() + " + " : "")
                + (your_fold_pts >= 250 ?
                your_fold_pts + " (" + Language.PLAYED_CAPOT.getValue() + ")" : your_fold_pts);
        String op_round_calc = (bid.youBought() ? "" : bid.fullValue() + " x " + bid.getCoinche() + " + ")
                + (op_fold_pts >= 250 ? op_fold_pts + " (" + Language.PLAYED_CAPOT.getValue() + ")" : op_fold_pts);

        String init_Yround_calc = your_round_calc, init_OPround_calc = op_round_calc;

        getItems().add(new TableRow((yourDec + opDec > 0) ? Language.PTS_DONE.getValue()
                : Language.TOTAL_PTS_DONE.getValue(), Integer.toString(your_fold_pts), Integer.toString(op_fold_pts)));

        if (!sa_ta) {
            if (yourDec > 0)
                your_round_calc += " + " + yourDec + " (" + Language.DECLARATIONS.getValue() + ")";
            if (opDec > 0)
                op_round_calc += " + " + opDec + " (" + Language.DECLARATIONS.getValue() + ")";
            if (yourDec > 0 || opDec > 0)
                getItems().add(new TableRow(Language.DECLARATIONS.getValue(), yourDec > 0 ?
                        yourDec + " " + yourDecDetail : "",
                        opDec > 0 ? opDec + " " + opDecDetail : ""));
            if (yourDec + opDec > 0)
                getItems().add(new TableRow(Language.TOTAL.getValue(),
                        Integer.toString(your_fold_pts + yourDec), Integer.toString(op_fold_pts + opDec)));
        }
        getItems().add(new TableRow(Language.RESULT.getValue(), bid.youBought() ? won ?
                Language.WON.getValue() : Language.FAILED.getValue() : "",
                bid.youBought() ? "" : won ? Language.WON.getValue() : Language.FAILED.getValue()));
        if (!won) {
            getItems().add(new TableRow(Language.REASONS.getValue(), bid.youBought() ? fail_reasons : "",
                    bid.youBought() ? "" : fail_reasons));
            if (your_capot || op_capot) {
                if (your_capot && !bid.youBought())
                    your_round_calc += " + " + your_round_pts + " (" + Language.DEFENDER_CAPOT.getValue() + ")";
                if (op_capot && bid.youBought())
                    op_round_calc += " + " + op_round_pts + " (" + Language.DEFENDER_CAPOT.getValue() + ")";
            }
        }
        if (dedans > 0) {
            if (bid.youBought()) {
                if (won) {
                    your_round_calc += " + " + dedans + " (" + Language.COINCHE_WIN_BONUS.getValue() + ")";
                } else {
                    op_round_calc += " + " + dedans + " (" + (bid.isCorS() ?
                            Language.COINCHE_FAIL_PENALTY.getValue() : Language.CONTRACT_FAIL_PENALTY.getValue()) + ")";
                }
            } else {
                if (won) {
                    op_round_calc += " + " + dedans + " (" + Language.COINCHE_WIN_BONUS.getValue() + ")";
                } else {
                    your_round_calc += " + " + dedans + " (" + (bid.isCorS() ?
                            Language.COINCHE_FAIL_PENALTY.getValue() : Language.CONTRACT_FAIL_PENALTY.getValue()) + ")";
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
                    + (op_round_calc.isEmpty() ? "" : " + " + op_round_calc)
                    + (your_round_calc.isEmpty() ? "" : " + " + your_round_calc);
            if (won) {
                if (bid.youBought()) {
                    your_round_calc = loss_calc;
                    op_round_calc = Language.LOSER_GET_NOTHING.getValue();
                } else {
                    op_round_calc = loss_calc;
                    your_round_calc = Language.LOSER_GET_NOTHING.getValue();
                }
            } else {
                if (bid.youBought()) {
                    op_round_calc = loss_calc;
                    your_round_calc = Language.LOSER_GET_NOTHING.getValue();
                } else {
                    your_round_calc = loss_calc;
                    op_round_calc = Language.LOSER_GET_NOTHING.getValue();
                }
            }
        }
        your_round_calc = your_round_calc.replaceAll("\\+", "\n+");
        op_round_calc = op_round_calc.replaceAll("\\+", "\n+");
        getItems().add(new TableRow(Language.PTS_CALCULATION.getValue(), your_round_calc, op_round_calc));

        getItems().add(new TableRow(Language.ROUND_TOTAL.getValue(),
                Integer.toString(your_round_pts), Integer.toString(op_round_pts)));
        getItems().add(new TableRow(Language.MATCH_TOTAL.getValue(),
                Integer.toString(Ytotal), Integer.toString(Ototal)));
    }
}
