package org.luke.decut.app.timeline.viewport;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.viewport.content.TrackContentList;
import org.luke.decut.app.timeline.viewport.timeRuler.TimeRuler;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.scroll.HorizontalScrollable;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.threading.Platform;

public class Viewport extends HorizontalScrollable implements Styleable {
    private final Home owner;

    private final DoubleProperty pre;
    private final DoubleProperty pps;
    private final DoubleProperty at;
    private final TimeRuler ruler;
    private final TrackContentList trackContents;
    private final double minPixelsPerSecond = 1;
    private final double maxPixelsPerSecond = 800;

    private final ColorIcon playHead;
    private final Line playLine;

    public Viewport(Home owner) {
        this.owner = owner;
        HBox.setHgrow(this, Priority.ALWAYS);

        pre = new SimpleDoubleProperty(10);
        pps = new SimpleDoubleProperty(50);
        at = new SimpleDoubleProperty(0);

        pps.addListener((obs, oldVal, newVal) -> {
            double newPixelsPerSecond = newVal.doubleValue();

            double center = at.get();

            double newDuration = (getWidth() - pre.get()) / newPixelsPerSecond;
            double newBegin = center - newDuration / 2;

            double totalDuration = owner.durationProperty().get();
            double totalWidth = totalDuration * newPixelsPerSecond + pre.get() * 2;

            double newScrollX = newBegin * newPixelsPerSecond + pre.get() / 2;

            setScrollX(newScrollX, totalWidth);
        });

        ruler = new TimeRuler(owner);
        ruler.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        ruler.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        trackContents = new TrackContentList(owner);

        playHead = new ColorIcon("playhead", 10);
        playLine = new Line();
        playLine.setStartY(0);
        playLine.setStrokeWidth(1.0);
        playHead.setMouseTransparent(true);
        playLine.setMouseTransparent(true);
        playLine.endYProperty().bind(heightProperty());
        playLine.setOpacity(0.5);

        Platform.runLater(() -> {
            pps.bind(owner.timeScaleProperty().multiply(maxPixelsPerSecond - minPixelsPerSecond).add(minPixelsPerSecond));
            ruler.prefWidthProperty().bind(
                    owner.durationProperty().multiply(pps).add(pre));

            trackContents.getScrollBar().positionProperty().bindBidirectional(
                    owner.getTracks().getTrackList().getScrollBar().positionProperty()
            );

            InvalidationListener rePlayHead = (e) -> {
                double phx = pre.get() + at.get() * pps.get();
                playLine.setTranslateX((int) phx);
                playHead.setTranslateX((int) (phx - playHead.getWidth() / 2));
            };

            pps.addListener(rePlayHead);
            at.addListener((e) -> {
                rePlayHead.invalidated(e);
                ensurePlayheadVisible();
            });

            Platform.runLater(() -> {
                rePlayHead.invalidated(at);
            });

            trackContents.getScrollBar().translateXProperty().bind(Bindings.createDoubleBinding(() ->
                            -(trackContents.getWidth() - getWidth() - getScrollX()),
                    scrollXProperty(), trackContents.widthProperty(), widthProperty()));
        });

        StackPane content = new StackPane();
        content.setAlignment(Pos.TOP_LEFT);
        ruler.setPrefHeight(TimeRuler.HEIGHT);
        Separator sep = new Separator(owner.getWindow(), Orientation.HORIZONTAL);
        sep.setTranslateY(TimeRuler.HEIGHT);
        trackContents.setTranslateY(TimeRuler.HEIGHT + 5);

        content.paddingProperty().bind(Bindings.createObjectBinding(() -> {
            return new Insets(0,preProperty().get(),0,0);
        }, preProperty()));
        content.getChildren().addAll(ruler, sep, trackContents, playHead, playLine);

        addEventFilter(ScrollEvent.ANY, e -> {
            if(e.getDeltaY() != 0 && e.isControlDown()) {
                DoubleProperty ts = owner.timeScaleSource();
                double nv = ts.get() + e.getDeltaY() / 1500;
                nv = Math.max(Math.min(nv, 1), 0);
                ts.set(nv);
            }
        });

        setContent(content);

        applyStyle(owner.getWindow().getStyl());
    }

    private void ensurePlayheadVisible() {
        if (pps == null || pre == null || at == null) {
            return;
        }

        double currentScrollX = getScrollX();
        double viewportWidth = getWidth();
        double playheadTime = at.get();
        double playheadPixelPos = pre.get() + playheadTime * pps.get();

        double visibleStart = currentScrollX + pre.get();
        double visibleEnd = currentScrollX + viewportWidth - pre.get();

        double totalDuration = owner.durationProperty().get();
        if (playheadPixelPos < visibleStart) {
            setScrollX(currentScrollX - 20);
        } else if(playheadPixelPos > visibleEnd) {
            setScrollX(currentScrollX + 20);
        }
    }

    public DoubleProperty ppsProperty() {
        return pps;
    }

    public DoubleProperty atProperty() {
        return at;
    }

    public DoubleProperty preProperty() {
        return pre;
    }

    @Override
    public void applyStyle(Style style) {
        setBackground(Backgrounds.make(style.getBackgroundFloating(),
                new CornerRadii(0, 0, 8, 0, false)));
        getScrollBar().setThumbFill(style.getTextMuted());

        playHead.setFill(style.getTextDanger());
        playLine.setStroke(style.getTextDanger());
    }
}
