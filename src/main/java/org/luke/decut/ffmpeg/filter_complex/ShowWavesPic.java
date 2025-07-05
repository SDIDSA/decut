package org.luke.decut.ffmpeg.filter_complex;

import org.luke.decut.ffmpeg.filter_complex.core.ComplexFilter;
import org.luke.decut.ffmpeg.filters.core.FilterOption;

public class ShowWavesPic extends ComplexFilter {
    private final FilterOption size;
    private final FilterOption split_channels;
    private final FilterOption colors;
    private final FilterOption scale;
    private final FilterOption draw;
    private final FilterOption filter;

    public ShowWavesPic() {
        super("showwavespic");
        size = new FilterOption("size");
        split_channels = new FilterOption("split_channels");
        colors = new FilterOption("colors");
        scale = new FilterOption("scale");
        draw = new FilterOption("draw");
        filter = new FilterOption("filter");

        addOptions(size, split_channels, colors, scale, draw, filter);
    }

    public ShowWavesPic setSize(String value) {
        size.setValue(value);
        return this;
    }

    public ShowWavesPic setSize(int width, int height) {
        size.setValue(width + "x" + height);
        return this;
    }

    public ShowWavesPic setSplitChannels(boolean value) {
        split_channels.setValue(value ? "1" : "0");
        return this;
    }

    public ShowWavesPic setColors(String value) {
        colors.setValue(value);
        return this;
    }

    public ShowWavesPic setScale(String value) {
        scale.setValue(value);
        return this;
    }

    public ShowWavesPic setDraw(String value) {
        draw.setValue(value);
        return this;
    }

    public ShowWavesPic setFilter(String value) {
        filter.setValue(value);
        return this;
    }
}