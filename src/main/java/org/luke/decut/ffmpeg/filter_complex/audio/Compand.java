package org.luke.decut.ffmpeg.filter_complex.audio;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class Compand extends ComplexFilter {
    private final FilterOption attacks;
    private final FilterOption decays;
    private final FilterOption points;
    private final FilterOption soft_knee;
    private final FilterOption gain;
    private final FilterOption volume;
    private final FilterOption delay;

    public Compand() {
        super("compand");
        attacks = new FilterOption("attacks");
        decays = new FilterOption("decays");
        points = new FilterOption("points");
        soft_knee = new FilterOption("soft-knee");
        gain = new FilterOption("gain");
        volume = new FilterOption("volume");
        delay = new FilterOption("delay");

        addOptions(attacks, decays, points, soft_knee, gain, volume, delay);
    }

    public Compand setAttacks(String value) {
        attacks.setValue(value);
        return this;
    }

    public Compand setAttacks(double... values) {
        String[] attackStrings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            attackStrings[i] = String.valueOf(values[i]);
        }
        attacks.setValue(String.join(",", attackStrings));
        return this;
    }

    public Compand setDecays(String value) {
        decays.setValue(value);
        return this;
    }

    public Compand setDecays(double... values) {
        String[] decayStrings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            decayStrings[i] = String.valueOf(values[i]);
        }
        decays.setValue(String.join(",", decayStrings));
        return this;
    }

    public Compand setPoints(String value) {
        points.setValue(value);
        return this;
    }

    public Compand setSoftKnee(String value) {
        soft_knee.setValue(value);
        return this;
    }

    public Compand setSoftKnee(double value) {
        soft_knee.setValue(String.valueOf(value));
        return this;
    }

    public Compand setGain(String value) {
        gain.setValue(value);
        return this;
    }

    public Compand setGain(double value) {
        gain.setValue(String.valueOf(value));
        return this;
    }

    public Compand setVolume(String value) {
        volume.setValue(value);
        return this;
    }

    public Compand setVolume(double value) {
        volume.setValue(String.valueOf(value));
        return this;
    }

    public Compand setDelay(String value) {
        delay.setValue(value);
        return this;
    }

    public Compand setDelay(double value) {
        delay.setValue(String.valueOf(value));
        return this;
    }

    public Compand setLightCompression() {
        points.setValue("-80/-80|-40/-30|-20/-20|-10/-10|0/0");
        attacks.setValue("0.1");
        decays.setValue("0.3");
        soft_knee.setValue("2");
        return this;
    }

    public Compand setMediumCompression() {
        points.setValue("-80/-80|-40/-35|-20/-15|-10/-8|0/0");
        attacks.setValue("0.05");
        decays.setValue("0.2");
        soft_knee.setValue("4");
        return this;
    }

    public Compand setHeavyCompression() {
        points.setValue("-80/-80|-40/-40|-20/-10|-10/-5|0/0");
        attacks.setValue("0.01");
        decays.setValue("0.1");
        soft_knee.setValue("6");
        return this;
    }

    public Compand setLimiter() {
        points.setValue("-80/-80|-6/-6|-3/-3|0/0");
        attacks.setValue("0.001");
        decays.setValue("0.05");
        soft_knee.setValue("1");
        return this;
    }

    public Compand setExpander() {
        points.setValue("-80/-105|-60/-80|-40/-60|-20/-40|0/0");
        attacks.setValue("0.1");
        decays.setValue("0.8");
        soft_knee.setValue("6");
        return this;
    }

    // Convenience method for creating custom transfer function points
    public Compand addTransferPoint(double input, double output) {
        String currentPoints = points.getValue();
        if (currentPoints == null || currentPoints.isEmpty()) {
            points.setValue(input + "/" + output);
        } else {
            points.setValue(currentPoints + "|" + input + "/" + output);
        }
        return this;
    }

    public Compand clearTransferPoints() {
        points.setValue("");
        return this;
    }
}