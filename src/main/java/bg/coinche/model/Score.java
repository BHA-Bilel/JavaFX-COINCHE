package bg.coinche.model;

import bg.coinche.lang.Language;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import shared.RoomPosition;

public class Score extends TableView<TableRow> {

    public Score() {
        TableColumn<TableRow, String> buy = new TableColumn<>();
        buy.textProperty().bind(Language.BUY);
        TableColumn<TableRow, String> you = new TableColumn<>();
        you.textProperty().bind(Language.YOU);
        TableColumn<TableRow, String> op = new TableColumn<>();
        op.textProperty().bind(Language.THEM);
        buy.setCellValueFactory(new PropertyValueFactory<>("value"));
        you.setCellValueFactory(new PropertyValueFactory<>("you"));
        op.setCellValueFactory(new PropertyValueFactory<>("op"));
        getColumns().add(buy);
        getColumns().add(you);
        getColumns().add(op);
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setPlaceholder(new Label(Language.table_placeholder()));
    }

    public void add(Bid bid, int Ytotal, int Ototal) {
        getItems().add(new TableRow(
                bid.toString() + (bid.getTrump().ordinal() < 4 ? " " + bid.getTrump() : "")
                        + " " + (bid.youBought() ? Language.YOU.getValue() : Language.THEM.getValue())
                        + (bid.getCoinche() == 2 ? "  *2" : bid.getCoinche() == 4 ? " *4" : ""),
                Integer.toString(Ytotal), Integer.toString(Ototal)));
    }

}
