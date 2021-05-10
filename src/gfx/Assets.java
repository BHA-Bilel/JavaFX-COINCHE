package gfx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import model.Card;
import model.Combination;
import model.Suit;
import shared.RoomPosition;

public class Assets {
    public static BufferedImage[][] cards = new BufferedImage[4][8];

    private static double width, height;
    public static double scale_width, scale_height, unscale_width, unscale_height;
    public static Scale scale;

    public static double mainApp_width = 0, mainApp_height = 0;
    public static double joinApp_width = 0, joinApp_height = 0;
    public static double roomApp_width = 0, roomApp_height = 0;
    public static double gameApp_width = 0, gameApp_height = 0;

    public static void init_scale() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.getWidth();
        height = screenSize.getHeight();
        scale_width = scale_width(1);
        scale_height = scale_height(1);
        unscale_width = unscale_width(1);
        unscale_height = unscale_height(1);
        scale = new Scale(scale_width, scale_height, 0, 0);
    }

    public static double scale_width(double old_width) {
        return old_width * (width / 1920.0);
    }

    public static double scale_height(double old_height) {
        return old_height * (height / 1080.0);
    }

    public static double unscale_width(double old_width) {
        return old_width * (1920.0 / width);
    }

    public static double unscale_height(double old_height) {
        return old_height * (1080.0 / height);
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
                cards[i][j] = ImageLoader.loadImage("/textures/V" + name + type + ".png");
                j++;
            }
            j = 0;
            i++;
        }
    }

    public static ImageView getSign(Suit suit) {
        Image image = null;
        switch (suit) {
            case Hearts : {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/H.png"), null);break;
            }
            case Spades : {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/S.png"), null);break;
            }
            case Diamonds : {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/D.png"), null);break;
            }
            case Clubs: {
                image = SwingFXUtils.toFXImage(ImageLoader.loadImage("/textures/C.png"), null);break;
            }
        }
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setPreserveRatio(true);
        iv.setFitWidth(30);
        return iv;
    }

    public static VBox getDeclaration(Combination comb) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        Text text = new Text(comb.getType().toString());
        text.setFont(Font.font(20));
        vbox.getChildren().add(text);
        VBox.setMargin(vbox, new Insets(0, 10, 0, 10));

        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER);
        for (Card card : comb.getCards()) {
            ImageView iv = getCard(card);
            hbox.getChildren().add(iv);
        }
        VBox.setMargin(hbox, new Insets(10, 0, 0, 0));
        vbox.getChildren().add(hbox);
        return vbox;
    }

    public static ImageView getCard(Card card) {
        Image image = SwingFXUtils.toFXImage(cards[card.getSuit().getIndex()][card.getRank().getIndex()], null);
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setPreserveRatio(true);
        iv.setFitHeight(200);
        return iv;
    }
}