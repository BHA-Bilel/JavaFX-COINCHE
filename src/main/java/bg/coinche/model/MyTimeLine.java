package bg.coinche.model;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class MyTimeLine {
    public Timeline timeLine;
    private final Map<Text, String> say_what;

    public MyTimeLine(Timeline timeLine) {
        this.timeLine = timeLine;
        say_what = new HashMap<>();
    }

    public void put_text(Text say, String what) {
        say_what.put(say, what);
    }


    public void add_text(Text say, String what) {
        say_what.put(say, say_what.get(say) + "\n" + what);
    }

    public void play() {
        Platform.runLater(() -> {
            for (Map.Entry<Text, String> SayWhatEntry : say_what.entrySet()) {
                Text say = SayWhatEntry.getKey();
                String what = SayWhatEntry.getValue();
                say.setText(what);
            }
            timeLine.play();
        });
    }
}