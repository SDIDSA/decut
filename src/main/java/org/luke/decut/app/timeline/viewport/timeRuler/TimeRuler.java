package org.luke.decut.app.timeline.viewport.timeRuler;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.viewport.Viewport;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.threading.Platform;

import java.util.ArrayList;

public class TimeRuler extends Pane implements Styleable {
    public static final int HEIGHT = 25;

    private final Home owner;

    // Object pools for recycling
    private final ArrayList<TickMark> tickMarkPool = new ArrayList<>();
    private final ArrayList<TimeLabel> timeLabelPool = new ArrayList<>();

    // Currently active (visible) elements
    private final ArrayList<TickMark> activeTickMarks = new ArrayList<>();
    private final ArrayList<TimeLabel> activeTimeLabels = new ArrayList<>();

    private DoubleProperty pixelsPerSecond;
    private DoubleProperty pre;
    private DoubleProperty at;

    private double initDur;
    private double initX;
    private boolean resizeOut = false;

    public TimeRuler(Home owner) {
        this.owner = owner;

        initializePools();
        setupClickHandler();

        Platform.runLater(() -> {
            Viewport vp = owner.getViewPort();

            at = vp.atProperty();
            pre = vp.preProperty();
            ObservableDoubleValue scrollX = vp.scrollXProperty();
            ReadOnlyDoubleProperty vpw = vp.widthProperty();

            DoubleProperty duration = owner.durationProperty();
            pixelsPerSecond = owner.ppsProperty();

            InvalidationListener retick = (e) -> {
                double begin = scrollX.get() / pixelsPerSecond.get();
                double visibleDuration = vpw.get() / pixelsPerSecond.get();
                double end = begin + visibleDuration;
                updateTicks(pre.get(), begin, end, pixelsPerSecond.get(), scrollX.get(), vpw.get());
            };

            duration.addListener(retick);
            pixelsPerSecond.addListener(retick);
            scrollX.addListener(retick);
            vpw.addListener(retick);
            owner.framerateProperty().addListener(retick);
        });

        setOnMouseMoved(event -> {
            double min = getWidth() - 8;
            double max = getWidth() + 8;
            double mx = event.getX();
            if(mx > min && mx < max) {
                setCursor(Cursor.H_RESIZE);
                resizeOut = true;
            } else {
                setCursor(Cursor.DEFAULT);
                resizeOut = false;
            }
        });

        applyStyle(owner.getWindow().getStyl());
    }

    private void setupClickHandler() {
        setOnMousePressed(this::handleMouseClick);
        setOnMouseDragged(this::handleMouseClick);

        setMouseTransparent(false);
        setPickOnBounds(true);
    }

    private void handleMouseClick(MouseEvent event) {
        owner.pausePlayback();
        if (pixelsPerSecond == null || pre == null || at == null) {
            return;
        }

        if(resizeOut) {
            if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                initDur = owner.durationProperty().get();
                initX = event.getSceneX();
            } else if(event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                double dx = event.getSceneX() - initX;
                double dt = owner.pixelToTime(dx);
                owner.durationProperty().set(initDur + dt);
            }

            return;
        }

        double clickX = event.getX();
        double timePosition = (clickX - pre.get()) / pixelsPerSecond.get();

        double maxDuration = owner.durationProperty().get();
        timePosition = Math.max(0, Math.min(timePosition, maxDuration));

        double frameRate = owner.framerateProperty().get();
        double framePosition = Math.round(timePosition * frameRate) / frameRate;

        at.set(framePosition);

        event.consume();
    }

    private void updateTicks(double pre, double begin, double end, double pixelsPerSecond, double xScroll, double vpw) {
        returnToPool();

        TickInterval interval = calculateTickInterval(pixelsPerSecond);

        double majorStart = Math.round(begin / interval.major) * interval.major;
        double minorStart = Math.round(begin / interval.minor) * interval.minor;

        for (double time = majorStart; time <= end + interval.major / 2; time += interval.major) {
            if (time >= begin - interval.major) {
                TickMark tick = getTickMark();
                tick.updatePosition(pre, time, true, pixelsPerSecond);

                TimeLabel label = getTimeLabel();
                label.updatePosition(pre, time, pixelsPerSecond, xScroll, vpw, owner.framerateProperty().get());
            }
        }

        for (double time = minorStart; time <= end + interval.minor / 2; time += interval.minor) {
            if (time >= begin - interval.minor) {
                if (Math.abs(time % interval.major) > 0.001) {
                    TickMark tick = getTickMark();
                    tick.updatePosition(pre, time, false, pixelsPerSecond);
                }
            }
        }
    }

    private TickMark getTickMark() {
        TickMark tick;
        if (tickMarkPool.isEmpty()) {
            tick = new TickMark();
        } else {
            tick = tickMarkPool.removeLast();
        }
        tick.setStroke(owner.getWindow().getStyl().get().getTextNormal());

        activeTickMarks.add(tick);
        getChildren().addFirst(tick);

        return tick;
    }

    private TimeLabel getTimeLabel() {
        TimeLabel label;
        if (timeLabelPool.isEmpty()) {
            label = new TimeLabel();
        } else {
            label = timeLabelPool.removeLast();
        }
        label.setFill(owner.getWindow().getStyl().get().getTextNormal());

        activeTimeLabels.add(label);
        getChildren().addFirst(label);

        return label;
    }

    private void returnToPool() {
        getChildren().removeAll(activeTickMarks);
        tickMarkPool.addAll(activeTickMarks);
        activeTickMarks.clear();

        getChildren().removeAll(activeTimeLabels);
        timeLabelPool.addAll(activeTimeLabels);
        activeTimeLabels.clear();
    }

    private void initializePools() {
        for (int i = 0; i < 50; i++) {
            tickMarkPool.add(new TickMark());
        }
        for (int i = 0; i < 20; i++) {
            timeLabelPool.add(new TimeLabel());
        }
    }

    private TickInterval calculateTickInterval(double pixelsPerSecond) {
        if (pixelsPerSecond >= 400) {
            return new TickInterval(1 / 6.0, 1 / owner.framerateProperty().get());
        } else if (pixelsPerSecond >= 200) {
            return new TickInterval(0.5, 0.1);
        } else if (pixelsPerSecond >= 100) {
            return new TickInterval(1.0, 0.2);
        } else if (pixelsPerSecond >= 50) {
            return new TickInterval(2.0, 0.5);
        } else if (pixelsPerSecond >= 25) {
            return new TickInterval(5.0, 1.0);
        } else if (pixelsPerSecond >= 10) {
            return new TickInterval(10.0, 2.0);
        } else if (pixelsPerSecond >= 5) {
            return new TickInterval(30.0, 5.0);
        } else if (pixelsPerSecond >= 2) {
            return new TickInterval(60.0, 15.0);
        } else if (pixelsPerSecond >= 1.2) {
            return new TickInterval(120.0, 15.0);
        } else {
            return new TickInterval(300.0, 60.0);
        }
    }

    @Override
    public void applyStyle(Style style) {
        for (TickMark activeTickMark : activeTickMarks) {
            activeTickMark.setStroke(style.getTextNormal());
        }

        for (TimeLabel activeTimeLabel : activeTimeLabels) {
            activeTimeLabel.setFill(style.getTextNormal());
        }

        setBackground(Backgrounds.make(style.getBackgroundModifierHover()));
    }
}
