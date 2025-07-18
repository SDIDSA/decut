package org.luke.decut.ffmpeg.bitrate;


import org.luke.decut.ffmpeg.CommandPart;
import org.luke.decut.ffmpeg.FfmpegCommand;
import org.luke.decut.ffmpeg.core.StreamType;

import java.util.List;

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
    public List<String> apply(FfmpegCommand command) {
        return List.of("-b:" + type.apply(command), bitrate);
    }
}