package org.luke.decut.ffmpeg.filters;

import org.luke.decut.ffmpeg.filters.core.Filter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;
import org.luke.decut.ffmpeg.core.StreamType;

public class SilenceDetect extends Filter {
    private final FilterOption noise;
    private final FilterOption duration;
    private final FilterOption mono;


    public SilenceDetect() {
        super("silencedetect", StreamType.AUDIO);
        noise = new FilterOption("noise");
        duration = new FilterOption("duration");
        mono = new FilterOption("mono");

        addOptions(noise, duration, mono);
    }

    public SilenceDetect setNoise(String value) {
        noise.setValue(value);
        return this;
    }

    public SilenceDetect setDuration(String value) {
        duration.setValue(value);
        return this;
    }

    public SilenceDetect setMono(String value) {
        mono.setValue(value);
        return this;
    }
}
