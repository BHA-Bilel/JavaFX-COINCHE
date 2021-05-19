module MainModule {
    requires javafx.controls;
    requires com.jfoenix;
    requires org.controlsfx.controls;
    requires javafx.swing;

    opens bg.coinche.model to javafx.base;
    exports bg.coinche;
}