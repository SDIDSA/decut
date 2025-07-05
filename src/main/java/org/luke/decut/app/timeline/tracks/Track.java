package org.luke.decut.app.timeline.tracks;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.home.Resizer;
import org.luke.decut.app.timeline.clips.LinkedClips;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.viewport.content.TrackContent;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.alert.Alert;
import org.luke.gui.controls.alert.AlertType;
import org.luke.gui.controls.alert.ButtonType;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.controls.space.ExpandingVSpace;
import org.luke.gui.controls.text.unkeyed.Label;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.threading.Platform;

import java.util.HashMap;

public class Track extends StackPane implements Styleable {
    private final DoubleProperty trackHeight;
    private final Label label;
    private final Label dropLabel;

    private final TrackType type;

    private final StringProperty titleProperty;
    private final TrackContent content;

    private final BooleanProperty muted;

    private double initHeight;
    private double initScroll;

    public Track(Home owner, TrackType trackType) {
        this.type = trackType;
        setAlignment(Pos.CENTER);

        titleProperty = new SimpleStringProperty("");

        muted = new SimpleBooleanProperty(false);

        trackHeight = new SimpleDoubleProperty(60);
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        prefHeightProperty().bind(trackHeight);

        label = new Label("", new Font(12));
        dropLabel = new Label("", new Font(12));
        StackPane insertBefore = new StackPane();
        StackPane swap = new StackPane();
        swap.getChildren().add(dropLabel);
        StackPane insertAfter = new StackPane();

        VBox.setVgrow(insertBefore, Priority.ALWAYS);
        VBox.setVgrow(swap, Priority.ALWAYS);
        VBox.setVgrow(insertAfter, Priority.ALWAYS);

        VBox drop = new VBox(
                insertBefore,
                swap,
                insertAfter);
        VBox.setVgrow(drop, Priority.ALWAYS);
        drop.setAlignment(Pos.CENTER);

        HBox top = new HBox();
        top.setSpacing(10);

        ColoredIcon type = new ColoredIcon(owner.getWindow(), trackType.getIcon(), 14, Style::getTextNormal);

        top.getChildren().addAll(type, label);

        HBox controls = new HBox();

        TrackButton mute = new TrackButton(owner.getWindow(), "mute", "Mute Track");
        TrackButton delete = new TrackButton(owner.getWindow(), "trash", "Remove Track");

        controls.getChildren().addAll(new ExpandingHSpace(), mute, delete);

        ExpandingVSpace ev = new ExpandingVSpace();

        Resizer resizer = setupResizer(owner);

        VBox root = new VBox(top, ev, controls, resizer);
        root.setPadding(new Insets(8, 8, 0, 8));
        VBox.setVgrow(root, Priority.ALWAYS);

        getChildren().setAll(root);

        this.content = new TrackContent(owner, this);

        Platform.runLater(() -> {
            label.textProperty().bind(Bindings.createStringBinding(() -> {
                String tit = titleProperty.get();
                int index = owner.getTracks().getTracks().indexOf(this);
                if (index == -1) return "";
                return tit.isBlank() ? ("Track " + index) : tit;
            }, titleProperty, owner.getTracks().getTracks()));
        });

        mute.setAction(() -> {
            muted.set(!muted.get());
            mute.setTextFill(muted.get() ? Style::getTextDanger : Style::getTextNormal);
        });

        delete.setAction(() -> {
            HashMap<TimelineClip, Track> clipsToRemove = new HashMap<>();
            for (TimelineClip clip : content.getClips()) {
                clipsToRemove.put(clip, clip.getTrack());
                LinkedClips group = clip.getLinkedGroup();
                if(group != null) {
                    for (TimelineClip otherClip : group.getOtherClips(clip)) {
                        clipsToRemove.put(otherClip, otherClip.getTrack());
                    }
                }
            }
            int index = owner.getTracks().getTracks().indexOf(this);
            Runnable perform = () -> {
                clipsToRemove.forEach((clip, track) -> {
                    track.getContent().getClips().remove(clip);
                });
                owner.getTracks().getTracks().remove(this);
            };
            Runnable undo = () -> {
                owner.getTracks().getTrackList().addTrackAt(this, index);
                clipsToRemove.forEach((clip, track) -> {
                    track.getContent().getClips().add(clip);
                });
            };
            if(!clipsToRemove.isEmpty()) {
                Alert alert = new Alert(owner, AlertType.DELETE);
                alert.setHead("Delete track");
                alert.addLabel("The track you want to delete is not empty, delete anyway?");
                alert.addAction(ButtonType.DELETE, () -> {
                    owner.perform("Remove track", perform, undo);
                    alert.hide();
                });
                alert.show();
            } else {
                owner.perform("Remove track", perform, undo);
            }
        });
        new TrackDragHandler(this, owner, root, drop,
                insertBefore, swap, insertAfter,
                dropLabel, trackHeight, resizer).setupDragHandlers();
        applyStyle(owner.getWindow().getStyl());
    }

    public void unpress() {
        setPressed(false);
    }

    public TrackType getType() {
        return type;
    }

    private Resizer setupResizer(Home owner) {
        Resizer resizer = new Resizer(owner, Orientation.HORIZONTAL, 8);
        resizer.setOnInit(() -> {
            initHeight = trackHeight.get();
            initScroll = owner.getTracks().getTrackList().getScrollY();
        });
        resizer.setOnDrag((byPx) -> {
            double newVal = initHeight + byPx;
            newVal = Math.min(160, Math.max(56, newVal));
            trackHeight.set(newVal);
            double dif = newVal - initHeight;
            if (dif != 0) {
                owner.getTracks().getTrackList().setScrollY(initScroll);
            }

        });
        resizer.setFill(Style::getNothing);
        return resizer;
    }

    public TrackContent getContent() {
        return content;
    }

    public DoubleProperty trackHeightProperty() {
        return trackHeight;
    }

    @Override
    public void applyStyle(Style style) {
        label.setFill(style.getTextNormal());
        dropLabel.setFill(style.getTextNormal());
        backgroundProperty().bind(Bindings.when(pressedProperty()).then(
                Backgrounds.make(style.getBackgroundModifierActive(), 5)
        ).otherwise(Bindings.when(hoverProperty()).then(
                Backgrounds.make(style.getBackgroundModifierHover(), 5)
        ).otherwise(Background.EMPTY)));
    }
}
