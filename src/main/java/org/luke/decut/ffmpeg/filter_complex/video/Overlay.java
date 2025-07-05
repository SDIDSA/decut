package org.luke.decut.ffmpeg.filter_complex.video;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class Overlay extends ComplexFilter {
    private final FilterOption x;
    private final FilterOption y;
    private final FilterOption eof_action;
    private final FilterOption eval;
    private final FilterOption shortest;
    private final FilterOption format;
    private final FilterOption repeatlast;
    private final FilterOption alpha;

    public Overlay() {
        super("overlay");

        x = new FilterOption("x");
        y = new FilterOption("y");
        eof_action = new FilterOption("eof_action");
        eval = new FilterOption("eval");
        shortest = new FilterOption("shortest");
        format = new FilterOption("format");
        repeatlast = new FilterOption("repeatlast");
        alpha = new FilterOption("alpha");

        addOptions(x, y, eof_action, eval, shortest, format, repeatlast, alpha);
    }

    public Overlay center() {
        setX("(main_w-overlay_w)/2");
        setY("(main_h-overlay_h)/2");
        return this;
    }

    public Overlay setX(String value) {
        x.setValue(value);
        return this;
    }

    public Overlay setY(String value) {
        y.setValue(value);
        return this;
    }

    public Overlay setEofAction(String value) {
        eof_action.setValue(value);
        return this;
    }

    public Overlay setEval(String value) {
        eval.setValue(value);
        return this;
    }

    public Overlay setShortest(String value) {
        shortest.setValue(value);
        return this;
    }

    public Overlay setFormat(String value) {
        format.setValue(value);
        return this;
    }

    public Overlay setRepeatLast(String value) {
        repeatlast.setValue(value);
        return this;
    }

    public Overlay setAlpha(String value) {
        alpha.setValue(value);
        return this;
    }
}
