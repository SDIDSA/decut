package org.luke.decut.app.timeline;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.controls.TimelineControls;
import org.luke.decut.app.timeline.tracks.Tracks;
import org.luke.decut.app.timeline.viewport.Viewport;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.controls.text.keyed.ColoredKeyedText;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class TimelinePane extends VBox implements Styleable {
    private final TimelineControls controls;
    private final Timeline timeline;

    public TimelinePane(Home owner) {

        setAlignment(Pos.CENTER);

        controls = new TimelineControls(owner);

        timeline = new Timeline(owner);

        timeline.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        timeline.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        timeline.prefWidthProperty().bind(widthProperty());
        timeline.prefHeightProperty().bind(heightProperty().subtract(controls.heightProperty()).subtract(1));

        VBox.setVgrow(timeline, Priority.ALWAYS);

        getChildren().add(controls);
        getChildren().add(new Separator(owner.getWindow(), Orientation.HORIZONTAL));
        getChildren().add(timeline);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        applyStyle(owner.getWindow().getStyl());
    }

    public DoubleProperty timeScale() {
        return controls.timeScale();
    }

    public DoubleProperty timeScaleSource() {
        return controls.timeScaleSource();
    }

    public DoubleProperty atProperty() {
        return timeline.atProperty();
    }

    public Tracks getTracks() {
        return timeline.getTracks();
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public Viewport getViewPort() {
        return timeline.getViewPort();
    }

    public DoubleProperty ppsProperty() {
        return timeline.ppsProperty();
    }

    public void pausePlayback() {
        controls.pausePlayback();
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundTertiary(), new CornerRadii(2, 2,
                8, 8, false)));
    }
}
