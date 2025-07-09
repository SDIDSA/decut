package org.luke.decut.app.timeline.clips;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.factory.Borders;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;

import java.util.HashMap;
import java.util.List;

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
            initX = event.getSceneX();
            initOut = outPoint.get();
            initIn = inPoint.get();
            initStart = this.startTime.get();
            initStartTimes = saveStartTimes();
        });

        setOnMouseDragged(event -> {
            double mx = event.getSceneX();
            double dx = mx - initX;
            double dt = owner.pixelToTime(dx);
            if (resizeOut) {
                double newOut = initOut + dt;
                double timePos = owner.snapDrag(startTime.get() + newOut - inPoint.get());
                newOut = timePos - startTime.get() + inPoint.get();
                newOut = Math.max(
                        Math.min(
                                newOut,
                                sourceAsset.getDurationSeconds()),
                        inPoint.get());
                if(newOut==outPoint.get()) return;
                setOutPoint(newOut);
                resolveCollision();
            } else if (resizeIn) {
                double newStartTime = initStart + dt;
                double snappedStartTime = owner.snapDrag(newStartTime);
                double startTimeDelta = Math.max(0, snappedStartTime - initStart);
                double newIn = initIn + startTimeDelta;
                newIn = Math.max(
                        Math.min(newIn, outPoint.get()),
                        0);
                if(newIn == inPoint.get()) return;
                setInPointAndStartTime(newIn, snappedStartTime);
                resolveCollision();
            } else {
                double newStartTime = Math.max(0, owner.snapDrag(initStart + dt));
                if (newStartTime != this.startTime.get()) {
                    setStartTime(newStartTime);
                    resolveCollision();
                }
            }
        });

        setOnMouseReleased(_ -> {
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
            }else if (inPoint.get() != initIn) {
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
            } else if(startTime.get() != initStart) {
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

    private HashMap<TimelineClip, Double> saveStartTimes() {
        HashMap<TimelineClip, Double> res = new HashMap<>();
        for (TimelineClip clip : track.getContent().getClips()) {
            res.put(clip, clip.getStartTime());
        }
        return res;
    }

    private void applyStartTimes(HashMap<TimelineClip, Double> times) {
        times.forEach(TimelineClip::setStartTime);
    }

    private void resolveCollision() {
        List<TimelineClip> clips = track.getContent().getSortedClips();
        int currentIndex = clips.indexOf(this);

        if (currentIndex == -1) return;

        if (currentIndex + 1 < clips.size()) {
            TimelineClip nextClip = clips.get(currentIndex + 1);
            double thisEndTime = getEndTime();
            double nextStartTime = nextClip.getStartTime();

            System.out.println(thisEndTime + " : " + nextStartTime);

            if (thisEndTime > nextStartTime) {
                double pushAmount = thisEndTime - nextStartTime;
                pushClipForward(nextClip, pushAmount);
            }
        }

        if (currentIndex > 0) {
            TimelineClip prevClip = clips.get(currentIndex - 1);
            double thisStartTime = getStartTime();
            double prevEndTime = prevClip.getEndTime();
            System.out.println(prevEndTime + " : " + thisStartTime);

            if (thisStartTime < prevEndTime) {
                double pushAmount = prevEndTime - thisStartTime;
                pushClipForward(this, pushAmount);
            }
        }

        resetStartTimes();
    }

    private void resetStartTimes() {
        List<TimelineClip> clips = track.getContent().getSortedClips();
        int currentIndex = clips.indexOf(this);

        for (int i = currentIndex + 1; i < clips.size(); i++) {
            TimelineClip clip = clips.get(i);
            double originalStartTime = initStartTimes.get(clip);
            double currentStartTime = clip.getStartTime();

            if (currentStartTime > originalStartTime) {
                double desiredStartTime = originalStartTime;

                if (i > 0) {
                    TimelineClip prevClip = clips.get(i - 1);
                    double prevEndTime = prevClip.getEndTime();
                    desiredStartTime = Math.max(desiredStartTime, prevEndTime);
                }

                desiredStartTime = Math.max(desiredStartTime, 0);

                desiredStartTime = owner.snapToFrame(desiredStartTime);

                if (desiredStartTime < currentStartTime) {
                    clip.setStartTime(desiredStartTime);
                }
            }
        }
    }

    private void pushClipForward(TimelineClip clip, double pushAmount) {
        double newStartTime = owner.snapToFrame(clip.getStartTime() + pushAmount);
        clip.setStartTime(newStartTime);

        List<TimelineClip> clips = track.getContent().getSortedClips();
        int clipIndex = clips.indexOf(clip);

        if (clipIndex + 1 < clips.size()) {
            TimelineClip nextClip = clips.get(clipIndex + 1);
            if (clip.getEndTime() > nextClip.getStartTime()) {
                double nextPushAmount = clip.getEndTime() - nextClip.getStartTime();
                pushClipForward(nextClip, nextPushAmount);
            }
        }
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
        if (linkedGroup != null) {
            linkedGroup.updateInPointAndStartTime(newInPoint, newStartTime);
        } else {
            setInPointAndStartTimeInternal(newInPoint, newStartTime);
        }
    }

    void setInPointAndStartTimeInternal(double newInPoint, double newStartTime) {
        this.inPoint.set(owner.snapToFrame(newInPoint));
        this.startTime.set(owner.snapToFrame(newStartTime));
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
}