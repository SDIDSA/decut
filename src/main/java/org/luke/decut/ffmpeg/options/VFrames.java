package org.luke.decut.ffmpeg.options;

public class VFrames extends FfmpegOption {
    public VFrames(int value) {
        super("vframes");
        setValue(Integer.toString(value));
    }
}
