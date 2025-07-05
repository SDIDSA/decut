package org.luke.decut.ffmpeg.options;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;

public abstract class FfmpegOption implements CommandPart {
    private final String name;
    private String value;

    public FfmpegOption(String name) {
        this.name = name;
    }

    public FfmpegOption setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String apply(FfmpegCommand command) {
        return "-".concat(name).concat(value != null ? " ".concat(value) : "");
    }
}
