package org.luke.decut.app.timeline.tracks;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.luke.decut.app.home.Home;
import org.luke.gui.controls.scroll.VerticalScrollable;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class TrackList extends VerticalScrollable implements Styleable {
    private final Home owner;

    private final ObservableList<Track> tracks;

    private final VBox root;

    public TrackList(Home owner) {
        this.owner = owner;

        getScrollBar().setShow(false);

        root = new VBox();

        StackPane.setAlignment(root, Pos.TOP_CENTER);

        tracks = FXCollections.observableArrayList();

        tracks.addListener((ListChangeListener<? super Track>) c -> {
            root.getChildren().setAll(c.getList());
        });

        setContent(root);

        VBox.setVgrow(this, Priority.ALWAYS);

        applyStyle(owner.getWindow().getStyl());
    }

    public void addVideoTrack() {
        addTrack(TrackType.VIDEO);
    }

    public void addAudioTrack() {
        addTrack(TrackType.AUDIO);
    }

    public void addEffectTrack() {
        addTrack(TrackType.EFFECT);
    }

    public Track addTrack(TrackType type) {
        Track track = new Track(owner, type);
        owner.perform("add track",
                () -> tracks.add(track),
                () -> tracks.remove(track));
        return track;
    }

    public Track addAudioTrackAt(int index) {
        Track res = new Track(owner, TrackType.AUDIO);
        tracks.add(index, res);
        return res;
    }

    public Track addVideoTrackAt(int index) {
        Track res = new Track(owner, TrackType.VIDEO);
        tracks.add(index, res);
        return res;
    }

    public Track addTrackAt(Track track, int index) {
        tracks.add(index, track);
        return track;
    }

    public ObservableList<Track> getTracks() {
        return tracks;
    }

    @Override
    public void applyStyle(Style style) {
        getScrollBar().setThumbFill(style.getTextMuted());
    }
}
