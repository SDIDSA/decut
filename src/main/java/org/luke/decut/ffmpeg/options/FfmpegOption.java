package org.luke.decut.ffmpeg.options;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;

import java.util.List;

public class FfmpegOption implements CommandPart {
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
    public List<String> apply(FfmpegCommand command) {
        String nameStr = "-".concat(name);
        return value == null ? List.of(nameStr) :
                List.of(nameStr, value);
    }
}
