package org.luke.decut.app.timeline.viewport.timeRuler;

import javafx.scene.shape.Line;

public class TickMark extends Line {

    public TickMark() {
        super();

        setMouseTransparent(true);
        setStrokeWidth(1.0);

        setStartY(0);
    }

    public void updatePosition(double pre, double timePosition, boolean isMajor, double pixelsPerSecond) {
        double x = pre + (timePosition * pixelsPerSecond);
        setLayoutX((int) x);
        setOpacity(isMajor ? 0.7 : 0.5);
        setEndY(isMajor ? 6 : 4);
    }
}