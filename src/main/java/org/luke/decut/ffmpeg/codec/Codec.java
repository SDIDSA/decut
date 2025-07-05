package org.luke.decut.ffmpeg.codec;

import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.core.StreamType;

public class Codec implements CommandPart {
    private final StreamType type;
    private final String codecName;

    public Codec(StreamType type, String codecName) {
        this.type = type;
        this.codecName = codecName;
    }

    public String getCodecName() {
        return codecName;
    }

    public StreamType getType() {
        return type;
    }

    @Override
    public String apply(FfmpegCommand command) {
        return "-c:" + type.apply(command) + " " + codecName;
    }
}