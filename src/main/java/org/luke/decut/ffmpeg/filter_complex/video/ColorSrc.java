package org.luke.decut.ffmpeg.filter_complex.video;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

import static org.luke.decut.ffmpeg.CommandPart.DECIMAL;

public class ColorSrc extends ComplexFilter {
    private final FilterOption color;
    private final FilterOption size;
    private final FilterOption rate;
    private final FilterOption duration;
    private final FilterOption sar;

    public ColorSrc() {
        super("color");

        color = new FilterOption("color");
        size = new FilterOption("size");
        rate = new FilterOption("rate");
        duration = new FilterOption("duration");
        sar = new FilterOption("sar");

        addOptions(color, size, rate, duration, sar);
    }

    public ColorSrc setColor(String value) {
        color.setValue(value);
        return this;
    }

    public ColorSrc setSize(String value) {
        size.setValue(value);
        return this;
    }

    public ColorSrc setRate(String value) {
        rate.setValue(value);
        return this;
    }

    public ColorSrc setDuration(double value) {
        duration.setValue(DECIMAL.format(value));
        return this;
    }

    public ColorSrc setSar(String value) {
        sar.setValue(value);
        return this;
    }
}
