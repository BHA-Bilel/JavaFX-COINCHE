package bg.coinche.gfx;

import bg.coinche.MainApp;
import bg.coinche.lang.Language;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BuyValue extends HBox {

    private int current_normal, normal_min,
            current_capot, capot_min;
    private final int normal_max = 550, capot_max = 300;
    private final JFXCheckBox capot;
    private final Label normal_value, capot_value;

    public BuyValue() {
        capot = new JFXCheckBox();
        capot.textProperty().bind(Language.CAPOT);
        capot.setMinSize(JFXCheckBox.USE_PREF_SIZE, JFXCheckBox.USE_PREF_SIZE);
        normal_min = 80;
        current_normal = normal_min;
        normal_value = new Label();
        normal_value.setText(String.valueOf(current_normal));
        TurnTriangle normal_add = new TurnTriangle(true);
        TurnTriangle normal_subtract = new TurnTriangle(false);

        normal_add.setOnAction(e -> {
            if (current_normal == normal_max) return;
            current_normal += 10;
            normal_value.setText(String.valueOf(current_normal));
        });
        normal_subtract.setOnAction(e -> {
            if (current_normal == normal_min) return;
            current_normal -= 10;
            normal_value.setText(String.valueOf(current_normal));
        });
        VBox normal_vb = new VBox();
        normal_vb.setAlignment(Pos.CENTER);
        normal_vb.spacingProperty().bind(MainApp.spacingProperty);
        normal_vb.getChildren().addAll(normal_add, normal_value, normal_subtract);
        normal_vb.visibleProperty().bind(capot.selectedProperty().not());

        capot_min = 0;
        current_capot = capot_min;
        capot_value = new Label();
        capot_value.setText(Language.CAPOT.getValue());
        TurnTriangle capot_add = new TurnTriangle(true);
        TurnTriangle capot_subtract = new TurnTriangle(false);

        capot_add.setOnAction(e -> {
            if (current_capot == capot_max) return;
            current_capot += 10;
            capot_value.setText(Language.CAPOT.getValue() + " + " + current_capot);
        });
        capot_subtract.setOnAction(e -> {
            if (current_capot == capot_min) return;
            current_capot -= 10;
            if (current_capot == 0) capot_value.setText(Language.CAPOT.getValue());
            else capot_value.setText(Language.CAPOT.getValue() + " + " + current_capot);
        });
        VBox capot_vb = new VBox();
        capot_vb.setAlignment(Pos.CENTER);
        capot_vb.spacingProperty().bind(MainApp.spacingProperty);
        capot_vb.getChildren().addAll(capot_add, capot_value, capot_subtract);
        capot_vb.visibleProperty().bind(capot.selectedProperty());

        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().addAll(normal_vb, capot_vb);

        setAlignment(Pos.CENTER);
        spacingProperty().bind(MainApp.spacingProperty);
        getChildren().addAll(sp, capot);
    }

    public void update(int new_min_value, boolean capot) {
        this.capot.setSelected(capot);
        if (capot) {
            this.capot.setDisable(true);
            current_capot = new_min_value;
            capot_min = new_min_value;
            if (new_min_value == 0) this.capot_value.setText(Language.CAPOT.getValue());
            else this.capot_value.setText(Language.CAPOT.getValue() + " + " + new_min_value);
        } else {
            if (new_min_value - 10 == normal_max) {
                this.capot.setSelected(true);
                this.capot.setDisable(true);
            } else {
                current_normal = new_min_value;
                normal_min = new_min_value;
                normal_value.setText(String.valueOf(new_min_value));
            }
        }
    }

    public boolean reached_max(int value) {
        return value == capot_max;
    }

    public void reset() {
        capot.setSelected(false);
        capot.setDisable(false);
        normal_min = 80;
        current_normal = normal_min;
        capot_min = 0;
        current_capot = capot_min;
        normal_value.setText(String.valueOf(80));
        capot_value.setText(Language.CAPOT.getValue());
    }

    public Object[] get_value() {
        return new Object[]{capot.isSelected() ? current_capot : current_normal,
                capot.isSelected()};
    }

    public void assert_min_atnt() {
        if (!capot.isSelected() && current_normal < 130) {
            normal_min = 130;
            current_normal = normal_min;
            normal_value.setText(String.valueOf(current_normal));
        }
    }
}
