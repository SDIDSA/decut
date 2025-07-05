package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class AFormat extends ComplexFilter {
    private final FilterOption sample_fmts;
    private final FilterOption sample_rates;
    private final FilterOption channel_layouts;

    public AFormat() {
        super("aformat");
        sample_fmts = new FilterOption("sample_fmts");
        sample_rates = new FilterOption("sample_rates");
        channel_layouts = new FilterOption("channel_layouts");

        addOptions(sample_fmts, sample_rates, channel_layouts);
    }

    public AFormat setSampleFormats(String value) {
        sample_fmts.setValue(value);
        return this;
    }

    public AFormat setSampleRates(String value) {
        sample_rates.setValue(value);
        return this;
    }

    public AFormat setSampleRates(int... rates) {
        String[] rateStrings = new String[rates.length];
        for (int i = 0; i < rates.length; i++) {
            rateStrings[i] = String.valueOf(rates[i]);
        }
        sample_rates.setValue(String.join("|", rateStrings));
        return this;
    }

    public AFormat setChannelLayouts(String value) {
        channel_layouts.setValue(value);
        return this;
    }

    // Convenience methods for common sample formats
    public AFormat setPcm16() {
        sample_fmts.setValue("s16");
        return this;
    }

    public AFormat setPcm24() {
        sample_fmts.setValue("s24");
        return this;
    }

    public AFormat setPcm32() {
        sample_fmts.setValue("s32");
        return this;
    }

    public AFormat setFloat32() {
        sample_fmts.setValue("flt");
        return this;
    }

    public AFormat setFloat64() {
        sample_fmts.setValue("dbl");
        return this;
    }

    // Convenience methods for common channel layouts
    public AFormat setMono() {
        channel_layouts.setValue("mono");
        return this;
    }

    public AFormat setStereo() {
        channel_layouts.setValue("stereo");
        return this;
    }

    public AFormat set51() {
        channel_layouts.setValue("5.1");
        return this;
    }

    public AFormat set71() {
        channel_layouts.setValue("7.1");
        return this;
    }

    // Convenience methods for common sample rates
    public AFormat set44100Hz() {
        sample_rates.setValue("44100");
        return this;
    }

    public AFormat set48000Hz() {
        sample_rates.setValue("48000");
        return this;
    }

    public AFormat set96000Hz() {
        sample_rates.setValue("96000");
        return this;
    }
}