package org.luke.decut.app.timeline.viewport.content;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.timeline.tracks.Track;

public class ClipPreDrop extends Rectangle {
    public ClipPreDrop(Home owner, Track track, AssetData clip) {
        setHeight(track.trackHeightProperty().get() - 6);
        setWidth(clip.getDurationSeconds() * owner.ppsProperty().get());
        setArcHeight(10);
        setArcWidth(10);

        setLayoutY(3);

        Color color = owner.getWindow().getStyl().get().getAccent();
        setFill(color);

        setOpacity(0.4);
    }

    public void setTrack(Track track) {
        setHeight(track.trackHeightProperty().get() - 6);
    }
}
