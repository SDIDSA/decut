package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class ASetPts extends ComplexFilter {
    private final FilterOption expr;

    public ASetPts() {
        super("asetpts");
        expr = new FilterOption("expr");

        addOptions(expr);
    }

    public ASetPts setExpr(String value) {
        expr.setValue(value);
        return this;
    }
}
