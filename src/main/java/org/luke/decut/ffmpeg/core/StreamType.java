package org.luke.decut.ffmpeg.core;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;

public enum StreamType {
    AUDIO("a"),
    VIDEO("v");

    private final String val;

    StreamType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public String apply(FfmpegCommand command) {
        return val;
    }
}
