package bg.coinche.gfx;

import bg.coinche.MainApp;
import bg.coinche.model.Card;
import bg.coinche.model.Combination;
import bg.coinche.model.Suit;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Assets {

    public static BufferedImage[][] cards = new BufferedImage[4][8];
    public static double height, width;

    public static void init_resolution() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        height = screenSize.getHeight();
        width = screenSize.getWidth();
    }

    public static void init_cards() {
        List<String> types = new ArrayList<>();
        types.add("H");
        types.add("S");
        types.add("D");
        types.add("C");
        List<String> names = new ArrayList<>();
        names.add("7");
        names.add("8");
        names.add("9");
        names.add("10");
        names.add("J");
        names.add("Q");
        names.add("K");
        names.add("A");
        int i = 0;
        int j = 0;
        for (String type : types) {
            for (String name : names) {
                cards[i][j] = ImageLoader.loadImage("/textures/" + name + type + ".png");
                j++;
            }
            j = 0;
            i++;
        }
    }

    public static void drop_cards() {
        for (BufferedImage[] card : cards) {
            Arrays.fill(card, null);
        }
    }

    public static ImageView getButtonSign(Suit suit) {
        Image image = null;
        switch (suit) {
            case Hearts: {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/heart.png"), null);
                break;
            }
            case Spades: {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/spade.png"), null);
                break;
            }
            case Diamonds: {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/diamond.png"), null);
                break;
            }
            case Clubs: {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/club.png"), null);
                break;
            }
        }
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(MainApp.turnSignProperty);
        return iv;
    }

    public static Image getSign(Suit suit) {
        switch (suit) {
            case Hearts:
                return SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/heart.png"), null);
            case Spades:
                return SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/spade.png"), null);
            case Diamonds:
                return SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/diamond.png"), null);
            case Clubs:
                return SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/club.png"), null);
            default:
                return null;
        }
    }

    public static ImageView getDealerChip() {
        Image image = SwingFXUtils.toFXImage(ImageLoader.loadImage(
                "/textures/dealer.png"), null);
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setPreserveRatio(true);
        iv.fitWidthProperty().bind(MainApp.dealerCchipProperty);
        return iv;
    }

    public static Image getCoincheChip(boolean coinche_only) {
        return SwingFXUtils.toFXImage(ImageLoader.loadImage(
                coinche_only ? "/textures/C.png" : "/textures/S.png"), null);
    }

    public static VBox getDeclaration(Combination comb) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.styleProperty().bind(Bindings.concat("-fx-padding: ", MainApp.paddingProperty.asString()));

        Label label = new Label(comb.getType().getNameProperty().getValue());
        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        HBox hbox = new HBox();
        hbox.spacingProperty().bind(MainApp.spacingProperty);
        hbox.styleProperty().bind(Bindings.concat("-fx-padding: ", MainApp.paddingProperty.asString()));
        hbox.setAlignment(Pos.CENTER);
        for (Card card : comb.getCards()) {
            ImageView iv = getCard(card);
            iv.fitWidthProperty().bind(MainApp.announcePlayProperty);
            hbox.getChildren().add(iv);
        }

        vbox.getChildren().addAll(label, hbox);
        return vbox;
    }

    public static ImageView getCard(Card card) {
        Image image = SwingFXUtils.toFXImage(
                cards[card.getSuit().ordinal()][card.getRank().ordinal()],
                null);
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setPreserveRatio(true);
        return iv;
    }
}