package org.luke.decut.app.timeline.controls;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import org.luke.decut.app.home.Home;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.threading.Platform;

import java.time.Duration;

public class PlaybackControls extends HBox {
    private final Home owner;
    private final TimelineButton play;
    private boolean playing = false;

    public PlaybackControls(Home owner) {
        this.owner = owner;
        setAlignment(Pos.CENTER);
        setSpacing(10);

        play = new TimelineButton(owner.getWindow(),
                "play", "Play / Pause");

        play.setAction(() -> {
            if (playing) {
                owner.pausePreview();
                play.setIcon("play");
            } else {
                owner.playPreview();
                play.setIcon("pause");
            }
            playing = !playing;
        });

        DurationLabel at = new DurationLabel(owner.getWindow());
        DurationLabel total = new DurationLabel(owner.getWindow());

        at.setDuration(Duration.ofSeconds(346));
        total.setDuration(Duration.ofSeconds(1547));

        Platform.runLater(() -> {
            owner.atProperty().addListener((_, _, nv) -> {
                at.setDuration(Duration.ofMillis((long) (nv.doubleValue() * 1000)));
            });
            at.setDuration(Duration.ofMillis((long) (owner.atProperty().doubleValue() * 1000)));

            owner.durationProperty().addListener((_, _, nv) -> {
                total.setDuration(Duration.ofMillis((long) (nv.doubleValue() * 1000)));
            });
            total.setDuration(Duration.ofMillis((long) (owner.durationProperty().doubleValue() * 1000)));
        });

        getChildren().addAll(play, at, new Separator(owner.getWindow(), Orientation.VERTICAL), total);
    }

    public void pausePlayback() {
        owner.pausePreview();
        play.setIcon("play");
        playing = false;
    }
}
