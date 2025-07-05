package org.luke.decut.ffmpeg.filter_complex.video;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class SetPts extends ComplexFilter {
    private final FilterOption expr;
    private final FilterOption strip_fps;

    public SetPts() {
        super("setpts");
        expr = new FilterOption("expr");
        strip_fps = new FilterOption("strip_fps");

        addOptions(expr, strip_fps);
    }

    public SetPts setExpr(String value) {
        expr.setValue(value);
        return this;
    }

    public SetPts setStripFps(String value) {
        strip_fps.setValue(value);
        return this;
    }
}
