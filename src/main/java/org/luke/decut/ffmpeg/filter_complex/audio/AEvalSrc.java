package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class AEvalSrc extends ComplexFilter {
    private final FilterOption exprs;
    private final FilterOption channel_layout;
    private final FilterOption duration;
    private final FilterOption nb_samples;
    private final FilterOption sample_rate;

    public AEvalSrc() {
        super("aevalsrc");

        exprs = new FilterOption("exprs" );
        channel_layout = new FilterOption("channel_layout" );
        duration = new FilterOption("duration" );
        nb_samples = new FilterOption("nb_samples" );
        sample_rate = new FilterOption("sample_rate" );

        addOptions(exprs, channel_layout, duration, nb_samples, sample_rate);
    }

    public AEvalSrc setExprs(String value) {
        exprs.setValue(value);
        return this;
    }

    public AEvalSrc setChannelLayout(String value) {
        channel_layout.setValue(value);
        return this;
    }

    public AEvalSrc setDuration(String value) {
        duration.setValue(value);
        return this;
    }

    public AEvalSrc setNbSamples(String value) {
        nb_samples.setValue(value);
        return this;
    }

    public AEvalSrc setSampleRate(String value) {
        sample_rate.setValue(value);
        return this;
    }
}
