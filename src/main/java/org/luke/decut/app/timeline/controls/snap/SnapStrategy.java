package org.luke.decut.app.timeline.controls.snap;

import org.luke.decut.app.home.Home;

import java.util.function.BiFunction;

public enum SnapStrategy {
    SECOND((owner, time) -> {
        double threshold = owner.pixelToTime(10);
        double mod = ((time + 0.5) % 1) - 0.5;
        if(Math.abs(mod) < threshold) {
            return (double) Math.round(time);
        }
        return time;
    }, "Second", "snap-to-second"),
    FIFTH(null, "Fifth", "snap-to-fifth"),
    CLIPS(null, "Clip", "snap-to-clips"),
    FRAME(null, "Frame", "snap-to-frame");

    private final BiFunction<Home, Double, Double> snapper;
    private final String name;
    private final String icon;

    SnapStrategy(BiFunction<Home, Double, Double> snapper, String name, String icon) {
        this.snapper = snapper;
        this.name = name;
        this.icon = icon;
    }
    public double snap(Home owner, double time) {
        return snapper.apply(owner, time);
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public static SnapStrategy forName(String name) {
        for (SnapStrategy value : values()) {
            if(value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }
}
