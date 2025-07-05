package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class ADelay extends ComplexFilter {
    private final FilterOption delays;
    private final FilterOption all;
    public ADelay() {
        super("adelay");

        delays = new FilterOption("delays");
        all = new FilterOption("all" );

        addOptions(delays, all);
    }

    public ADelay(long delay) {
        this();
        setDelays(Long.toString(delay));
        setAll("1");
    }

    public ADelay setDelays(String value) {
        delays.setValue(value);
        return this;
    }

    public ADelay setAll(String value) {
        all.setValue(value);
        return this;
    }
}
