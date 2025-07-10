package org.luke.decut.app.timeline.viewport.content;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.lib.assets.data.AudioAssetData;
import org.luke.decut.app.lib.assets.data.VideoAssetData;
import org.luke.decut.app.timeline.clips.AudioClip;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.clips.VideoClip;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.text.unkeyed.Label;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.threading.Platform;

import java.util.Comparator;

public class TrackContent extends Pane implements Styleable {
    private final Home owner;
    private final Track track;
    private final ObservableList<TimelineClip> clips;
    private final SortedList<TimelineClip> sortedClips;

    private final Label label;

    private ClipPreDrop pd;

    private final AssetDragHandler assetDragHandler;

    public TrackContent(Home owner, Track track) {
        this.owner = owner;
        this.track = track;
        clips = FXCollections.observableArrayList();
        sortedClips = new SortedList<>(clips, Comparator.comparing(TimelineClip::getStartTime));

        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        prefHeightProperty().bind(track.trackHeightProperty());
        prefWidthProperty().bind(owner.durationProperty().multiply(owner.ppsProperty()));
        translateXProperty().bind(owner.getViewPort().preProperty());

        label = new Label("Drop assets here to add as clips...", new Font(12));

        Platform.runLater(() -> {
            label.layoutXProperty().bind(Bindings.createDoubleBinding(
                    () -> owner.getViewPort().getScrollX() + 10,
                    owner.getViewPort().scrollXProperty()));

            label.layoutYProperty().bind(Bindings.createDoubleBinding(
                    () -> track.trackHeightProperty().get() / 2 - label.getHeight() / 2,
                    track.trackHeightProperty(), label.heightProperty()));
        });

        clips.addListener((InvalidationListener) _ -> {
            if (clips.isEmpty()) {
                getChildren().setAll(label);
            } else {
                getChildren().setAll(clips);
            }
        });

        assetDragHandler = new AssetDragHandler(owner, track, this);

        setOnDragEntered(event -> {
            assetDragHandler.onDragEntered(event);
        });

        setOnDragExited(event -> {
            assetDragHandler.onDragExited(event);
        });

        setOnDragOver(event -> {
            assetDragHandler.onDragOver(event);
        });

        setOnDragDropped(event -> {
            assetDragHandler.onDragDropped(event);
        });

        getChildren().add(label);

        applyStyle(owner.getWindow().getStyl());
    }

    public TimelineClip addAsset(AssetData asset, double at) {
        TimelineClip newClip = createClip(asset, at);
        clips.add(newClip);
        return newClip;
    }

    public void addClip(TimelineClip clip) {
        clips.add(clip);
    }

    public TimelineClip createClip(AssetData asset, double at) {
        TimelineClip newClip = switch (asset.getType()) {
            case AUDIO -> new AudioClip(owner, track, (AudioAssetData) asset, at);
            case VIDEO -> new VideoClip(owner, track, (VideoAssetData) asset, at);
            default ->
                // For images or any other asset type, use the default TimelineClip.
                // You could also create a dedicated ImageClip class if needed.
                    new TimelineClip(owner, track, asset, at);
        };
        return newClip;
    }

    public ObservableList<TimelineClip> getClips() {
        return clips;
    }

    public SortedList<TimelineClip> getSortedClips() {
        Comparator<? super TimelineClip> comp = sortedClips.getComparator();
        sortedClips.setComparator(null);
        sortedClips.setComparator(comp);
        return sortedClips;
    }

    public Track getTrack() {
        return track;
    }

    @Override
    public void applyStyle(Style style) {
        label.setFill(style.getTextNormal());
        setBackground(Backgrounds.make(style.getBackgroundModifierHover(), 5, new Insets(3, 0, 3, 0)));
    }
}