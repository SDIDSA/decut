package org.luke.decut.app.timeline.clips;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.decut.dragdrop.ClipDrag;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.factory.Borders;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

public class TimelineClip extends Pane implements Styleable {
    private final AssetData sourceAsset;
    private final Track track;
    private final Home owner;

    private DoubleProperty startTime;
    private double duration;

    protected final DoubleProperty inPoint;
    protected final DoubleProperty outPoint;

    private double timeshift = 0;

    private LinkedClips linkedGroup;

    private boolean resizeOut = false;
    private boolean resizeIn = false;
    private double initX;
    private double initStart;
    private double initOut;
    private double initIn;

    public TimelineClip(Home owner, Track track, AssetData sourceAsset, double startTime) {
        this.owner = owner;
        this.track = track;
        this.sourceAsset = sourceAsset;
        this.startTime = new SimpleDoubleProperty(owner.snapToFrame(startTime));
        this.duration = owner.snapToFrame(sourceAsset.getDurationSeconds());
        this.inPoint = new SimpleDoubleProperty(0);
        this.outPoint = new SimpleDoubleProperty(duration);

        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        prefHeightProperty().bind(track.trackHeightProperty().subtract(6));
        setLayoutY(3);

        setOnMouseMoved(event -> {
            if (isPressed()) return;
            double mx = event.getX();
            if (mx < getWidth() && mx > getWidth() - 12) {
                setCursor(Cursor.W_RESIZE);
                resizeOut = true;
            } else if (mx >= 0 && mx < 12) {
                setCursor(Cursor.E_RESIZE);
                resizeIn = true;
            } else {
                setCursor(Cursor.DEFAULT);
                resizeOut = false;
                resizeIn = false;
            }
        });

        setOnMousePressed(event -> {
            if (resizeOut || resizeIn) {
                initX = event.getSceneX();
                initOut = outPoint.get();
                initIn = inPoint.get();
                initStart = this.startTime.get();
            }
        });

        setOnMouseDragged(event -> {
            double mx = event.getSceneX();
            double dx = mx - initX;
            double dt = owner.pixelToTime(dx);
            if (resizeOut) {
                double newOut = Math.max(
                        Math.min(
                                owner.snapDrag(initOut + dt),
                                sourceAsset.getDurationSeconds()),
                        inPoint.get());
                setOutPoint(newOut);
            } else if (resizeIn) {
                double newIn = Math.max(Math.min(owner.snapDrag(initIn + dt), outPoint.get()), 0);
                double inPointDelta = newIn - initIn;
                double newStartTime = initStart + inPointDelta;
                setInPointAndStartTime(newIn, newStartTime);
            }
        });

        setOnMouseReleased(_ -> {
            if (resizeOut) {
                double newOut = outPoint.get();
                double oldOut = initOut;
                owner.perform("Trim clip",
                        () -> setOutPoint(newOut),
                        () -> setOutPoint(oldOut));
            }

            if (resizeIn) {
                double newIn = inPoint.get();
                double newStart = this.startTime.get();
                double oldIn = initIn;
                double oldStart = initStart;
                owner.perform("Trim clip",
                        () -> setInPointAndStartTime(newIn, newStart),
                        () -> setInPointAndStartTime(oldIn, oldStart));
            }
        });

        setOnDragDetected(event -> {
            if (resizeIn || resizeOut) return;

            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            WritableImage snapshot = snapshot(null, null);
            db.setDragView(snapshot);

            ClipDrag dc = new ClipDrag(owner, this);
            dc.putContent(db);

            setGroupOpacity(0.5);
            event.consume();
        });

        setOnDragDone(e -> {
            setGroupOpacity(1);
        });

        updateUIPosition();

        owner.ppsProperty().addListener(_ -> updateUIPosition());

        applyStyle(owner.getWindow().getStyl());
    }

    private void setGroupOpacity(double opacity) {
        if (linkedGroup == null) {
            setOpacity(opacity);
        } else {
            for (TimelineClip clip : linkedGroup.getClips()) {
                clip.setOpacity(opacity);
            }
        }
    }

