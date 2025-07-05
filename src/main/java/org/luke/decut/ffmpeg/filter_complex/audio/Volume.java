package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class Volume extends ComplexFilter {
    private final FilterOption volume;
    private final FilterOption precision;
    private final FilterOption replaygain;
    private final FilterOption replaygain_preamp;
    private final FilterOption replaygain_noclip;
    private final FilterOption eval;

    public Volume() {
        super("volume");

        volume = new FilterOption("volume");
        precision = new FilterOption("precision");
        replaygain = new FilterOption("replaygain");
        replaygain_preamp = new FilterOption("replaygain_preamp");
        replaygain_noclip = new FilterOption("replaygain_noclip");
        eval = new FilterOption("eval");

        addOptions(volume, precision, replaygain, replaygain_preamp, replaygain_noclip, eval);
    }

    public Volume(double value) {
        this();
        setVolume(DECIMAL.format(value));
    }

    public Volume setVolume(String value) {
        volume.setValue(value);
        return this;
    }

    public Volume setPrecision(String value) {
        precision.setValue(value);
        return this;
    }

    public Volume setReplayGain(String value) {
        replaygain.setValue(value);
        return this;
    }

    public Volume setReplayGainPreamp(String value) {
        replaygain_preamp.setValue(value);
        return this;
    }

    public Volume setReplayGainNoClip(String value) {
        replaygain_noclip.setValue(value);
        return this;
    }

    public Volume setEval(String value) {
        eval.setValue(value);
        return this;
    }
}
