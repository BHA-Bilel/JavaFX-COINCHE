package bg.coinche.model;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Score extends TableView<TableRow> {

    public Score() {
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

    public void add(Bid bid, int Ytotal, int Ototal) {
        getItems().add(new TableRow(
                bid.toString() + (bid.getTrump().getIndex() < 4 ? " " + bid.getTrump() : "")
                        + (bid.youBought() ? " YOU" : " OP.")
                        + (bid.getCoinche() == 2 ? "  *2" : bid.getCoinche() == 4 ? " *4" : ""),
                Integer.toString(Ytotal), Integer.toString(Ototal)));
    }

}
