package org.luke.decut.ffmpeg.options;

public class Seek extends FfmpegOption {
    public Seek(String value) {
        super("ss");
        setValue(value);
    }
}
