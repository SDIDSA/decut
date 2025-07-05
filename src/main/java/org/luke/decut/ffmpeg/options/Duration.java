package org.luke.decut.ffmpeg.options;

public class Duration extends FfmpegOption {
    public Duration(double value) {
        super("t");
        setValue(DECIMAL.format(value));
    }
}
