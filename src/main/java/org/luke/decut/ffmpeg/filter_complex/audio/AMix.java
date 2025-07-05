package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class AMix extends ComplexFilter {
    private final FilterOption inputs;
    private final FilterOption duration;
    private final FilterOption dropout_transition;
    private final FilterOption weights;
    private final FilterOption normalize;

    public AMix() {
        super("amix");

        inputs = new FilterOption("inputs");
        duration = new FilterOption("duration");
        dropout_transition = new FilterOption("dropout_transition");
        weights = new FilterOption("weights");
        normalize = new FilterOption("normalize");

        addOptions(inputs, duration, dropout_transition, weights, normalize);
    }

    public AMix(int inputCount) {
        this();
        setInputs(inputCount);
        setDuration("longest");
    }

    public AMix setInputs(int value) {
        inputs.setValue(String.valueOf(value));
        return this;
    }

    public AMix setDuration(String value) {
        duration.setValue(value);
        return this;
    }

    public AMix setDropoutTransition(String value) {
        dropout_transition.setValue(value);
        return this;
    }

    public AMix setWeights(String value) {
        weights.setValue(value);
        return this;
    }

    public AMix setNormalize(String value) {
        normalize.setValue(value);
        return this;
    }
}
