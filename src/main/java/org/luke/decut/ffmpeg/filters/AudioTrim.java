package org.luke.decut.ffmpeg.filters;

import org.luke.decut.ffmpeg.core.StreamType;
import org.luke.decut.ffmpeg.filters.core.Filter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class AudioTrim extends Filter {
    private final FilterOption start;
    private final FilterOption end;
    private final FilterOption start_pts;
    private final FilterOption end_pts;
    private final FilterOption duration;
    private final FilterOption start_sample;
    private final FilterOption end_sample;

    public AudioTrim() {
        super("atrim", StreamType.AUDIO);

        start = new FilterOption("start");
        end = new FilterOption("end");
        start_pts = new FilterOption("start_pts");
        end_pts = new FilterOption("end_pts");
        duration = new FilterOption("duration");
        start_sample = new FilterOption("start_sample");
        end_sample = new FilterOption("end_sample");

        addOptions(start, end, start_pts, end_pts, duration, start_sample, end_sample);
    }

    public AudioTrim setStart(String value) {
        start.setValue(value);
        return this;
    }

    public AudioTrim setEnd(String value) {
        end.setValue(value);
        return this;
    }

    public AudioTrim setStartPts(String value) {
        start_pts.setValue(value);
        return this;
    }

    public AudioTrim setEndPts(String value) {
        end_pts.setValue(value);
        return this;
    }

    public AudioTrim setDuration(String value) {
        duration.setValue(value);
        return this;
    }

    public AudioTrim setStartSample(String value) {
        start_sample.setValue(value);
        return this;
    }

    public AudioTrim setEndSample(String value) {
        end_sample.setValue(value);
        return this;
    }
}
