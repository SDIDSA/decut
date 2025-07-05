package org.luke.decut.ffmpeg.bitrate;


import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.core.StreamType;

public class Bitrate implements CommandPart {
    private final StreamType type;
    private final String bitrate;

    public Bitrate(StreamType type, String bitrate) {
        this.type = type;
        this.bitrate = bitrate;
    }

    public StreamType getType() {
        return type;
    }

    @Override
    public String apply(FfmpegCommand command) {
        return "-b:" + type.apply(command) + " " + bitrate;
    }
}