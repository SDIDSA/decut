package org.luke.decut.app.timeline.tracks;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.home.Resizer;
import org.luke.decut.dragdrop.*;
import org.luke.gui.controls.text.unkeyed.Label;
import org.luke.gui.factory.Backgrounds;

import java.util.ArrayList;
import java.util.Collections;

public class TrackDragHandler implements DragDropHandler {
    private final Track track;
    private final Home owner;
    private final VBox root;
    private final VBox drop;
    private final StackPane insertBefore;
    private final StackPane swap;
    private final StackPane insertAfter;
    private final Label dropLabel;
    private final DoubleProperty trackHeight;
    private final Resizer resizer;

    public TrackDragHandler(Track track, Home owner, VBox root, VBox drop,
                            StackPane insertBefore, StackPane swap, StackPane insertAfter,
                            Label dropLabel, DoubleProperty trackHeight, Resizer resizer) {
        this.track = track;
        this.owner = owner;
        this.root = root;
        this.drop = drop;
        this.insertBefore = insertBefore;
        this.swap = swap;
        this.insertAfter = insertAfter;
        this.dropLabel = dropLabel;
        this.trackHeight = trackHeight;
        this.resizer = resizer;
    }

    public void setupDragHandlers() {
        track.setOnDragDetected(this::onDragDetected);
        track.setOnDragEntered(this::onDragEntered);
        track.setOnDragExited(this::onDragExited);
        track.setOnDragOver(this::onDragOver);
        track.setOnDragDropped(this::onDragDropped);
        track.setOnDragDone(this::onDragDone);
    }

    @Override
    public void onDragDone(DragEvent event) {
        track.unpress();
        track.setOpacity(1);
    }

    @Override
    public void onDragDropped(DragEvent event) {
        root.setOpacity(1);
        track.getChildren().remove(drop);
        Dragboard db = event.getDragboard();
        boolean success = false;
        try {
            TrackDrag dc = new TrackDrag(owner, db);
            ObservableList<Track> tracks = owner.getTracks().getTracks();
            int draggedTrackIndex = dc.getIndex();
            int dropIndex = tracks.indexOf(track);
            Track draggedTrack = dc.getBody();
            double perc = event.getY() / trackHeight.get();
            ArrayList<Track> originalOrder = new ArrayList<>(tracks);
            if (perc <= 0.3) {
                int targetIndex;
                if (draggedTrackIndex < dropIndex) {
                    targetIndex = dropIndex - 1;
                } else {
                    targetIndex = dropIndex;
                }
                owner.perform("reorder tracks",
                        () -> {
                            tracks.remove(draggedTrackIndex);
                            tracks.add(targetIndex, draggedTrack);
                        }, () -> {
                            tracks.setAll(originalOrder);
                        });
                success = true;
            } else if (perc < 0.7) {
                if (draggedTrackIndex != dropIndex) {
                    ArrayList<Track> newOrder = new ArrayList<>(tracks);
                    Collections.swap(newOrder, draggedTrackIndex, dropIndex);
                    owner.perform("reorder tracks",
                            () -> {
                                tracks.setAll(newOrder);
                            }, () -> {
                                tracks.setAll(originalOrder);
                            });
                    success = true;
                }
            } else {
                int targetIndex;
                if (draggedTrackIndex < dropIndex) {
                    targetIndex = dropIndex;
                } else {
                    targetIndex = dropIndex + 1;
                }
                owner.perform("reorder tracks",
                        () -> {
                            tracks.remove(draggedTrackIndex);
                            tracks.add(targetIndex, draggedTrack);
                        },
                        () -> tracks.setAll(originalOrder));
                success = true;
            }
        } catch (DragContentException x) {
            //Wrong drag type
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @Override
    public void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getGestureSource() == this) return;
        event.acceptTransferModes(TransferMode.MOVE);
        try {
            new TrackDrag(owner, db);
            double perc = event.getY() / trackHeight.get();
            insertBefore.setBackground(null);
            swap.setBackground(null);
            insertAfter.setBackground(null);
            Color selected = owner.getWindow().getStyl().get().getBackgroundModifierSelected();
            if (perc <= 0.3) {
                insertBefore.setBackground(Backgrounds.make(selected, 5));
                dropLabel.setText("Drop to insert before");
            } else if (perc < 0.7) {
                swap.setBackground(Backgrounds.make(selected, 5));
                dropLabel.setText("Drop to swap order");
            } else {
                insertAfter.setBackground(Backgrounds.make(selected, 5));
                dropLabel.setText("Drop to insert after");
            }
            event.consume();
        } catch (DragContentException _) {
            //Wrong drag type
        }
    }

    @Override
    public void onDragExited(DragEvent event) {
        root.setOpacity(1);
        track.getChildren().remove(drop);
    }

    @Override
    public void onDragEntered(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (event.getGestureSource() == this) return;
        try {
            new TrackDrag(owner, db);
            root.setOpacity(0.2);
            track.getChildren().add(drop);
        } catch (DragContentException _) {
            //Wrong drag type
        }
    }

    @Override
    public void onDragDetected(MouseEvent event) {
        if (!resizer.isPressed()) {
            Dragboard db = track.startDragAndDrop(TransferMode.MOVE);

            WritableImage snapshot = track.snapshot(null, null);
            db.setDragView(snapshot);

            TrackDrag dc = new TrackDrag(owner, track);
            dc.putContent(db);

            track.setOpacity(0.2);
            event.consume();
        }
    }

}
