package bg.coinche.model;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Map;

public class MyTimeLine {
    public Timeline timeLine;
    private final Map<Label, String> say_what;

    public MyTimeLine(Timeline timeLine) {
        this.timeLine = timeLine;
        say_what = new HashMap<>();
    }

    public void put_text(Label say, String what) {
        say_what.put(say, what);
    }


    public void add_text(Label say, String what) {
        say_what.put(say, say_what.get(say) + "\n" + what);
    }

    public void play() {
        Platform.runLater(() -> {
            for (Map.Entry<Label, String> SayWhatEntry : say_what.entrySet()) {
                Label say = SayWhatEntry.getKey();
                String what = SayWhatEntry.getValue();
                say.setText(what);
            }
            timeLine.play();
        });
    }
}