package org.luke.decut.app.timeline.controls;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.controls.snap.SnapControl;
import org.luke.gui.controls.space.ExpandingHSpace;

public class TimelineControls extends HBox {

    private final ZoomControls zoom;

    public TimelineControls(Home owner) {
        setSpacing(5);
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER);

        TimelineButton crop = new TimelineButton(owner.getWindow(),
                "crop", "Canvas size");

        TimelineButton frame = new TimelineButton(owner.getWindow(),
                "frame", "Full screen");
        SnapControl snap = new SnapControl(owner);

        zoom = new ZoomControls(owner);

        PlaybackControls play = new PlaybackControls(owner);

        TimelineButton delete = new TimelineButton(owner.getWindow(),
                "trash", "Remove clip");


        getChildren().addAll(crop, frame, snap, new ExpandingHSpace(), play, new ExpandingHSpace(), zoom, delete);
    }

    public DoubleProperty timeScale() {
        return zoom.timeScale();
    }

    public DoubleProperty timeScaleSource() {
        return zoom.source();
    }

}
