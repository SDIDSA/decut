package org.luke.decut.ffmpeg.filter_complex.video;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class TrimC extends ComplexFilter {
    private final FilterOption start;
    private final FilterOption end;
    private final FilterOption start_pts;
    private final FilterOption end_pts;
    private final FilterOption duration;
    private final FilterOption start_sample;
    private final FilterOption end_sample;

    public TrimC() {
        super("trim");

        start = new FilterOption("start");
        end = new FilterOption("end");
        start_pts = new FilterOption("start_pts");
        end_pts = new FilterOption("end_pts");
        duration = new FilterOption("duration");
        start_sample = new FilterOption("start_sample");
        end_sample = new FilterOption("end_sample");

        addOptions(start, end, start_pts, end_pts, duration, start_sample, end_sample);
    }

    public TrimC(double start, double duration) {
        this();

        setStart(start);
        setDuration(duration);
    }

    public TrimC setStart(double value) {
        start.setValue(String.valueOf(value));
        return this;
    }

    public TrimC setEnd(String value) {
        end.setValue(value);
        return this;
    }

    public TrimC setStartPts(String value) {
        start_pts.setValue(value);
        return this;
    }

    public TrimC setEndPts(String value) {
        end_pts.setValue(value);
        return this;
    }

    public TrimC setDuration(double value) {
        duration.setValue(String.valueOf(value));
        return this;
    }

    public TrimC setStartSample(String value) {
        start_sample.setValue(value);
        return this;
    }

    public TrimC setEndSample(String value) {
        end_sample.setValue(value);
        return this;
    }
}
