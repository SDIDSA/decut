package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

import static org.luke.decut.ffmpeg.CommandPart.DECIMAL;

public class ATrimC extends ComplexFilter {
    private final FilterOption start;
    private final FilterOption end;
    private final FilterOption start_pts;
    private final FilterOption end_pts;
    private final FilterOption duration;
    private final FilterOption start_sample;
    private final FilterOption end_sample;

    public ATrimC() {
        super("atrim");

        start = new FilterOption("start");
        end = new FilterOption("end");
        start_pts = new FilterOption("start_pts");
        end_pts = new FilterOption("end_pts");
        duration = new FilterOption("duration");
        start_sample = new FilterOption("start_sample");
        end_sample = new FilterOption("end_sample");

        addOptions(start, end, start_pts, end_pts, duration, start_sample, end_sample);
    }

    public ATrimC(double start, double duration) {
        this();

        setStart(start);
        if(duration != -1) {
            setDuration(duration);
        }
    }

    public ATrimC setStart(double value) {
        start.setValue(DECIMAL.format(value));
        return this;
    }

    public ATrimC setEnd(String value) {
        end.setValue(value);
        return this;
    }

    public ATrimC setStartPts(String value) {
        start_pts.setValue(value);
        return this;
    }

    public ATrimC setEndPts(String value) {
        end_pts.setValue(value);
        return this;
    }

    public ATrimC setDuration(double value) {
        duration.setValue(DECIMAL.format(value));
        return this;
    }

    public ATrimC setStartSample(String value) {
        start_sample.setValue(value);
        return this;
    }

    public ATrimC setEndSample(String value) {
        end_sample.setValue(value);
        return this;
    }
}
