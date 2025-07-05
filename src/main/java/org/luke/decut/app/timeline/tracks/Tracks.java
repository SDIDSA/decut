package org.luke.decut.app.timeline.tracks;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.viewport.timeRuler.TimeRuler;
import org.luke.gui.controls.button.ColoredIconButton;
import org.luke.gui.controls.button.IconButton;
import org.luke.gui.controls.popup.Direction;
import org.luke.gui.controls.popup.context.ContextMenu;
import org.luke.gui.controls.popup.tooltip.TextTooltip;
import org.luke.gui.style.Style;
import org.luke.gui.threading.Platform;

public class Tracks extends VBox {
    private final TrackList trackList;

    public Tracks(Home owner) {
        HBox top = new HBox();
        top.setPadding(new Insets(2));
        top.setAlignment(Pos.TOP_RIGHT);

        int size = TimeRuler.HEIGHT - 4;
        IconButton add = new ColoredIconButton(owner.getWindow(),
                5, size,size,
                "add", 18,
                Style::getBackgroundTertiary, Style::getTextNormal);

        trackList = new TrackList(owner);

        Platform.runAfter(() -> trackList.addVideoTrackAt(0), 100);

        TextTooltip.install(add, Direction.UP, "Add new Track", 0, 15);

        ContextMenu addMenu = new ContextMenu(owner.getWindow(), 120);
        addMenu.addMenuItem("Effects Track", "effect", trackList::addEffectTrack);
        addMenu.addMenuItem("Video Track", "video-clip", trackList::addVideoTrack);
        addMenu.addMenuItem("Audio Track", "speaker", trackList::addAudioTrack);
        add.setAction(() -> addMenu.showPop(add, Direction.DOWN_LEFT, 0, 10));

        top.getChildren().add(add);

        StackPane preTrackList = new StackPane(trackList);
        preTrackList.setPadding(new Insets(5,0,5,5));
        VBox.setVgrow(preTrackList, Priority.ALWAYS);

        getChildren().addAll(top, preTrackList);
    }

    public TrackList getTrackList() {
        return trackList;
    }

    public ObservableList<Track> getTracks() {
        return trackList.getTracks();
    }
}
