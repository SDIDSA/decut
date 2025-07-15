package org.luke.decut.app.timeline.clips;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.factory.Borders;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

import java.util.*;
import java.util.stream.Collectors;

public class TimelineClip extends Pane implements Styleable {
    private final AssetData sourceAsset;
    private final Track track;
    private final Home owner;

    private final DoubleProperty startTime;
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
    private HashMap<TimelineClip, Double> initStartTimes;
    private HashMap<TimelineClip, Double> currentStartTimes;
    private List<TimelineClip> initOrder;

    public TimelineClip(Home owner, Track track, AssetData sourceAsset, double prestart) {
        this.owner = owner;
        this.track = track;
        this.sourceAsset = sourceAsset;
        this.startTime = new SimpleDoubleProperty(owner.snapToFrame(prestart));
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
            focus();
            initX = event.getSceneX();
            initOut = outPoint.get();
            initIn = inPoint.get();
            initStart = this.startTime.get();
            initStartTimes = saveStartTimes();
            currentStartTimes = new HashMap<>(initStartTimes);
            initOrder = initStartTimes.keySet().stream().sorted(Comparator.comparing(TimelineClip::getStartTime)).toList();
        });

        setOnMouseDragged(event -> {
            double mx = event.getSceneX();
            double dx = mx - initX;
            double dt = owner.pixelToTimeNoSnap(dx);
            if (resizeOut) {
                double newOut = initOut + dt;
                double timePos = owner.snapDrag(startTime.get() + newOut - inPoint.get());
                newOut = timePos - startTime.get() + inPoint.get();
                newOut = Math.max(
                        Math.min(
                                newOut,
                                sourceAsset.getDurationSeconds()),
                        inPoint.get());
                if (newOut == outPoint.get()) return;
                setOutPoint(newOut);
                preResolve();
            } else if (resizeIn) {
                double newStartTime = initStart + dt;
                double snappedStartTime = owner.snapDrag(newStartTime);
                double startTimeDelta = Math.max(0, snappedStartTime) - initStart;
                double newIn = initIn + startTimeDelta;
                newIn = Math.max(
                        Math.min(newIn, outPoint.get()),
                        0);
                if (newIn == inPoint.get()) return;
                setInPointAndStartTime(newIn, snappedStartTime);
                preResolve();
            } else {
                double newStartTime = Math.max(0, owner.snapDrag(initStart + dt));
                if (newStartTime != this.startTime.get()) {
                    setStartTime(newStartTime);
                    preResolve();
                }
            }
        });

        setOnMouseReleased(_ -> {
            unfocus();
            if (outPoint.get() != initOut) {
                double newOut = outPoint.get();
                HashMap<TimelineClip, Double> newStarts = saveStartTimes();
                double oldOut = initOut;
                HashMap<TimelineClip, Double> oldStarts = new HashMap<>(initStartTimes);
                owner.perform("Trim clip",
                        () -> {
                            setOutPoint(newOut);
                            applyStartTimes(newStarts);
                        },
                        () -> {
                            setOutPoint(oldOut);
                            applyStartTimes(oldStarts);
                        });
            } else if (inPoint.get() != initIn) {
                double newIn = inPoint.get();
                HashMap<TimelineClip, Double> newStarts = saveStartTimes();
                double oldIn = initIn;
                HashMap<TimelineClip, Double> oldStarts = new HashMap<>(initStartTimes);
                owner.perform("Trim clip",
                        () -> {
                            setInPoint(newIn);
                            applyStartTimes(newStarts);
                        },
                        () -> {
                            setInPoint(oldIn);
                            applyStartTimes(oldStarts);
                        });
            } else if (startTime.get() != initStart) {
                HashMap<TimelineClip, Double> newStarts = saveStartTimes();
                HashMap<TimelineClip, Double> oldStarts = new HashMap<>(initStartTimes);
                owner.perform("Move clip",
                        () -> applyStartTimes(newStarts),
                        () -> applyStartTimes(oldStarts));
            }
        });

        updateUIPosition();

        owner.ppsProperty().addListener(_ -> updateUIPosition());

        applyStyle(owner.getWindow().getStyl());
    }

    public void focus() {
        if(isLinked()) {
            linkedGroup.getClips().forEach(TimelineClip::focusInternal);
        }else {
            focusInternal();
        }
    }

    public void unfocus() {
        if(isLinked()) {
            linkedGroup.getClips().forEach(TimelineClip::unfocusInternal);
        }else {
            unfocusInternal();
        }
    }

    public void focusInternal() {
        toFront();
        setEffect(new DropShadow(15, Color.gray(1, 0.4)));
    }

    public void unfocusInternal() {
        setEffect(null);
    }

    public void preResolve() {
        resetTimes();
        resolveCollision();
    }

    public void resolveCollision() {
        Set<Track> tracks = currentStartTimes.keySet().stream().map(TimelineClip::getTrack).collect(Collectors.toSet());
        for (Track track : tracks) {
            if (resolveCollision(track)) {
                resolveCollision();
                return;
            }
        }
    }

    public boolean resolveCollision(Track track) {
        List<TimelineClip> clips = track.getContent().getClips().sorted(
                Comparator.comparing(TimelineClip::getStartTime)
                        .thenComparing((c1, c2) ->
                                (isThis(c1) && !isThis(c2)) ? -1 : (isThis(c2) && !isThis(c1)) ? 1 : 0)
                        .thenComparing(initOrder::indexOf));

        for (int i = 0; i < clips.size() - 1; i++) {
            TimelineClip clip = clips.get(i);
            TimelineClip next = clips.get(i + 1);
            if (owner.timeToFrame(clip.getEndTime()) > owner.timeToFrame(next.getStartTime())) {
                next.setStartTime(clip.getEndTime());
                return true;
            }
        }
        return false;
    }

    public boolean isThis(TimelineClip clip) {
        return clip == this || (clip.isLinked() && clip.getLinkedGroup() == linkedGroup);
    }

    private HashMap<TimelineClip, Double> saveStartTimes() {
        HashMap<TimelineClip, Double> res = new HashMap<>();
        HashSet<Track> tracks = new HashSet<>();
        if (isLinked()) {
            linkedGroup.getClips().forEach(c -> tracks.add(c.getTrack()));
        } else {
            tracks.add(track);
        }
        tracks.forEach(t -> {
            t.getContent().getClips().forEach(c -> {
                if (c.isLinked()) {
                    c.linkedGroup.getClips().forEach(lc -> tracks.add(lc.getTrack()));
                }
            });
        });
        tracks.forEach(track -> {
            track.getContent().getClips().forEach(clip -> {
                res.put(clip, clip.getStartTime());
            });
        });
        return res;
    }

    private void resetTimes() {
        initStartTimes.forEach((clip, start) -> {
            if (clip == this || (isLinked() && linkedGroup.getClips().contains(clip))) {
                return;
            }
            clip.setStartTime(start);
        });
    }

    private void applyStartTimes(HashMap<TimelineClip, Double> times) {
        times.forEach(TimelineClip::setStartTime);
    }

    private void updateUIPosition() {
        double pixelsPerSecond = owner.ppsProperty().get();
        setLayoutX((startTime.get() + timeshift) * pixelsPerSecond);
        setPrefWidth(duration * pixelsPerSecond);
    }

    private void setInPointAndStartTime(double newInPoint, double newStartTime) {
        if (linkedGroup != null) {
            linkedGroup.updateInPointAndStartTime(newInPoint, newStartTime);
        } else {
            setInPointAndStartTimeInternal(newInPoint, newStartTime);
        }
    }

    void setInPointAndStartTimeInternal(double newInPoint, double newStartTime) {
        this.inPoint.set(owner.snapToFrame(newInPoint));
        this.startTime.set(owner.snapToFrame(newStartTime));
        this.duration = outPoint.get() - inPoint.get();
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

    public void setStartTime(double startTime) {
        if (linkedGroup != null) {
            linkedGroup.updateStartTime(startTime);
        } else {
            setStartTimeInternal(startTime);
        }
    }

    public double getEndTime() {
        return startTime.get() + duration;
    }

    public double getDuration() {
        return duration;
    }

    private void setDuration(double duration) {
        this.duration = owner.snapToFrame(duration);
        updateUIPosition();
    }

    public double getInPoint() {
        return inPoint.get();
    }

    public void setInPoint(double inPoint) {
        if (linkedGroup != null) {
            linkedGroup.setInPoint(inPoint);
        } else {
            setInPointInternal(inPoint);
        }
    }

    public double getTimeshift() {
        return timeshift;
    }

    public void setTimeshift(double timeshift) {
        if (linkedGroup != null) {
            linkedGroup.updateTimeshift(timeshift);
        } else {
            setTimeshiftInternal(timeshift);
        }
    }

    public double getOutPoint() {
        return outPoint.get();
    }

    public void setOutPoint(double outPoint) {
        if (linkedGroup != null) {
            linkedGroup.setOutPoint(outPoint);
        } else {
            setOutPointInternal(outPoint);
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
        this.startTime.set(owner.snapToFrame(startTime));
        updateUIPosition();
    }

    public void setTimeshiftInternal(double timeshift) {
        this.timeshift = owner.snapToFrame(timeshift);
        updateUIPosition();
    }

    // Internal setter that doesn't trigger linked updates
    void setInPointInternal(double inPoint) {
        this.inPoint.set(owner.snapToFrame(inPoint));
        setDuration(outPoint.get() - inPoint);
    }

    // Internal setter that doesn't trigger linked updates
    void setOutPointInternal(double outPoint) {
        this.outPoint.set(owner.snapToFrame(outPoint));
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

    @Override
    protected void updateBounds() {
        try {
            super.updateBounds();
        } catch (Exception x) {
            System.out.println("suspect : Clip");
        }
    }
}