    private void updateUIPosition() {
        double pixelsPerSecond = owner.ppsProperty().get();
        setLayoutX((startTime.get() + timeshift) * pixelsPerSecond);
        setPrefWidth(duration * pixelsPerSecond);
    }

    private void setInPointAndStartTime(double newInPoint, double newStartTime) {
        setInPointAndStartTimeInternal(newInPoint, newStartTime);

        if (linkedGroup != null) {
            linkedGroup.updateInPointAndStartTime(this, newInPoint, newStartTime);
        }
    }

    void setInPointAndStartTimeInternal(double newInPoint, double newStartTime) {
        this.inPoint.set(newInPoint);
        this.startTime.set(newStartTime);
        this.duration = outPoint.get() - newInPoint;
        updateUIPosition();
    }

    // Getters
    public Home getOwner() {
        return owner;
    }

    public AssetData getSourceAsset() {
        return sourceAsset;
    }

    public Track getTrack() {
        return track;
    }

    public double getStartTime() {
        return startTime.get();
    }

    // Setters that handle linked clips
    public void setStartTime(double startTime) {
        if (linkedGroup != null) {
            linkedGroup.updateStartTime(this, startTime);
        }
        setStartTimeInternal(startTime);
    }

    public double getEndTime() {
        return startTime.get() + duration;
    }

    public double getDuration() {
        return duration;
    }

    private void setDuration(double duration) {
        this.duration = duration;
        updateUIPosition();
    }

    public double getInPoint() {
        return inPoint.get();
    }

    public void setInPoint(double inPoint) {
        double newOutPoint = this.outPoint.get();
        setInPointInternal(inPoint);

        if (linkedGroup != null) {
            linkedGroup.updateInOutPoints(this, inPoint, newOutPoint);
        }
    }

    public double getTimeshift() {
        return timeshift;
    }

    // Setters that handle linked clips
    public void setTimeshift(double timeshift) {
        if (linkedGroup != null) {
            linkedGroup.updateTimeshift(this, timeshift);
        }
        setTimeshiftInternal(timeshift);
    }

    public double getOutPoint() {
        return outPoint.get();
    }

    public void setOutPoint(double outPoint) {
        double delta = this.outPoint.get() - outPoint;
        setOutPointInternal(outPoint);

        if (linkedGroup != null) {
            linkedGroup.updateInOutPoints(this, 0, delta);
        }
    }

    public LinkedClips getLinkedGroup() {
        return linkedGroup;
    }

    // Linked group management
    public void setLinkedGroup(LinkedClips group) {
        this.linkedGroup = group;
    }

    // Internal setter that doesn't trigger linked updates
    void setStartTimeInternal(double startTime) {
        this.startTime.set(startTime);
        updateUIPosition();
    }

    public void setTimeshiftInternal(double timeshift) {
        this.timeshift = timeshift;
        updateUIPosition();
    }

    // Internal setter that doesn't trigger linked updates
    void setInPointInternal(double inPoint) {
        this.inPoint.set(inPoint);
        setDuration(outPoint.get() - inPoint);
    }

    // Internal setter that doesn't trigger linked updates
    void setOutPointInternal(double outPoint) {
        this.outPoint.set(outPoint);
        setDuration(outPoint - inPoint.get());
    }

    public boolean isLinked() {
        return linkedGroup != null && linkedGroup.size() > 1;
    }

    public void unlinkFromGroup() {
        if (linkedGroup != null) {
            linkedGroup.removeClip(this);
        }
    }

    @Override
    public void applyStyle(Style style) {
        // Different styling for linked clips
        if (isLinked()) {
            setBorder(Borders.make(style.getAccent(), 5, 1));
            setBackground(Backgrounds.make(style.getBackgroundFloatingOr().deriveColor(0, 1, 1, 0.8), 5));
        } else {
            setBorder(Borders.make(style.getAccent(), 5, 1));
            setBackground(Backgrounds.make(style.getBackgroundFloatingOr(), 5));
        }
    }
}