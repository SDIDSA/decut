package org.luke.decut.app.timeline.controls.snap;

import org.luke.decut.app.home.Home;

import java.util.List;

public enum SnapStrategy {
    SECOND(SnapPointsFactory.SECOND, "Second", "snap-to-second"),
    FIFTH(SnapPointsFactory.FIFTH, "Fifth", "snap-to-fifth"),
    CLIPS(SnapPointsFactory.CLIPS, "Clip", "snap-to-clips"),
    FRAME(SnapPointsFactory.FRAME, "Frame", "snap-to-frame");

    private final SnapPointsFactory snapPointsFactory;
    private final String name;
    private final String icon;

    SnapStrategy(SnapPointsFactory snapPointsFactory, String name, String icon) {
        this.snapPointsFactory = snapPointsFactory;
        this.name = name;
        this.icon = icon;
    }

    private List<Double> snapPoints;
    public double snap(Home owner, double time) {
        if(snapPoints == null) {
            snapPoints = snapPointsFactory.apply(owner);
        }
        double threshold = owner.pixelToTime(10);
        for (double point : snapPoints) {
            if (Math.abs(time - point) <= threshold) {
                return point;
            }
        }
        return time;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public SnapStrategy forName(String name) {
        for (SnapStrategy value : values()) {
            if(value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }
}
