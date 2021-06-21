module MainModule {
    requires javafx.controls;
    requires com.jfoenix;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires javafx.swing;

    exports shared;
    exports bg.coinche.server.local;
    exports bg.coinche.server.room;
    exports bg.coinche.lang;
    exports bg.coinche.room;
    exports bg.coinche.game;
    exports bg.coinche.model;
    exports bg.coinche.gfx;
    opens bg.coinche.model to javafx.base;
    exports bg.coinche;
}